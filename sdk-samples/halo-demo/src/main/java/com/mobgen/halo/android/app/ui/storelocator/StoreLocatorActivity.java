package com.mobgen.halo.android.app.ui.storelocator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.Store;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.utils.StatusInterceptor;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.search.SearchQueryBuilderFactory;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.sdk.core.management.models.HaloModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Store locator activity that shows some points on a map.
 */
public class StoreLocatorActivity extends MobgenHaloActivity implements OnMapReadyCallback, SingleLocationFetcher.LocationCallback {

    /**
     * Bundle general content module.
     */
    private static final String BUNDLE_GENERAL_CONTENT_MODULE = "store_locator_module_bundle";

    /**
     * Shows the current stores on the map.
     */
    private static final String BUNDLE_STATE_STORE = "stores_bundle";

    /**
     * Store status.
     */
    private static final String BUNDLE_STATE_STORE_STATUS = "stores_bundle_status";

    /**
     * Request permission code.
     */
    private static final int REQUEST_PERMISSION = 1;

    /**
     * The halo module.
     */
    private HaloModule mStoreLocatorModule;

    /**
     * Current stores.
     */
    private HaloResultV2<List<Store>> mStores;

    /**
     * Provides the google map.
     */
    private GoogleMap mMap;

    /**
     * The progress bar.
     */
    private ProgressBar mProgressBar;

    /**
     * The gps manager.
     */
    private SingleLocationFetcher mLocationFetcher;

    /**
     * The status bar.
     */
    private View mStatusBar;

    /**
     * Starts the activity with the module.
     *
     * @param context            The module context.
     * @param storeLocatorModule The store locator module.
     */
    public static void start(@NonNull Context context, @NonNull HaloModule storeLocatorModule) {
        Intent intent = new Intent(context, StoreLocatorActivity.class);
        Bundle data = new Bundle();
        data.putParcelable(BUNDLE_GENERAL_CONTENT_MODULE, storeLocatorModule);
        intent.putExtras(data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the previous state
        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_STATE_STORE)
                && savedInstanceState.containsKey(BUNDLE_STATE_STORE_STATUS)) {
            List<Store> stores = savedInstanceState.getParcelableArrayList(BUNDLE_STATE_STORE);
            HaloStatus status = savedInstanceState.getParcelable(BUNDLE_STATE_STORE_STATUS);
            if (status != null) {
                mStores = new HaloResultV2<>(status, stores);
            }
        }

        //Get the module on finish if opened without the needed data
        if (getIntent().getExtras().containsKey(BUNDLE_GENERAL_CONTENT_MODULE)) {
            mStoreLocatorModule = getIntent().getExtras().getParcelable(BUNDLE_GENERAL_CONTENT_MODULE);
        } else {
            Toast.makeText(this, "You need to provide the halo module to bring the data.", Toast.LENGTH_LONG).show();
            finish();
        }

        //Show what it is needed
        if (!hasPermissions()) {
            openNoPermissions();
        }
        mLocationFetcher = new SingleLocationFetcher(this, this);
    }

    @Override
    public void onPresenterInitialized() {
        super.onPresenterInitialized();
        //Check permissions
        displayMapInfoRequesting();
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.store_locator_title);
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            if (hasPermissions()) {
                updateStores();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mStores != null) {
            outState.putParcelableArrayList(BUNDLE_STATE_STORE, (ArrayList<? extends Parcelable>) mStores.data());
            outState.putParcelable(BUNDLE_STATE_STORE_STATUS, mStores.status());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
        } else {
            mLocationFetcher.enable();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationFetcher.shutdown();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Displays the map information making a request.
     */
    private void displayMapInfoRequesting() {
        if (hasPermissions()) {
            openGrantedPermissionsMap();
            if (mStores == null) {
                updateStores();
            }
        }
    }

    /**
     * Opens the fragment that shows that there is at least one permission not allowed.
     */
    private void openNoPermissions() {
        setContentView(R.layout.activity_store_locator_no_permission);
        findViewById(R.id.bt_open_permissions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                }
            }
        });
    }

    /**
     * Opens the map.
     */
    private void openGrantedPermissionsMap() {
        setContentView(R.layout.activity_store_locator);
        mProgressBar = (ProgressBar) findViewById(R.id.pb_store_map);
        mStatusBar = findViewById(R.id.v_status);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment_store_locator_map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Updates the stores of the store locator
     */
    public void updateStores() {
        startLoading();
        SearchQuery options = SearchQueryBuilderFactory.getPublishedItems(mStoreLocatorModule.getName(),mStoreLocatorModule.getName())
                .onePage(true)
                .segmentWithDevice()
                .build();
        HaloContentApi.with(MobgenHaloApplication.halo())
                .search(Data.NETWORK_AND_STORAGE, options)
                .asContent(Store.class)
                .execute(new CallbackV2<List<Store>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<Store>> result) {
                        stopLoading();
                        HaloStatus status = result.status();
                        if(status.isOk()){
                            List<Store> data = result.data();
                            if (data != null) {
                                mStores = new HaloResultV2<>(status,data);
                                updateMap();
                            }
                        } else {
                            Halog.d(StoreLocatorActivity.this.getClass(), "Error: " + result.status().getExceptionMessage());
                            Toast.makeText(StoreLocatorActivity.this, "Unable to get the store locations", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    /**
     * Checks if it is a valid store.
     *
     * @param store The store to check.
     * @return True if the story is valid. False otherwise.
     */
    private boolean validStore(Store store) {
        return !TextUtils.isEmpty(store.getName()) && store.getLatitude() != null && store.getLongitude() != null;
    }

    /**
     * Updates the stores on to the map;
     */
    private void updateMap() {
        if (mStores != null) {
            List<Store> stores = mStores.data();
            if (mMap != null && stores != null) {
                mMap.clear();
                LatLngBounds.Builder boundsBuilder = LatLngBounds.builder();
                boolean hasStores = false;
                for (Store store : stores) {
                    if (!validStore(store)) continue;
                    LatLng position = new LatLng(store.getLatitude(), store.getLongitude());
                    boundsBuilder.include(position);
                    dropPinEffect(mMap.addMarker(new MarkerOptions()
                            .title(store.getName())
                            .position(position)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))));
                    hasStores = true;
                }
                if (hasStores) {
                    //Move camera
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 200));
                }
            }
            StatusInterceptor.intercept(mStores.status(), mStatusBar);
        }
    }

    /**
     * Animation to drop pins in the map.
     *
     * @param marker The marker animation.
     */
    private void dropPinEffect(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1000;
        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    handler.postDelayed(this, 10);
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateMap();
    }

    @Override
    public void sendNewLocation(Location location) {
        Store nearestStore = getNearestStore(location);
        if (nearestStore != null && mMap != null) {
            CameraPosition cameraPosition = CameraPosition.fromLatLngZoom(new LatLng(nearestStore.getLatitude(), nearestStore.getLongitude()), 15);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            //Keep trying with the next location
            mLocationFetcher.enable();
        }
    }

    @Nullable
    private Store getNearestStore(Location location) {
        Store nearestStore = null;
        float minDistance = Float.MAX_VALUE;
        if (mStores != null) {
            List<Store> stores = mStores.data();
            if (stores != null) {
                for (Store store : stores) {
                    if (!validStore(store)) continue;
                    Location locationStore = new Location("");
                    locationStore.setLatitude(store.getLatitude());
                    locationStore.setLongitude(store.getLongitude());
                    float newDistance = location.distanceTo(locationStore);
                    minDistance = Math.min(newDistance, minDistance);
                    if (minDistance == newDistance) {
                        nearestStore = store;
                    }
                }
            }
        }
        return nearestStore;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    displayMapInfoRequesting();
                    mLocationFetcher.enable();
                } else {
                    openNoPermissions();
                }
            }
        }
    }

    /**
     * Starts the loader.
     */
    private void startLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * Stops the loader.
     */
    private void stopLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    /**
     * Determines if the application has enough permissions to open the map.
     */
    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }
}