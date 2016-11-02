package com.mobgen.halo.android.framework.storage.database;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Represents the operations that have to be done for a given version of the database.
 */
public abstract class HaloDatabaseMigration implements Comparable<HaloDatabaseMigration> {
    /**
     * Open helper to update the database.
     *
     * @param database The open helper.
     */
    @Api(2.0)
    public abstract void updateDatabase(@NonNull SQLiteDatabase database);

    /**
     * Provides the database version to which this update script belongs to.
     *
     * @return The database version.
     */
    @Api(2.0)
    public abstract int getDatabaseVersion();

    /**
     * Checks if this migration is in the current version.
     *
     * @param currentVersion   The current version.
     * @param migrationVersion The migration version to which the database will be changed.
     * @return True if this migration is contained, false otherwise.
     */
    @Api(2.0)
    public boolean isInMigration(int currentVersion, int migrationVersion) {
        return getDatabaseVersion() > currentVersion && getDatabaseVersion() <= migrationVersion;
    }

    /**
     * Compares two database versions to make keep the order.
     *
     * @param another The other element to which it will be compared.
     * @return The result of a compare operation.
     */
    @Override
    public final int compareTo(@NonNull HaloDatabaseMigration another) {
        int compare = getDatabaseVersion() - another.getDatabaseVersion();
        if (compare > 0) {
            return 1;
        } else if (compare < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
