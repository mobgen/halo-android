package com.mobgen.halo.android.framework.common.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.mobgen.halo.android.framework.common.annotations.Api;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Typical utils used and repeated in all the typical Android applications.
 */
public final class HaloUtils {

    /**
     * Hexadecimal array.
     */
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    /**
     * Blocked constructor for utilities class that has all its methods static.
     */
    private HaloUtils() {
    }

    /**
     * Checks if the application has internet connection. Requires to have the permission
     * android.permission.ACCESS_NETWORK_STATE declared in the manifest.
     *
     * @param context The context where we ask for the network state.
     * @return True if we have connectivity available, false otherwise.
     */
    @Api(1.0)
    public static boolean isNetworkConnected(Context context) {
        boolean networkConnected = false;
        if (context.checkCallingOrSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            networkConnected = info != null && info.isAvailable() && info.isConnected();
        }
        return networkConnected;
    }

    /**
     * Checks if the device is being charged.
     *
     * @param context The context.
     * @param intent The intent for the charging status.
     * @return True if it is plugged, false otherwise.
     */
    @Api(2.0)
    public static boolean isCharging(@NonNull Context context, @Nullable Intent intent) {
        Intent finalIntent = intent;
        if(finalIntent == null){
            finalIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            assert finalIntent != null;
        }
        int plugged = finalIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean isPlugged = plugged == BatteryManager.BATTERY_PLUGGED_AC ||
                plugged == BatteryManager.BATTERY_PLUGGED_USB;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            isPlugged = isPlugged || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS;
        }
        return isPlugged;
    }

    /**
     * Hides the keyboard based on the view which has de focus.
     *
     * @param focusView The view that has the focus.
     */
    @Api(1.0)
    public static void hideKeyboard(@Nullable View focusView) {
        if (focusView != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) focusView.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }
    }

    /**
     * Posts an action on the main thread immediately.
     *
     * @param runnable The action to post.
     */
    @Api(1.0)
    public static void postUI(Runnable runnable) {
        Looper mainLooper = Looper.getMainLooper();
        if (mainLooper != null) {
            new Handler(mainLooper).post(runnable);
        }
    }

    /**
     * Checks if a feature is available for a given version checking if the passed version is greater than the current one.
     *
     * @param sdkVersion The version to check.
     * @return True if it is available, false otherwise.
     */
    @Api(1.0)
    public static boolean isAvailableForVersion(int sdkVersion) {
        return Build.VERSION.SDK_INT >= sdkVersion;
    }

    /**
     * Provides a drawable with an android independent version.
     *
     * @param context  The context.
     * @param resource The resource to take.
     * @return The drawable instance.
     */
    @Api(1.0)
    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(Context context, @DrawableRes int resource) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getResources().getDrawable(resource, null);
        }

        return context.getResources().getDrawable(resource);
    }

    /**
     * Provides the uri for a resource id.
     *
     * @param context    The context used to get the data from the resource.
     * @param resourceId The resource id to check.
     * @return The uri obtained.
     */
    @Api(1.0)
    public static Uri getUriFromResource(Context context, int resourceId) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resourceId) + '/' +
                context.getResources().getResourceTypeName(resourceId) + '/' +
                context.getResources().getResourceEntryName(resourceId));
    }

    /**
     * Helper function used to take flag responses for options in the SDK.
     *
     * @param flags      The flags to check.
     * @param optionFlag The option flag to check against.
     * @return True if the flag is present, false otherwise.
     */
    @Api(1.0)
    public static boolean hasFlag(int flags, int optionFlag) {
        return (flags & optionFlag) > 0;
    }

    /**
     * Provides the argb from string.
     *
     * @param hexadecimal The hexadecimal value in String and a hash.
     * @return The integer value for the argb.
     */
    @Api(1.0)
    public static int getArgb(String hexadecimal) {
        return Color.parseColor(hexadecimal);
    }

    /**
     * Checks if the string provided is a color.
     *
     * @param color The string to check against.
     * @return True if it is a color. False otherwise.
     */
    @Api(1.0)
    public static boolean isColor(String color) {
        try {
            getArgb(color);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Hashes a string using sha-1.
     *
     * @param origin The original string to create the hash.
     * @return The resulting.
     * @throws UnsupportedEncodingException Error encoding.
     * @throws NoSuchAlgorithmException     Error locating sha algorithm.
     */
    @Api(2.0)
    public static String sha1(@NonNull String origin) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(origin.getBytes("iso-8859-1"), 0, origin.length());
        byte[] sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    /**
     * Converst the data into hexadecimal.
     *
     * @param data The bytes to convert.
     * @return The final string.
     */
    @Api(2.0)
    public static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte b : data) {
            int halfByte = (b >>> 4) & 0x0F;
            int twoHalfs = 0;
            do {
                buf.append((0 <= halfByte) && (halfByte <= 9) ? (char) ('0' + halfByte) : (char) ('a' + (halfByte - 10)));
                halfByte = b & 0x0F;
            } while (twoHalfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * Provides the name of an event with the package of the current application.
     *
     * @param context   The context.
     * @param eventName The event name.
     * @return The event created.
     */
    @NonNull
    @Api(1.3)
    public static String getEventName(@NonNull Context context, @NonNull String eventName) {
        return context.getPackageName() + eventName;
    }

    /**
     * Converts a byte array into a string.
     *
     * @param bytes The bytes to convert to string.
     * @return The string converted.
     */
    @Api(2.0)
    public static String bytesToHex(@NonNull byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Converts a string to a byte array. The string have to be in an hexadecimal format.
     *
     * @param data The data to convert.
     * @return The converted byte array.
     */
    @Api(2.0)
    public static byte[] hexToBytes(@NonNull String data) {
        int len = data.length();
        byte[] dataArray = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            dataArray[i / 2] = (byte) ((Character.digit(data.charAt(i), 16) << 4)
                    + Character.digit(data.charAt(i + 1), 16));
        }
        return dataArray;
    }
}
