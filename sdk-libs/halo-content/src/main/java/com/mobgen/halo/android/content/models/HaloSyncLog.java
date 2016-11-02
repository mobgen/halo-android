package com.mobgen.halo.android.content.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mobgen.halo.android.content.spec.HaloContentContract.ContentSyncLog;

/**
 * Stats with the synchronization result.
 */
@Keep
public class HaloSyncLog implements Parcelable {
    /**
     * The module id of the stats.
     */
    private String mModuleName;
    /**
     * The locale of the sync.
     */
    @HaloLocale.LocaleDefinition
    private String mLocale;
    /**
     * Sync log date.
     */
    private Date mSyncDate;
    /**
     * The number of creations.
     */
    private final int mCreations;
    /**
     * The number of updates in the database.
     */
    private final int mUpdates;
    /**
     * The number of deletions in the database.
     */
    private final int mDeletions;

    /**
     * The creator for parcelable.
     */
    public static final Creator<HaloSyncLog> CREATOR = new Creator<HaloSyncLog>() {
        @Override
        public HaloSyncLog createFromParcel(Parcel source) {
            return new HaloSyncLog(source);
        }

        @Override
        public HaloSyncLog[] newArray(int size) {
            return new HaloSyncLog[size];
        }
    };

    /**
     * Creates the log.
     * @param moduleName The module name.
     * @param locale The locale.
     * @param syncDate The current sync date.
     * @param creations The number of creations.
     * @param updates The number of updates.
     * @param deletions The number of deletions.
     */
    private HaloSyncLog(@NonNull String moduleName, @HaloLocale.LocaleDefinition @Nullable String locale, @NonNull Date syncDate, int creations, int updates, int deletions) {
        mModuleName = moduleName;
        mLocale = locale;
        mSyncDate = syncDate;
        mCreations = creations;
        mUpdates = updates;
        mDeletions = deletions;
    }

    /**
     * Constructor for the parcelable.
     *
     * @param in The parcel.
     */
    @SuppressWarnings("all")
    protected HaloSyncLog(Parcel in) {
        this.mModuleName = in.readString();
        this.mLocale = in.readString();
        this.mSyncDate = new Date(in.readLong());
        this.mCreations = in.readInt();
        this.mUpdates = in.readInt();
        this.mDeletions = in.readInt();
    }

    /**
     * Creates the sync stats based on the module name.
     *
     * @param moduleName  The module name.
     * @param locale    The locale.
     * @param creations The number of items created.
     * @param updates   The number of items updated.
     * @param deletions The number of items deleted.
     * @return The sync stats.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public static HaloSyncLog create(@NonNull String moduleName, @Nullable String locale, @NonNull Date syncDate, int creations, int updates, int deletions) {
        return new HaloSyncLog(moduleName, locale, syncDate, creations, updates, deletions);
    }

    /**
     * Creates the content values for the stats.
     *
     * @param stats The stats.
     * @return The content values created.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public static ContentValues createSyncStatsValues(@NonNull HaloSyncLog stats) {
        ContentValues values = new ContentValues();
        values.put(ContentSyncLog.MODULE_NAME, stats.mModuleName);
        values.put(ContentSyncLog.LOCALE, stats.mLocale);
        values.put(ContentSyncLog.SYNC_DATE, stats.mSyncDate.getTime());
        values.put(ContentSyncLog.CREATIONS, stats.mCreations);
        values.put(ContentSyncLog.UPDATES, stats.mUpdates);
        values.put(ContentSyncLog.DELETIONS, stats.mDeletions);
        return values;
    }

    /**
     * Creates a list of sync log items based on the cursor.
     *
     * @param cursor The cursor provided.
     * @param close  True to close.
     * @return The list of items.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public static List<HaloSyncLog> createList(@NonNull Cursor cursor, boolean close) {
        List<HaloSyncLog> logs = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                logs.add(parseCursorInternal(cursor));
            } while (cursor.moveToNext());
        }
        if (close) {
            cursor.close();
        }
        return logs;
    }

    /**
     * Picks the data from the cursor to fromCursor a sync stats.
     *
     * @param cursor     The data fetched.
     * @param closeAfter True to close the cursor after requesting the data.
     */
    @Keep
    @Api(2.0)
    @Nullable
    public static HaloSyncLog create(Cursor cursor, boolean closeAfter) {
        HaloSyncLog stats = null;
        if (cursor.moveToFirst()) {
            stats = parseCursorInternal(cursor);
        }
        if (closeAfter) {
            cursor.close();
        }
        return stats;
    }

    /**
     * Parses the cursor into a new instance.
     *
     * @param cursor The cursor.
     * @return The new instance.
     */
    @NonNull
    @SuppressWarnings("ResourceType")
    private static HaloSyncLog parseCursorInternal(@NonNull Cursor cursor) {
        String moduleName = cursor.getString(cursor.getColumnIndexOrThrow(ContentSyncLog.MODULE_NAME));
        String locale = cursor.getString(cursor.getColumnIndexOrThrow(ContentSyncLog.LOCALE));
        Date syncDate = new Date(cursor.getLong(cursor.getColumnIndexOrThrow(ContentSyncLog.SYNC_DATE)));
        int creations = cursor.getInt(cursor.getColumnIndexOrThrow(ContentSyncLog.CREATIONS));
        int updates = cursor.getInt(cursor.getColumnIndexOrThrow(ContentSyncLog.UPDATES));
        int deletions = cursor.getInt(cursor.getColumnIndexOrThrow(ContentSyncLog.DELETIONS));
        return new HaloSyncLog(moduleName, locale, syncDate, creations, updates, deletions);
    }

    /**
     * Provides the module id.
     *
     * @return The module id.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public String getModuleName() {
        return mModuleName;
    }

    /**
     * Provides the locale.
     *
     * @return The locale.
     */
    @Keep
    @Api(2.0)
    @HaloLocale.LocaleDefinition
    @Nullable
    public String getLocale() {
        return mLocale;
    }

    /**
     * Provides the sync date.
     *
     * @return The sync date.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public Date getSyncDate() {
        return mSyncDate;
    }

    /**
     * Provides the number of creations.
     *
     * @return The number of creations.
     */
    @Keep
    @Api(2.0)
    public int getCreations() {
        return mCreations;
    }

    /**
     * Provides the number of updates.
     *
     * @return The number of updates.
     */
    @Keep
    @Api(2.0)
    public int getUpdates() {
        return mUpdates;
    }

    /**
     * Provides the number of deletions.
     *
     * @return The deletions.
     */
    @Keep
    @Api(2.0)
    public int getDeletions() {
        return mDeletions;
    }

    /**
     * Provides the number of affected entries by the sync.
     *
     * @return The number of affected entries.
     */
    @Keep
    @Api(2.0)
    public int getModifiedEntries() {
        return mCreations + mUpdates + mDeletions;
    }

    /**
     * Tells if the synchronization changed some row or not.
     *
     * @return True if the sync changed something, false otherwise.
     */
    @Keep
    @Api(2.0)
    public boolean didSomethingChange() {
        return getModifiedEntries() > 0;
    }

    @Override
    public String toString() {
        return "CREATED: " + mCreations + " | " +
                "UPDATED: " + mUpdates + " | " +
                "DELETED: " + mDeletions + " on " + mModuleName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mModuleName);
        dest.writeString(this.mLocale);
        dest.writeLong(mSyncDate != null ? mSyncDate.getTime() : -1);
        dest.writeInt(this.mCreations);
        dest.writeInt(this.mUpdates);
        dest.writeInt(this.mDeletions);
    }
}
