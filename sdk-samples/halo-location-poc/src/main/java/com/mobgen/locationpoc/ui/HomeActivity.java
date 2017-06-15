package com.mobgen.locationpoc.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mobgen.locationpoc.R;
import com.mobgen.locationpoc.model.Friend;
import com.mobgen.locationpoc.model.ObserverMsg;
import com.mobgen.locationpoc.model.ScanAPResult;
import com.mobgen.locationpoc.receiver.AccessPointReceiver;
import com.mobgen.locationpoc.receiver.BroadcastObserver;
import com.mobgen.halo.android.content.edition.HaloContentEditApi;
import com.mobgen.halo.android.content.models.BatchOperationResults;
import com.mobgen.halo.android.content.models.BatchOperations;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.locationpoc.ui.fingerprint.AddLocationFragment;
import com.mobgen.locationpoc.ui.friends.AllFriendsActivity;
import com.mobgen.locationpoc.ui.friends.FriendsFragment;
import com.mobgen.locationpoc.ui.heatmap.HeatRegionFragment;
import com.mobgen.locationpoc.ui.location.PositionFragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class HomeActivity extends AppCompatActivity implements Observer, LocationListener {

    private static final int REQUEST_LOCATION_PERMS = 12;
    private static final int REQUEST_PHONE_PERMS = 13;
    private final static int FIVE_MINUTES_MS = 5 * 60 * 1000;

    private BottomNavigationView bottomNavigationView;
    private AddLocationFragment mAddLocationFragment;
    private PositionFragment mPositionFragment;
    private FriendsFragment mFriendsFragment;
    private HeatRegionFragment mHeatRegionFragment;
    private BroadcastObserver mBroadcastObserver;
    List<ScanAPResult> scanAPResults = new ArrayList<>();
    private FloatingActionButton fab;
    private AccessPointReceiver mAccessPointReceiver;
    private WifiManager wifi;
    private TelephonyManager telephony;
    private LocationManager locationManager;
    private int currentFragment = -1;
    private Activity mActivity = this;
    private Context mContext = this;


    public static void start(@NonNull Context context) {
        Intent intent = new Intent(context, HomeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActivity = this;
        mContext = this;

        wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        telephony = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mBroadcastObserver = new BroadcastObserver();
        mBroadcastObserver.addObserver(this);
        mAccessPointReceiver = new AccessPointReceiver(wifi, telephony, mBroadcastObserver);
        registerReceiver(mAccessPointReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifi.startScan();

        fab = (FloatingActionButton) findViewById(R.id.fab);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_add_location:
                                changeFragment(1);
                                currentFragment = 1;
                                break;
                            case R.id.action_position:
                                changeFragment(0);
                                currentFragment = 0;
                                break;
                            case R.id.action_friends:
                                changeFragment(2);
                                currentFragment = 2;
                                break;
                            case R.id.action_heat:
                                changeFragment(3);
                                currentFragment = 3;
                                break;
                        }
                        return true;
                    }
                });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentFragment == 1) {
                    sendFingerPrint();
                } else if (currentFragment == 2) {
                    listFriends(mFriendsFragment.getUniqueFriendList());
                } else if(currentFragment == 3){
                    mHeatRegionFragment.startHeatAnimation();
                }
            }
        });

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, FIVE_MINUTES_MS, 0, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, FIVE_MINUTES_MS, 0, this);
        }
    }

    /**
     * Send a new fingerprint
     */
    private void sendFingerPrint() {
        //send scan result to halo
        if (scanAPResults.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(getString(R.string.dialog_title));
            final EditText roomSelection = new EditText(mContext);
            roomSelection.setText(scanAPResults.get(0).getRoomName());
            roomSelection.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(roomSelection);

            builder.setPositiveButton(getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    HaloContentEditApi.with(MobgenHaloApplication.halo())
                            .batch(getInstancesToBatch(roomSelection.getText().toString()), true)
                            .execute(new CallbackV2<BatchOperationResults>() {
                                @Override
                                public void onFinish(@NonNull HaloResultV2<BatchOperationResults> haloResultV2) {
                                    Toast.makeText(mContext, getString(R.string.dialog_result_ok),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });

            builder.show();
        } else {
            Toast.makeText(mContext, getString(R.string.dialog_result_ko),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Show unique friends list.
     *
     * @param uniqueFriends
     */
    private void listFriends(List<Friend> uniqueFriends) {
        AllFriendsActivity.start(mContext, uniqueFriends);
    }

    @Override
    public void onResume() {
        super.onResume();
        //get permission for location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMS);
            changeFragment(1);
        } else {
            if (currentFragment == -1) {
                changeFragment(0);
            }
        }
        //get permission for phone state
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONE_PERMS);
        }
    }

    @Override
    public void onPause() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
            locationManager = null;
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mAccessPointReceiver);
        mBroadcastObserver.deleteObservers();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //show map
                    changeFragment(0);
                } else {
                    //show wifi list
                    changeFragment(1);
                }
            }
        }
    }

    /**
     * get instances to proceed with the batch operation
     *
     * @return The batch operations
     */
    @Nullable
    public BatchOperations getInstancesToBatch(String roomName) {
        BatchOperations.Builder operations = BatchOperations.builder();
        for (int i = 0; i < scanAPResults.size(); i++) {
            //add one instance with every image which has been selected
            Date now = new Date();
            HaloContentInstance instance = new HaloContentInstance.Builder(AccessPointReceiver.MODULE_NAME)
                    .withAuthor(AccessPointReceiver.AUTHOR_NAME)
                    .withContentData(scanAPResults.get(i))
                    .withName(roomName)
                    .withCreationDate(now)
                    .withPublishDate(now)
                    .withModuleId(AccessPointReceiver.MODULE_ID)
                    .build();
            operations.createOrUpdate(instance);
        }
        return operations.build();
    }


    /**
     * To load fragments
     *
     * @param position menu index
     */
    private void changeFragment(int position) {

        Fragment newFragment = null;

        if (position == 0) {
            if (mPositionFragment == null) {
                mPositionFragment = PositionFragment.newInstance(mBroadcastObserver);
            }
            newFragment = mPositionFragment;
            fab.setVisibility(View.GONE);
        } else if (position == 1) {
            if (mAddLocationFragment == null) {
                mAddLocationFragment = AddLocationFragment.newInstance(mBroadcastObserver);
            }
            newFragment = mAddLocationFragment;
            fab.setImageResource(R.drawable.ic_backup_black_24dp);
            fab.setVisibility(View.VISIBLE);
        } else if (position == 2) {
            if (mFriendsFragment == null) {
                mFriendsFragment = FriendsFragment.newInstance(mBroadcastObserver);
            }
            newFragment = mFriendsFragment;
            fab.setImageResource(R.drawable.ic_people_black_24dp);
            fab.setVisibility(View.VISIBLE);
        } else if (position == 3) {
            if (mHeatRegionFragment == null) {
                mHeatRegionFragment = HeatRegionFragment.newInstance();
            }
            newFragment = mHeatRegionFragment;
            fab.setImageResource(R.drawable.ic_video_library_black_24dp);
            fab.setVisibility(View.VISIBLE);
        }

        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragmentContainer, newFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            wifi.startScan();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void update(Observable o, Object result) {
        if (result instanceof ObserverMsg) {
            scanAPResults = ((ObserverMsg) result).getScanAPResults();
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
