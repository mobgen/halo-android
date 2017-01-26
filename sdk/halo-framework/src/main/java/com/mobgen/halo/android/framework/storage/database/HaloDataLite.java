package com.mobgen.halo.android.framework.storage.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloConfigurationException;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;


/**
 * Database creation helper that manages the connections and transactions.
 */
public class HaloDataLite extends SQLiteOpenHelper implements Corruptible {

    /**
     * The database version manager. This instance comes from the builder passed in the constructor.
     */
    private HaloDatabaseVersionManager mDatabaseVersionManager;

    /**
     * The database instance in case it is opened successfully.
     */
    private SQLiteDatabase mDatabase;

    /**
     * The application context to create a database.
     */
    private final Context mContext;

    /**
     * Creates the helper for the database.
     *
     * @param context The context on which this database will be created. Typically it is the
     *                application context.
     * @param name    The name of the database. Taken as static value for the HALO contract.
     * @param version The version for this database.
     * @param manager The database version manager used to create all the versions for this database.
     * @param handler The error handler to avoid corrupted databases to be used and produce exceptions.
     */
    private HaloDataLite(@NonNull Context context, @NonNull String name, int version, @NonNull HaloDatabaseVersionManager manager, @NonNull HaloDatabaseErrorHandler handler) {
        super(context, name, null, version, handler);
        mContext = context;
        mDatabaseVersionManager = manager;
        //Make sure the error handler belongs a reference to the data lite helper.
        handler.setCorruptible(this);
    }

    /**
     * Creates a builder for the database helper.
     *
     * @param context The context of the application.
     * @return The current builder.
     */
    @Api(2.0)
    public static Builder builder(Context context) {
        return new Builder(context);
    }

    /**
     * Delegates to the version manager for the first version.
     *
     * @param db The database.
     */
    @Override
    public void onCreate(@NonNull SQLiteDatabase db) {
        mDatabaseVersionManager.create(db);
    }

    /**
     * Upgrades the database delegating to the version manager.
     *
     * @param db         The current database.
     * @param oldVersion The old version of the database.
     * @param newVersion The new version of the database.
     */
    @Override
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        mDatabaseVersionManager.update(db, oldVersion, newVersion);
    }

    /**
     * Provides the current instance of the database or opens it if available. This process creates
     * also the database if it has not been created.
     *
     * @return The database instance.
     */
    @Api(2.0)
    public synchronized SQLiteDatabase getDatabase() {
        //Singleton instance for the database.
        if (mDatabase == null) {
            mDatabase = getWritableDatabase();
        }
        return mDatabase;
    }

    /**
     * Ensure that the database is ready.
     */
    @Api(2.0)
    public void ensureInitialized(){
        getDatabase();
    }

    /**
     * Notifies that the database is corrupted. Never call this method from outside, it will be called
     * by the HALO SDK in case this is needed.
     */
    @Override
    public void onCorrupted() {
        deleteDatabase();
    }

    /**
     * Deletes the cache database.
     */
    @Api(1.0)
    public void deleteDatabase() {
        //Remove the database instance
        if (mDatabase != null) {
            mDatabase.close();
            //We don't need this reference anymore since it is closed and removed.
            mDatabase = null;
        }
        //Remove the current database to make sure there is no error related to corruption
        mContext.deleteDatabase(getDatabaseName());
    }

    /**
     * Attaches a database to the current one by name.
     *
     * @param databaseName The database to attach.
     * @param aliasName    The alias name.
     */
    @Api(1.3)
    public void attachDatabase(@NonNull String databaseName, @NonNull String aliasName) {
        AssertionUtils.notNull(databaseName, "databaseName");
        AssertionUtils.notNull(aliasName, "aliasName");
        getDatabase().execSQL(String.format("ATTACH ? as ?;", mContext.getDatabasePath(databaseName), aliasName));
    }

    /**
     * Detaches a database.
     *
     * @param aliasName The database nam as alias to detach.
     */
    @Api(1.3)
    public void detachDatabase(@NonNull String aliasName) {
        AssertionUtils.notNull(aliasName, "aliasName");
        getDatabase().execSQL(String.format("DETACH ?;", aliasName));
    }

    /**
     * Executes a database transaction.
     *
     * @param transaction The transaction callback.
     * @throws HaloStorageGeneralException Error with the storage operation.
     */
    @Api(2.0)
    public void transaction(@NonNull HaloDataLiteTransaction transaction) throws HaloStorageGeneralException {
        SQLiteDatabase database = getDatabase();
        database.beginTransaction();
        try {
            transaction.onTransaction(database);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            throw new HaloStorageGeneralException("Database error: " + e.getMessage(), e);
        } finally {
            database.endTransaction();
        }
    }

    /**
     * Transaction class for the database.
     */
    public interface HaloDataLiteTransaction {
        /**
         * The database on which the transaction will happen.
         *
         * @param database The database.
         * @throws HaloStorageException Transaction storage.
         */
        @Api(2.0)
        void onTransaction(@NonNull SQLiteDatabase database) throws HaloStorageException;
    }

    /**
     * The internal builder for the database helper. Should not be created
     * by the user since it is something internal to the SDK.
     */
    public static class Builder implements IBuilder<HaloDataLite> {

        /**
         * The application context.
         */
        private final Context mContext;
        /**
         * The database name.
         */
        private String mDatabaseName;
        /**
         * The database version.
         */
        private int mDatabaseVersion;
        /**
         * The version manager.
         */
        private HaloDatabaseVersionManager mVersionManager;
        /**
         * The error handler.
         */
        private HaloDatabaseErrorHandler mErrorHandler;

        /**
         * Constructor for the builder that needs he context.
         *
         * @param ctx The application context.
         */
        private Builder(@NonNull Context ctx) {
            mContext = ctx;
        }

        /**
         * Sets the database name.
         *
         * @param name The database name.
         * @return The current builder.
         */
        @NonNull
        @Api(2.0)
        public Builder setDatabaseName(@NonNull String name) {
            mDatabaseName = name;
            return this;
        }

        /**
         * Sets the database version.
         *
         * @param version The database version.
         * @return The current builder.
         */
        @NonNull
        @Api(2.0)
        public Builder setDatabaseVersion(int version) {
            mDatabaseVersion = version;
            return this;
        }

        /**
         * Sets the version manager builder to handle the versions of the database.
         *
         * @param builder The database version builder.
         * @return The current builder.
         */
        @NonNull
        @Api(2.0)
        public Builder setVersionManager(@NonNull HaloDatabaseVersionManager builder) {
            mVersionManager = builder;
            return this;
        }

        /**
         * Sets the error handler for the database.
         *
         * @param errorHandler The error handler.
         * @return The current builder.
         */
        @NonNull
        @Api(2.0)
        public Builder setErrorHandler(@NonNull HaloDatabaseErrorHandler errorHandler) {
            mErrorHandler = errorHandler;
            return this;
        }

        /**
         * Builds the database helper object. That does not mean the database is created at all
         * until you call to open it.
         *
         * @return The helper object. You should only save one reference to it.
         */
        @Override
        @NonNull
        @Api(2.0)
        public HaloDataLite build() {
            if (mDatabaseName == null) {
                throw new HaloConfigurationException("The database name cannot be null.");
            }
            if (mDatabaseVersion <= 0) {
                throw new HaloConfigurationException("The database version should be greater than 0.");
            }
            if (mVersionManager == null) {
                throw new HaloConfigurationException("The version manager is for the database is null.");
            }
            if (mErrorHandler == null) {
                throw new HaloConfigurationException("The error handler must not be is null");
            }
            return new HaloDataLite(mContext, mDatabaseName, mDatabaseVersion, mVersionManager, mErrorHandler);
        }
    }
}
