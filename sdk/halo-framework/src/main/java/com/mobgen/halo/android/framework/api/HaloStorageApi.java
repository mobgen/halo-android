package com.mobgen.halo.android.framework.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloConfigurationException;
import com.mobgen.halo.android.framework.storage.database.HaloDataLite;
import com.mobgen.halo.android.framework.storage.preference.HaloPreferencesStorage;

/**
 * Builder factory that allows the developer to configure with the properly validations a storage
 * api, with possibility to access to preferences as well as a database.
 */
public class HaloStorageApi {

    /**
     * The framework instance that contains this api.
     */
    private HaloFramework mFramework;
    /**
     * The preferences data source.
     */
    private final HaloPreferencesStorage mPreferences;
    /**
     * The database data source.
     */
    private final HaloDataLite mDatabase;

    /**
     * Creates a new instance of the storage api.
     *
     * @param framework          The framework instance that contains this api.
     * @param preferencesStorage The preferences storage.
     * @param database           The database.
     */
    protected HaloStorageApi(@NonNull HaloFramework framework, @Nullable HaloPreferencesStorage preferencesStorage, @NonNull HaloDataLite database) {
        mFramework = framework;
        mPreferences = preferencesStorage;
        mDatabase = database;
    }

    /**
     * Creates a new storage based on the config provided. This storage is not saved
     * in the framework, but provided as a new item.
     *
     * @param framework     The framework.
     * @param configuration The configuration.
     * @return The new storage.
     */
    @Api(1.3)
    @NonNull
    public static HaloStorageApi newStorageApi(HaloFramework framework, StorageConfig configuration) {
        //The database
        HaloDataLite database = HaloDataLite.builder(framework.context())
                .setDatabaseName(configuration.storageName())
                .setDatabaseVersion(configuration.databaseVersion())
                .setErrorHandler(configuration.errorHandler())
                .setVersionManager(configuration.versionManager())
                .build();
        //The preferences
        HaloPreferencesStorage preferences = new HaloPreferencesStorage(framework.context(), configuration.storageName());

        //Create the api instances
        return new HaloStorageApi(framework, preferences, database);
    }

    /**
     * Provides the preferences data source.
     *
     * @return The preferences data source.
     */
    @Api(1.0)
    @NonNull
    public HaloPreferencesStorage prefs() {
        if (mPreferences == null) {
            throw new HaloConfigurationException("You have to provide a preferences name in the builder to use the preferences data source.");
        }
        return mPreferences;
    }

    /**
     * The database instance.
     *
     * @return The database instance.
     */
    @Api(1.0)
    @NonNull
    public HaloDataLite db() {
        return mDatabase;
    }

    /**
     * Provides the current context.
     *
     * @return The context.
     */
    @Api(1.1)
    @NonNull
    public Context context() {
        return mFramework.context();
    }

    /**
     * The framework instance.
     *
     * @return The framework instance.
     */
    @Api(1.3)
    @NonNull
    public HaloFramework framework() {
        return mFramework;
    }
}
