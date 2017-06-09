package com.mobgen.locationpoc.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

/**
 * Created by f.souto.gonzalez on 07/06/2017.
 */

public class LocationUtils {

    private final static long FIVE_MINUTES_MS = 5 * 60 * 1000;

    /**
     * Get current location.
     *
     * @return The location.
     */
    @Nullable
    public static Location getLocation(Context context) {
        if (context != null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return null;
                }
                Location lastKnowLocationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location lastKnowLocationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (lastKnowLocationGPS != null && ageMinutes(lastKnowLocationGPS) < FIVE_MINUTES_MS) {
                    return lastKnowLocationGPS;
                } else if (lastKnowLocationNet != null && ageMinutes(lastKnowLocationNet) < FIVE_MINUTES_MS) {
                    return lastKnowLocationNet;
                } else {
                    return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                }
            }
        }
        return null;
    }

    /**
     * The age of the last fix.
     *
     * @param last
     * @return
     */
    private static long ageMinutes(Location last) {
        return System.currentTimeMillis() - last.getTime();
    }

}
