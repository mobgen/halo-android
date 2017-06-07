package com.mobgen.fernandosouto.locationpoc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.annotation.NonNull;
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

import com.mobgen.fernandosouto.locationpoc.model.AddLocationMsg;
import com.mobgen.fernandosouto.locationpoc.model.GsmAps;
import com.mobgen.fernandosouto.locationpoc.model.ObserverMsg;
import com.mobgen.fernandosouto.locationpoc.model.PositionMsg;
import com.mobgen.fernandosouto.locationpoc.model.ScanAPResult;
import com.mobgen.fernandosouto.locationpoc.model.WifiAps;
import com.mobgen.fernandosouto.locationpoc.ui.MobgenHaloApplication;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.search.SearchQueryBuilderFactory;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.models.HaloEvent;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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

    List<ScanResult> results;
    WifiManager wifiManager;
    TelephonyManager telephonyManager;
    List<ScanAPResult> scanAPResults = new ArrayList<>();
    List<WifiAps> wifiApsList;
    List<GsmAps> gsmApsList;
    String location = null;
    int ROOM_RADIUS = 10;
    BroadcastObserver broadcastObserver;
    String previousName = "";

    public AccessPointReceiver(WifiManager wifi, TelephonyManager telephony, BroadcastObserver broadcastObserver) {
        wifiManager = wifi;
        telephonyManager = telephony;

        this.broadcastObserver = broadcastObserver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
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

        ScanAPResult apResult = new ScanAPResult(wifiApsList, gsmApsList, getRoomName(getMinimunDistance(results)), location);
        scanAPResults.add(apResult);
        //set oberser msg object
        observerMsg.setScanAPResults(scanAPResults);
        addLocationMsg.setRoomSelection("You will make a fingerprint of room: " + getRoomName(wifiApsList.get(0).getBSSID()));
        observerMsg.setAddLocationMsg(addLocationMsg);
        observerMsg.setPositionMsg(positionMsg);
        observerMsg.setPositionMsg(positionMsg);
        //save on halo
        checkWithHalo(wifiManager, observerMsg);
    }

    private void scanFor3G(TelephonyManager telephony) {
        gsmApsList = new ArrayList<GsmAps>();
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

    private void checkWithHalo(final WifiManager wifi, final ObserverMsg observerMsg) {
        SearchQuery options = SearchQueryBuilderFactory.getPublishedItems("LocationPOC", "LocationPOC")
                .populateAll()
                .build();

        HaloContentApi.with(MobgenHaloApplication.halo())
                .search(Data.NETWORK_AND_STORAGE, options)
                .asContent(ScanAPResult.class)
                .execute(new CallbackV2<List<ScanAPResult>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<ScanAPResult>> haloResultV2) {
                        String currentName = getRealName(haloResultV2.data());
                        observerMsg.getPositionMsg().setDetectedName(currentName);
                        observerMsg.getPositionMsg().setChangeStatus(!currentName.equals(previousName));
                        broadcastObserver.change(observerMsg);
                        //save events on halo when changing room
                        if (!currentName.equals(previousName)) {
                            HashMap<String, String> mapValues = new HashMap<>();
                            mapValues.put("roomName", currentName);
                            mapValues.put("prevRoom", previousName);
                            HaloEvent event = HaloEvent.builder()
                                    .withType(HaloEvent.REGISTER_LOCATION)
                                    .withExtra(mapValues).build();
                            MobgenHaloApplication
                                    .halo()
                                    .manager()
                                    .sendEvent(event)
                                    .execute(new CallbackV2<HaloEvent>() {
                                        @Override
                                        public void onFinish(@NonNull HaloResultV2<HaloEvent> result) {
                                            Halog.v(AccessPointReceiver.class, result.toString());
                                        }
                                    });
                        }
                        previousName = currentName;
                        wifi.startScan();
                    }
                });
    }

    private void getIPFromGoogleAPI() {
        if (location == null) {
            OkHttpClient client = MobgenHaloApplication.halo().framework().network().client().ok();

            try {
                String serialized = WifiAps.serialize(wifiApsList, Halo.instance().framework().parser());
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), serialized);
                Request request = new Request.Builder()
                        .url("https://www.googleapis.com/geolocation/v1/geolocate?key=AIzaSyCoMroBJr37F5oUPcIMNPKVmNnGPhU_gjk")
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

    private String scanWifiAPs(WifiManager wifi) {
        wifiApsList = new ArrayList<>();
        results = wifi.getScanResults();
        //distance.setText("");
        String roomName;
        Collections.sort(results, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult lhs, ScanResult rhs) {
                // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
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
            String resultCalc = "[" + roomName + "]: \n" + "WIFI name: " + s.SSID + "\n=======================\n" +
                    "distance: " +
                    df.format(calculateDistance((double) s.level, s.frequency)) + " ,level: " +
                    s.level +
                    "\n=======================\n";

            wifiStatus = wifiStatus + "\n" + resultCalc;

            WifiAps wifiAps = new WifiAps(s.BSSID, s.SSID, calculateDistance((double) s.level, s.frequency), roomName);
            wifiApsList.add(wifiAps);
        }
        return wifiStatus;
    }

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
        Collections.sort(tempDistanceAvg);
        tempDistanceAvg = new ArrayList<Double>(tempDistanceAvg.subList(0, 2));
        List<Integer> top2Distance = new ArrayList<>();
        top2Distance.add(distancesAvgs.indexOf(tempDistanceAvg.get(0)));
        top2Distance.add(distancesAvgs.indexOf(tempDistanceAvg.get(1)));

        int finalIndex = 0;
        int result1 = 0;
        int result2 = 0;
        if (tempDistanceAvg.get(1) - tempDistanceAvg.get(0) < ROOM_RADIUS && tempDistanceAvg.get(0) > ROOM_RADIUS) {
            //middle point
            finalIndex = -1;//the first
            result1 = top2Distance.get(0);
            result2 = top2Distance.get(1);
        } else {
            finalIndex = top2Distance.get(0);
        }

        if (finalIndex != -1) {
            return haloResult.get(finalIndex).getRoomName();
        } else {
            return "Between" + haloResult.get(result1).getRoomName() + " AND: " + haloResult.get(result2).getRoomName();
        }
    }

    private String getMinimunDistance(List<ScanResult> results) {
        double min = calculateDistance((double) results.get(0).level, results.get(0).frequency);
        int minIndex = 0;
        for (int i = 0; i < results.size(); i++) {
            if (calculateDistance((double) results.get(i).level, results.get(i).frequency) < min) {
                minIndex = i;
            }
        }
        return results.get(minIndex).BSSID;
    }

    @NonNull
    private String getRoomName(String bssid) {
        String room;
        if (bssid.equals("24:a4:3c:b2:5d:3d") || bssid.equals("2a:a4:3c:b2:5d:3d")
                || bssid.equals("2a:a4:3c:b1:5d:3d") || bssid.equals("24:a4:3c:b1:5d:3d")) {
            room = "Cocina";
        } else if (bssid.equals("f8:fb:56:13:ca:bd") || bssid.equals("14:91:82:64:4d:fe")) {
            room = "Cocina meeting";
        } else if (bssid.equals("24:a4:3c:b1:61:01")) {
            room = "Ping Pong";
        } else if (bssid.equals("24:a4:3c:b1:5f:83") || bssid.equals("2a:a4:3c:b1:5f:83")
                || bssid.equals("14:91:82:64:4d:fe") || bssid.equals("18:d6:c7:d5:18:82")) {
            room = "Cueva";
        } else if (bssid.equals("04:18:d6:81:e8:eb") || bssid.equals("0a:18:d6:81:e8:eb")) {
            room = "Entrada Shell";
        } else if (bssid.equals("44:d9:e7:91:75:16") || bssid.equals("4a:d9:e7:91:75:16")) {
            room = "Medio Shell";
        } else if (bssid.equals("24:a4:3c:b2:5e:ef") || bssid.equals("2a:a4:3c:b0:5e:ef")
                || bssid.equals("00:4a:77:60:f9:3e")) {
            room = "Fondo Shell";
        } else if (bssid.equals("04:18:d6:81:e8:96") || bssid.equals("04:18:d6:82:e8:96") || bssid.equals("0a:18:d6:81:e8:96") || bssid.equals("0a:18:d6:82:e8:96")) {
            room = "2B";
        } else if (bssid.equals("ac:bc:32:ed:91:6d")) {
            room = "2C";
        } else if (bssid.equals("04:18:d6:81:e9:1c") || bssid.equals("0a:18:d6:81:e9:1c")) {
            room = "2C Meeting";
        } else if (bssid.equals("04:18:d6:81:e9:74") || bssid.equals("0a:18:d6:81:e9:74")
                || bssid.equals("04:18:d6:82:e9:74") || bssid.equals("0a:18:d6:82:e9:74")
                || bssid.equals("04:18:b6:82:e9:74") || bssid.equals("0a:18:b6:82:e9:74")) {
            room = "3B";
        } else if (bssid.equals("04:18:d6:81:e9:02") || bssid.equals("0a:18:d6:82:e9:02")) {
            room = "3C";
        } else {
            room = "outOfMobgenRooms";
        }
        return room;
    }

    public double calculateDistance(double levelInDb, double freqInMHz) {
        double exp = (27.55 - (20 * Math.log10(freqInMHz)) + abs(levelInDb)) / 20.0;
        return Math.pow(10.0, exp);
    }

}
