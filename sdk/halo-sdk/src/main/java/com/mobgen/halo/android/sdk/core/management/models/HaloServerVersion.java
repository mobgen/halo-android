package com.mobgen.halo.android.sdk.core.management.models;

import android.support.annotation.IntDef;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Representation of the server version.
 */
@Keep
@JsonObject
public class HaloServerVersion {

    /**
     * Checks if the version of the sdk is valid for the server one.
     */
    @IntDef({VALID, OUTDATED, NOT_CHECKED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface VersionCheck {
    }

    /**
     * The version is valid for the server.
     */
    public static final int VALID = 1;
    /**
     * The version is outdated.
     */
    public static final int OUTDATED = 2;
    /**
     * Couldn't check the version for some reason.
     */
    public static final int NOT_CHECKED = 3;

    /**
     * Provides the min android version required for android.
     */
    @JsonField(name = "minAndroid")
    String mMinHaloVersion;
    /**
     * Provides the changelog url to show it to the device.
     */
    @JsonField(name = "androidChangeLog")
    String mChangeLogUrl;

    /**
     * Default constructor.
     */
    public HaloServerVersion() {
        //Constructor for Gson.
    }

    /**
     * Constructor for the server version.
     *
     * @param changelogUrl      The changelog url.
     * @param minAndroidVersion The min android version.
     */
    public HaloServerVersion(@Nullable String changelogUrl, @Nullable String minAndroidVersion) {
        mChangeLogUrl = changelogUrl;
        mMinHaloVersion = minAndroidVersion;
    }

    /**
     * Provides the minimum android version available for Android.
     *
     * @return The halo version.
     */
    @Api(1.2)
    @Nullable
    public String getHaloVersion() {
        return mMinHaloVersion;
    }

    /**
     * The url of the changelog for the last version.
     *
     * @return The url of the changelog.
     */
    @Api(1.2)
    @Nullable
    public String getChangeLogUrl() {
        return mChangeLogUrl;
    }

    /**
     * Checks if the version is outdated given another version name.
     *
     * @param version The version name.
     * @return True if it is outdated, false otherwise.
     */
    @Api(1.2)
    public boolean isOutdated(@NonNull String version) {
        AssertionUtils.notNull(version, "version == null");
        return new Version(version).compareTo(new Version(mMinHaloVersion)) < 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HaloServerVersion version = (HaloServerVersion) o;

        return (mMinHaloVersion != null ? mMinHaloVersion.equals(version.mMinHaloVersion) : version.mMinHaloVersion == null) &&
                (mChangeLogUrl != null ? mChangeLogUrl.equals(version.mChangeLogUrl) : version.mChangeLogUrl == null);

    }

    @Override
    public int hashCode() {
        int result = mMinHaloVersion != null ? mMinHaloVersion.hashCode() : 0;
        result = 31 * result + (mChangeLogUrl != null ? mChangeLogUrl.hashCode() : 0);
        return result;
    }

    /**
     * The version representation.
     */
    private class Version implements Comparable<Version> {

        /**
         * The version name.
         */
        private String mVersionName;

        /**
         * The version constructor.
         *
         * @param versionName The version name.
         */
        private Version(@NonNull String versionName) {
            mVersionName = versionName.replace("-SNAPSHOT", "");
        }

        @Override
        public int compareTo(@NonNull Version another) {
            String[] subNumbersFirst = mVersionName.split("\\.");
            String[] subNumbersSecond = another.mVersionName.split("\\.");
            int i = 0;
            // set index to first non-equal ordinal or length of shortest version string
            while (i < subNumbersFirst.length && i < subNumbersSecond.length && subNumbersFirst[i].equals(subNumbersSecond[i])) {
                i++;
            }
            // compare first non-equal ordinal numbers
            if (i < subNumbersFirst.length && i < subNumbersSecond.length) {
                int diff = Integer.valueOf(subNumbersFirst[i]).compareTo(Integer.valueOf(subNumbersSecond[i]));
                return Integer.signum(diff);
            } else {
                // the strings are equal or one string is a substring of the other
                // e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"
                return Integer.signum(subNumbersFirst.length - subNumbersSecond.length);
            }
        }
    }
}
