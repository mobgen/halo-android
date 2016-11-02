package com.mobgen.halo.android.app.ui.storelocator;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;

import com.mobgen.halo.android.framework.common.helpers.logger.Halog;


/**
 * Gps manager that tries to get the current user location.
 */
public class SingleLocationFetcher implements LocationListener {

    /**
     * Callback call to receive the location.
     */
    public interface LocationCallback {
        /**
         * The new location saving callback.
         *
         * @param location The location obtained.
         */
        void sendNewLocation(Location location);
    }

    /**
     * The location manager.
     */
    private LocationManager mLocationManager;

    /**
     * The callback.
     */
    private LocationCallback mCallback;

    /**
     * Determines if the manager is enabled or not.
     */
    private boolean mEnabled;

    /**
     * Constructor to keep locations.
     *
     * @param context  The context.
     * @param callback The current callback.
     */
    public SingleLocationFetcher(Context context, LocationCallback callback) {
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        mCallback = callback;
    }

    /**
     * Enables the location fetcher.
     */
    public void enable() {
        if (!mEnabled) {
            if (mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.myLooper());
            }
            if (mLocationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER)) {
                mLocationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, this, Looper.myLooper());
            }
            mEnabled = true;
            Halog.d(getClass(), "Location service started");
        }
    }

    public void shutdown() {
        if (mEnabled) {
            mLocationManager.removeUpdates(this);
            mLocationManager.removeUpdates(this);
            mEnabled = false;
            Halog.d(getClass(), "Location service stopped");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (mCallback != null) {
            mCallback.sendNewLocation(location);
        }
        shutdown();
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
