package com.mobgen.locationpoc.receiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;

import com.mobgen.halo.android.content.edition.HaloContentEditApi;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.locationpoc.R;
import com.mobgen.locationpoc.model.AddLocationMsg;
import com.mobgen.locationpoc.model.GsmAps;
import com.mobgen.locationpoc.model.ObserverMsg;
import com.mobgen.locationpoc.model.PositionMsg;
import com.mobgen.locationpoc.model.ScanAPResult;
import com.mobgen.locationpoc.model.WifiAps;
import com.mobgen.locationpoc.ui.LoginActivity;
import com.mobgen.locationpoc.ui.MobgenHaloApplication;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.search.SearchQueryBuilderFactory;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.models.HaloEvent;
import com.mobgen.locationpoc.utils.LocationUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.Math.abs;

/**
 * Created by f.souto.gonzalez on 01/06/2017.
 */
public class AccessPointReceiver extends BroadcastReceiver {

    public final static String MODULE_NAME = "LocationPOC";
    public final static String MODULE_NAME_FRIENDS = "FriendsLocationPOC";
    public static final String AUTHOR_NAME = "Location POC";
    public static final String MODULE_ID = "592bed5194bca6001162a16a";
    public static final String MODULE_FRIENDS_ID = "593913330cb1f9001e3b99c6";
    private static String googleAPI = "https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyCoMroBJr37F5oUPcIMNPKVmNnGPhU_gjk";
    private final static String CURRENT_ROOM = "roomName";
    private final static String PREV_ROOM = "prevRoom";
    private final static int ROOM_RADIUS = 10;

    private List<ScanResult> results;
    private WifiManager wifiManager;
    private TelephonyManager telephonyManager;
    private List<ScanAPResult> scanAPResults = new ArrayList<>();
    private List<WifiAps> wifiApsList;
    private List<GsmAps> gsmApsList;
    private String location = null;

    private BroadcastObserver broadcastObserver;
    private String previousName = "";
    private Context mContext;
    private HaloResultV2<List<ScanAPResult>> haloRoomResult = null;

    public AccessPointReceiver(WifiManager wifi, TelephonyManager telephony, BroadcastObserver broadcastObserver) {
        wifiManager = wifi;
        telephonyManager = telephony;

        this.broadcastObserver = broadcastObserver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        scanAPResults = new ArrayList<>();
        //set Observer objects
        ObserverMsg observerMsg = new ObserverMsg();
        AddLocationMsg addLocationMsg = new AddLocationMsg();
        PositionMsg positionMsg = new PositionMsg();
        //scan wifis
        addLocationMsg.setWifiStatus(scanWifiAPs(wifiManager));
        //take a ip from google
        getIPFromGoogleAPI();
        //scan for 3G
        scanFor3G(telephonyManager);
        //set result of the scan process
        ScanAPResult apResult = new ScanAPResult(wifiApsList, gsmApsList, getRoomName(getMinimunDistance(results)), location);
        scanAPResults.add(apResult);
        //set obeserver msg object
        observerMsg.setScanAPResults(scanAPResults);
        if (wifiApsList.size() > 0) {
            addLocationMsg.setRoomSelection(mContext.getString(R.string.selection_result) + " " + getRoomName(wifiApsList.get(0).getBSSID()));
        } else {
            addLocationMsg.setRoomSelection(mContext.getString(R.string.selection_result) + previousName);
        }
        observerMsg.setAddLocationMsg(addLocationMsg);
        observerMsg.setPositionMsg(positionMsg);
        //check name with halo
        checkWithHalo(wifiManager, observerMsg);
    }

    /**
     * Scan 3G/4G/GSM data
     *
     * @param telephony
     */
    private void scanFor3G(TelephonyManager telephony) {
        gsmApsList = new ArrayList<GsmAps>();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            List<CellInfo> infos = telephony.getAllCellInfo();
            telephony.getCellLocation();
            for (int i = 0; i < infos.size(); ++i) {
                try {
                    CellInfo info = infos.get(i);
                    GsmAps gsmAps = null;
                    if (info instanceof CellInfoGsm) {
                        CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                        CellIdentityGsm identityGsm = ((CellInfoGsm) info).getCellIdentity();
                        gsmAps = new GsmAps(identityGsm.getLac(), identityGsm.getMcc(), gsm.getDbm());
                    } else if (info instanceof CellInfoLte) {
                        CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                        CellIdentityLte identityLte = ((CellInfoLte) info).getCellIdentity();
                        gsmAps = new GsmAps(identityLte.getTac(), identityLte.getMcc(), lte.getDbm());
                    } else if (info instanceof CellInfoWcdma) {
                        CellSignalStrengthWcdma wcdma = ((CellInfoWcdma) info).getCellSignalStrength();
                        CellIdentityWcdma identityWcdma = ((CellInfoWcdma) info).getCellIdentity();
                        gsmAps = new GsmAps(identityWcdma.getLac(), identityWcdma.getMcc(), wcdma.getDbm());
                    }
                    gsmApsList.add(gsmAps);

                } catch (Exception ex) {
                    gsmApsList = null;
                }
            }
        }
    }

    /**
     * Check all wifis with halo stored ones.
     *
     * @param wifi
     * @param observerMsg
     */
    private void checkWithHalo(final WifiManager wifi, final ObserverMsg observerMsg) {
        //we do not check every time with halo room only once per execution
        if(haloRoomResult != null){
            notifyAndScan(haloRoomResult, observerMsg, wifi);
        } else {
            SearchQuery options = SearchQueryBuilderFactory.getPublishedItems(MODULE_NAME, MODULE_NAME)
                    .populateAll()
                    .build();

            HaloContentApi.with(MobgenHaloApplication.halo())
                    .search(Data.NETWORK_AND_STORAGE, options)
                    .asContent(ScanAPResult.class)
                    .execute(new CallbackV2<List<ScanAPResult>>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<List<ScanAPResult>> haloResultV2) {
                            haloRoomResult = haloResultV2;
                            notifyAndScan(haloResultV2, observerMsg, wifi);
                        }
                    });
        }
    }

    /**
     * Notify observer save on halo and restart scan
     * @param haloResultV2
     * @param observerMsg
     * @param wifi
     */
    private void notifyAndScan(@NonNull HaloResultV2<List<ScanAPResult>> haloResultV2, ObserverMsg observerMsg, WifiManager wifi) {
        String currentName = getRealName(haloResultV2.data());
        observerMsg.getPositionMsg().setDetectedName(currentName);
        observerMsg.getPositionMsg().setChangeStatus(!currentName.equals(previousName));
        broadcastObserver.change(observerMsg);
        //save events on halo when changing room
        if (!currentName.equals(previousName)) {
            //user data
            String userName = MobgenHaloApplication.halo()
                    .getCore().manager().storage()
                    .prefs().getString(LoginActivity.USER_NAME, "");
            String userMail = MobgenHaloApplication.halo()
                    .getCore().manager().storage()
                    .prefs().getString(LoginActivity.USER_MAIL, "");
            String userPhoto = MobgenHaloApplication.halo()
                    .getCore().manager().storage()
                    .prefs().getString(LoginActivity.USER_PHOTO, "https://mobgen.github.io/halo-documentation/images/halo-home.png");
            //location data
            Location location = LocationUtils.getLocation(mContext);
            String locationStringify = null;
            Double longitude = null;
            Double latitude = null;
            if (location != null) {
                locationStringify = location.getLatitude() + "," + location.getLongitude();
                longitude = location.getLongitude();
                latitude = location.getLatitude();
            }
            //event data
            HashMap<String, String> eventValues = new HashMap<>();
            eventValues.put(CURRENT_ROOM, currentName);
            eventValues.put(PREV_ROOM, previousName);
            eventValues.put(LoginActivity.USER_NAME,userName);
            eventValues.put(LoginActivity.USER_MAIL,userMail);
            //send event
            HaloEvent event = HaloEvent.builder()
                    .withType(HaloEvent.REGISTER_LOCATION)
                    .withLocation(locationStringify)
                    .withExtra(eventValues).build();
            MobgenHaloApplication
                    .halo()
                    .manager()
                    .sendEvent(event)
                    .execute();
            //instance data
            HashMap<String, Object> instanceValues = new HashMap<>();
            instanceValues.put("room", currentName);
            instanceValues.put("photo", userPhoto);
            instanceValues.put("name",userName);
            instanceValues.put("mail",userMail);
            instanceValues.put("longitude",longitude);
            instanceValues.put("latitude",latitude);
            Date now = new Date();
            //save data instance
            HaloContentInstance newPosition = new HaloContentInstance.Builder(MODULE_NAME_FRIENDS)
                    .withAuthor(AUTHOR_NAME)
                    .withContentData(instanceValues)
                    .withName(userName)
                    .withCreationDate(now)
                    .withPublishDate(now)
                    .withModuleId(MODULE_FRIENDS_ID)
                    .build();
            HaloContentEditApi.with(MobgenHaloApplication.halo())
                    .addContent(newPosition)
                    .execute();
        }
        previousName = currentName;
        wifi.startScan();
    }

    /**
     * Get IP from google api service sending wifi aps.
     */
    private void getIPFromGoogleAPI() {
        if (location == null) {
            OkHttpClient client = MobgenHaloApplication.halo().framework().network().client().ok();

            try {
                String serialized = WifiAps.serialize(wifiApsList, Halo.instance().framework().parser());
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), serialized);
                Request request = new Request.Builder()
                        .url(googleAPI)
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //get response
                        location = response.body().string();
                    }
                });
            } catch (HaloParsingException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Get all wifi aps
     *
     * @param wifi
     * @return
     */
    private String scanWifiAPs(WifiManager wifi) {
        wifiApsList = new ArrayList<>();
        results = wifi.getScanResults();
        String roomName;
        Collections.sort(results, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                double lDistance = calculateDistance((double) lhs.level, lhs.frequency);
                double rDistance = calculateDistance((double) rhs.level, rhs.frequency);
                return lDistance > rDistance ? -1 : (lDistance < rDistance) ? 1 : 0;
            }
        });
        Collections.reverse(results);
        int i = 0;
        String wifiStatus = "";
        for (ScanResult s : results) {
            roomName = getRoomName(s.BSSID);
            //wifi status to print
            DecimalFormat df = new DecimalFormat("#.##");
            String resultCalc = "[" + roomName + "]: \n" + mContext.getString(R.string.scan_print_1) + " " + s.SSID + "\n=======================\n" +
                    mContext.getString(R.string.scan_print_2) + " " +
                    df.format(calculateDistance((double) s.level, s.frequency)) + " " + mContext.getString(R.string.scan_print_3) + " " +
                    s.level +
                    "\n=======================\n";

            wifiStatus = wifiStatus + "\n" + resultCalc;

            WifiAps wifiAps = new WifiAps(s.BSSID, s.SSID, calculateDistance((double) s.level, s.frequency), roomName);
            wifiApsList.add(wifiAps);
        }
        return wifiStatus;
    }


    /**
     * Get the real name of the room based all aps from halo
     *
     * @param haloResult
     * @return
     */
    public String getRealName(List<ScanAPResult> haloResult) {
        List<Double> numberOfCoincidence = new ArrayList<>();
        //coincidences
        for (int j = 0; j < haloResult.size(); j++) {
            double coincidence = 0;
            numberOfCoincidence.add(coincidence);
            for (int k = 0; k < haloResult.get(j).getWifiAps().size(); k++) {
                for (int i = 0; i < wifiApsList.size(); i++) {
                    if (wifiApsList.get(i).getBSSID().equals(haloResult.get(j).getWifiAps().get(k).getBSSID())) {
                        double diff = wifiApsList.get(i).getDistance() - haloResult.get(j).getWifiAps().get(k).getDistance();
                        haloResult.get(j).getWifiAps().get(k).setDifference(diff);
                        coincidence++;
                        numberOfCoincidence.set(j, coincidence / (double) wifiApsList.size());
                    }
                }
            }
        }

        //get] all distance diffs
        List<Double> distancesAvgs = new ArrayList<>();
        for (int i = 0; i < haloResult.size(); i++) {
            double avg = 0;
            for (int k = 0; k < haloResult.get(i).getWifiAps().size(); k++) {
                avg = avg + abs(haloResult.get(i).getWifiAps().get(k).getDifference());
            }
            avg = (avg / haloResult.get(i).getWifiAps().size()) * (1 - numberOfCoincidence.get(i));
            distancesAvgs.add(avg);
        }

        //solving ecuation
        List<Double> tempDistanceAvg = new ArrayList<Double>(distancesAvgs);
        int finalIndex = 0;
        int result1 = 0;
        int result2 = 0;
        if (distancesAvgs.size() > 0) {
            Collections.sort(tempDistanceAvg);
            tempDistanceAvg = new ArrayList<Double>(tempDistanceAvg.subList(0, 2));
            List<Integer> top2Distance = new ArrayList<>();
            top2Distance.add(distancesAvgs.indexOf(tempDistanceAvg.get(0)));
            top2Distance.add(distancesAvgs.indexOf(tempDistanceAvg.get(1)));

            if (tempDistanceAvg.get(1) - tempDistanceAvg.get(0) < ROOM_RADIUS && tempDistanceAvg.get(0) > ROOM_RADIUS) {
                //middle point
                finalIndex = -1;//the first
                result1 = top2Distance.get(0);
                result2 = top2Distance.get(1);
            } else {
                finalIndex = top2Distance.get(0);
            }
        }

        //return result
        if (finalIndex != -1) {
            return haloResult.get(finalIndex).getRoomName();
        } else {
            return mContext.getString(R.string.scan_between_1) + " " + haloResult.get(result1).getRoomName()
                    + " " + mContext.getString(R.string.scan_between_2) + " " + haloResult.get(result2).getRoomName();
        }
    }

    /**
     * Get min distance to a ap
     *
     * @param results
     * @return
     */
    private String getMinimunDistance(List<ScanResult> results) {
        if (results.size() > 0) {
            double min = calculateDistance((double) results.get(0).level, results.get(0).frequency);
            int minIndex = 0;
            for (int i = 0; i < results.size(); i++) {
                if (calculateDistance((double) results.get(i).level, results.get(i).frequency) < min) {
                    minIndex = i;
                }
            }
            return results.get(minIndex).BSSID;
        } else {
            return "";
        }
    }

    /**
     * Get a friendly room name based on mac
     *
     * @param bssid
     * @return
     */
    @NonNull
    private String getRoomName(String bssid) {
        String room = "";
        if (bssid.equals("24:a4:3c:b2:5d:3d") || bssid.equals("2a:a4:3c:b2:5d:3d")
                || bssid.equals("2a:a4:3c:b1:5d:3d") || bssid.equals("24:a4:3c:b1:5d:3d")) {
            room = mContext.getString(R.string.room_kitchen);
        } else if (bssid.equals("f8:fb:56:13:ca:bd") || bssid.equals("14:91:82:64:4d:fe")) {
            room = mContext.getString(R.string.room_kitchen_meeting);
        } else if (bssid.equals("24:a4:3c:b1:61:01")) {
            room = mContext.getString(R.string.room_ping_pong);
        } else if (bssid.equals("24:a4:3c:b1:5f:83") || bssid.equals("2a:a4:3c:b1:5f:83")
                || bssid.equals("14:91:82:64:4d:fe") || bssid.equals("18:d6:c7:d5:18:82")) {
            room = mContext.getString(R.string.room_cave);
        } else if (bssid.equals("04:18:d6:81:e8:eb") || bssid.equals("0a:18:d6:81:e8:eb")) {
            room = mContext.getString(R.string.room_shell_1);
        } else if (bssid.equals("44:d9:e7:91:75:16") || bssid.equals("4a:d9:e7:91:75:16")) {
            room = mContext.getString(R.string.room_shell_2);
        } else if (bssid.equals("24:a4:3c:b2:5e:ef") || bssid.equals("2a:a4:3c:b0:5e:ef")
                || bssid.equals("00:4a:77:60:f9:3e")) {
            room = mContext.getString(R.string.room_shell_3);
        } else if (bssid.equals("04:18:d6:81:e8:96") || bssid.equals("04:18:d6:82:e8:96") || bssid.equals("0a:18:d6:81:e8:96") || bssid.equals("0a:18:d6:82:e8:96")) {
            room = mContext.getString(R.string.room_2b);
        } else if (bssid.equals("ac:bc:32:ed:91:6d")) {
            room = mContext.getString(R.string.room_2c);
        } else if (bssid.equals("04:18:d6:81:e9:1c") || bssid.equals("0a:18:d6:81:e9:1c")) {
            room = mContext.getString(R.string.room_2c_meeting);
        } else if (bssid.equals("04:18:d6:81:e9:74") || bssid.equals("0a:18:d6:81:e9:74")
                || bssid.equals("04:18:d6:82:e9:74") || bssid.equals("0a:18:d6:82:e9:74")
                || bssid.equals("04:18:b6:82:e9:74") || bssid.equals("0a:18:b6:82:e9:74")) {
            room = mContext.getString(R.string.room_3b);
        } else if (bssid.equals("04:18:d6:81:e9:02") || bssid.equals("0a:18:d6:82:e9:02")) {
            room = mContext.getString(R.string.room_3c);
        } else {
            room = mContext.getString(R.string.room_out);
        }
        return room;
    }

    /**
     * Calculate the distance of the ap
     *
     * @param levelInDb
     * @param freqInMHz
     * @return The distance
     */
    public double calculateDistance(double levelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + abs(levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

}
