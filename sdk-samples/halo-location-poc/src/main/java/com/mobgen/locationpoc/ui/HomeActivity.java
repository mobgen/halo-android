package com.mobgen.locationpoc.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
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
import com.mobgen.locationpoc.model.ObserverMsg;
import com.mobgen.locationpoc.model.ScanAPResult;
import com.mobgen.locationpoc.receiver.AccessPointReceiver;
import com.mobgen.locationpoc.receiver.BroadcastObserver;
import com.mobgen.halo.android.auth.HaloAuthApi;
import com.mobgen.halo.android.auth.models.HaloAuthProfile;
import com.mobgen.halo.android.auth.models.IdentifiedUser;
import com.mobgen.halo.android.auth.providers.SocialNotAvailableException;
import com.mobgen.halo.android.content.edition.HaloContentEditApi;
import com.mobgen.halo.android.content.models.BatchOperationResults;
import com.mobgen.halo.android.content.models.BatchOperations;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class HomeActivity extends AppCompatActivity implements Observer {

    private BottomNavigationView bottomNavigationView;
    private AddLocationFragment mAddLocationFragment;
    private PositionFragment mPositionFragment;
    private BroadcastObserver mBroadcastObserver;
    List<ScanAPResult> scanAPResults = new ArrayList<>();
    private FloatingActionButton fab;
    private WifiManager wifi;
    private TelephonyManager telephony;
    private static final int REQUEST_LOCATION_PERMS = 12;
    private static final int REQUEST_PHONE_PERMS = 13;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Activity context = this;
        Context context1 = this;

        wifi = (WifiManager) context1.getSystemService(Context.WIFI_SERVICE);
        telephony = (TelephonyManager) context1.getSystemService(Context.TELEPHONY_SERVICE);
        mBroadcastObserver = new BroadcastObserver();
        mBroadcastObserver.addObserver(this);
        AccessPointReceiver accessPointReceiver = new AccessPointReceiver(wifi, telephony, mBroadcastObserver);
        registerReceiver(accessPointReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
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
                                break;
                            case R.id.action_position:
                                changeFragment(0);
                                break;
                        }
                        return true;
                    }
                });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //send scan result to halo
                if (scanAPResults.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Select the name of the room");
                    final EditText roomSelection = new EditText(context);
                    roomSelection.setText(scanAPResults.get(0).getRoomName());
                    roomSelection.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(roomSelection);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HaloContentEditApi.with(MobgenHaloApplication.halo())
                                    .batch(getInstancesToBatch(roomSelection.getText().toString()), true)
                                    .execute(new CallbackV2<BatchOperationResults>() {
                                        @Override
                                        public void onFinish(@NonNull HaloResultV2<BatchOperationResults> haloResultV2) {
                                            Toast.makeText(context, "We sent the data to HALO",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });

                    builder.show();
                } else {
                    Toast.makeText(context, "Sorry you need to detect WIFI APs",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        //get permission for location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_PERMS);
            changeFragment(1);
        } else {
            changeFragment(0);
        }
        //get permission for phone state
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},REQUEST_PHONE_PERMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMS: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
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
            HaloContentInstance instance = new HaloContentInstance.Builder("LocationPOC")
                    .withAuthor("Location POC")
                    .withContentData(scanAPResults.get(i))
                    .withName(roomName)
                    .withCreationDate(now)
                    .withPublishDate(now)
                    .withModuleId("592bed5194bca6001162a16a")
                    .build();
            operations.createOrUpdate(instance);
        }
        return operations.build();
    }


    /**
     * To load fragments for sample
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
            fab.setVisibility(View.VISIBLE);
        }

        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragmentContainer, newFragment)
                .commit();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_login) {
            try {
                HaloAuthProfile auth = new HaloAuthProfile("halo@team.halo", "qwe12qwe");
                MobgenHaloApplication.getAuth().loginWithHalo(HaloAuthApi.SOCIAL_HALO, auth, new CallbackV2<IdentifiedUser>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<IdentifiedUser> result) {
                        changeFragment(0);
                    }
                });
            } catch (SocialNotAvailableException e) {
            }
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
}
