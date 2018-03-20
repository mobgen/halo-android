package com.mobgen.halo.android.framework.api;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloConfigurationException;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.storage.database.HaloDatabaseErrorHandler;
import com.mobgen.halo.android.framework.storage.database.HaloDatabaseMigration;
import com.mobgen.halo.android.framework.storage.database.HaloDatabaseVersionManager;

/**
 * Storage configurations that are available when creating a new
 * instance of the {@link HaloStorageApi} using the {@link HaloFramework}.
 */
public class StorageConfig {

    /**
     * Default storage name
     */
    public static String  DEFAULT_STORAGE_NAME = "halo-storage";

    /**
     * Builder config.
     */
    private Builder mBuilder;
    /**
     * The databse manager created.
     */
    private HaloDatabaseVersionManager mVersionManager;

    /**
     * Constructor for the config.
     *
     * @param builder The builder.
     */
    private StorageConfig(@NonNull Builder builder) {
        mBuilder = builder;
        mVersionManager = builder.mVersionManagerBuilder.build();
    }

    /**
     * The builder creation.
     *
     * @return The builder.
     */
    @Api(2.0)
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Provides the storage name.
     *
     * @return The name of the storage.
     */
    @Api(2.0)
    @NonNull
    public String storageName() {
        return mBuilder.mStorageName;
    }

    /**
     * Provides the database version.
     *
     * @return The database version.
     */
    @Api(2.0)
    public int databaseVersion() {
        return mBuilder.mDatabaseVersion;
    }

    /**
     * Provides the database version manager.
     *
     * @return The database version manager.
     */
    @Api(2.0)
    @NonNull
    public HaloDatabaseVersionManager versionManager() {
        return mVersionManager;
    }

    /**
     * The error handler.
     *
     * @return The error handler.
     */
    @Api(2.0)
    @NonNull
    public HaloDatabaseErrorHandler errorHandler() {
        return mBuilder.mErrorHandler;
    }

    /**
     * The builder class for the storage config.
     */
    public static class Builder implements IBuilder<StorageConfig> {

        /**
         * The storage name.
         */
        private String mStorageName;

        /**
         * The database version.
         */
        private int mDatabaseVersion;

        /**
         * The error handler.
         */
        private HaloDatabaseErrorHandler mErrorHandler;

        /**
         * The version manager.
         */
        private HaloDatabaseVersionManager.Builder mVersionManagerBuilder;

        /**
         * Builder for the storage configuration.
         */
        private Builder() {
            mVersionManagerBuilder = HaloDatabaseVersionManager.builder();
        }

        /**
         * Provides the storage name.
         *
         * @param storageName The storage name.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder storageName(@NonNull String storageName) {
            mStorageName = storageName;
            return this;
        }

        /**
         * Sets the database version number.
         *
         * @param databaseVersion The database version number.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder databaseVersion(int databaseVersion) {
            mDatabaseVersion = databaseVersion;
            return this;
        }

        /**
         * Sets the error handler.
         *
         * @param errorHandler The error handler.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder errorHandler(@NonNull HaloDatabaseErrorHandler errorHandler) {
            AssertionUtils.notNull(errorHandler, "errorHandler");
            mErrorHandler = errorHandler;
            return this;
        }

        /**
         * Adds some sort of migrations to this database. It includes the master one.
         *
         * @param migrations The migrations added.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder addMigrations(@NonNull HaloDatabaseMigration... migrations) {
            AssertionUtils.notNull(migrations, "migrations");
            for (HaloDatabaseMigration migration : migrations) {
                mVersionManagerBuilder.add(migration);
            }
            return this;
        }

        /**
         * Constructs the configuration.
         *
         * @return The configuration.
         */
        @Api(2.0)
        @NonNull
        @Override
        public StorageConfig build() {
            if (mStorageName == null) {
                mStorageName = DEFAULT_STORAGE_NAME;
            }
            if (mDatabaseVersion <= 0) {
                throw new HaloConfigurationException("Error configuring the database" + mStorageName + ". The version must be greater than 0");
            }
            if (mErrorHandler == null) {
                mErrorHandler = new HaloDatabaseErrorHandler();
            }
            return new StorageConfig(this);
        }
    }
}
