package com.mobgen.halo.android.framework.storage.database;

import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The version manager is in charge to manage how the database will handle the version update.
 */
public class HaloDatabaseVersionManager {

    /**
     * The builder used to create the database version manager.
     */
    private final Builder mBuilder;

    /**
     * The constructor for the version manager.
     *
     * @param builder The current builder.
     */
    private HaloDatabaseVersionManager(@NonNull Builder builder) {
        mBuilder = builder;
    }

    /**
     * Provide the builder object for the HaloDatabaseVersionManager.
     *
     * @return The builder created.
     */
    @Api(2.0)
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder to create the new database version. It uses the version numbers to create the database
     * or update it accordingly.
     * @param db The database.
     */
    @Api(2.0)
    public void create(@NonNull SQLiteDatabase db) {
        for (HaloDatabaseMigration version : mBuilder.mDatabaseVersions) {
            version.updateDatabase(db);
        }
    }

    /**
     * Updates the database based on the versions available and the current version.
     *
     * @param db         The database to update.
     * @param oldVersion The old version of this database.
     * @param newVersion The new version of the database.
     */
    @Api(2.0)
    public void update(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        for (HaloDatabaseMigration version : mBuilder.mDatabaseVersions) {
            if (version.isInMigration(oldVersion, newVersion)) {
                version.updateDatabase(db);
            }
        }
    }

    /**
     * The builder class for the database version manager. It stores all the version.
     */
    public static class Builder implements IBuilder<HaloDatabaseVersionManager> {

        /**
         * The database version list. This versions will be called from the app to
         */
        private final List<HaloDatabaseMigration> mDatabaseVersions;

        /**
         * Constructor of the builder that creates the versions.
         */
        private Builder() {
            mDatabaseVersions = new ArrayList<>();
        }

        /**
         * The builder constructor that takes the parameters from another builder.
         *
         * @param builder The builder.
         */
        @Api(2.0)
        public Builder(@NonNull Builder builder) {
            mDatabaseVersions = new ArrayList<>(builder.mDatabaseVersions);
        }

        /**
         * Adds a new version to the version manager.
         *
         * @param version The version to add to the version manager.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder add(@NonNull HaloDatabaseMigration version) {
            mDatabaseVersions.add(version);
            return this;
        }

        /**
         * Build the instance.
         *
         * @return The instance.
         */
        @NonNull
        @Override
        @Api(2.0)
        public HaloDatabaseVersionManager build() {
            Collections.sort(mDatabaseVersions);
            return new HaloDatabaseVersionManager(this);
        }
    }
}
