package com.mobgen.halo.android.content.sync;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.HaloInstanceSync;
import com.mobgen.halo.android.content.models.HaloSyncLog;
import com.mobgen.halo.android.content.models.SyncQuery;
import com.mobgen.halo.android.content.spec.HaloContentContract;
import com.mobgen.halo.android.content.spec.HaloContentContract.ContentSync;
import com.mobgen.halo.android.framework.api.HaloStorageApi;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.storage.database.HaloDataLite;
import com.mobgen.halo.android.framework.storage.database.dsl.ORMUtils;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Delete;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Select;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import java.util.List;

/**
 * @hide Sync data source for the operations needed.
 */
public class ContentSyncLocalDatasource {

    /**
     * Sync operation definition.
     */
    @IntDef({SYNC_OP_CREATION, SYNC_OP_UPDATE, SYNC_OP_DELETION})
    @Retention(RetentionPolicy.SOURCE)
    private @interface SyncOperation {
    }

    /**
     * Sync operation number for creations.
     */
    private static final int SYNC_OP_CREATION = 1;
    /**
     * Sync operation number for updates.
     */
    private static final int SYNC_OP_UPDATE = 2;
    /**
     * Sync operation number for deletions.
     */
    private static final int SYNC_OP_DELETION = 3;
    /**
     * The locale constant to use as id when the locale is being used.
     */
    private static final String LOCALE_CONSTANT = "_locale";

    /**
     * Provides the storage access.
     */
    private HaloStorageApi mStorage;

    /**
     * Constructor for the local datasource.
     *
     * @param storage The storage api.
     */
    public ContentSyncLocalDatasource(@NonNull HaloStorageApi storage) {
        AssertionUtils.notNull(storage, "storage");
        mStorage = storage;
    }

    /**
     * Provides the synced module log.
     *
     * @param entryLogId The entry log id.
     * @return The cursor with the log of the id provided.
     */
    @Nullable
    public Cursor getSyncedModuleLog(long entryLogId) {
        Cursor entryLogCursor = null;
        if (entryLogId != -1) {
            entryLogCursor = Select.all().from(HaloContentContract.ContentSyncLog.class)
                    .where(HaloContentContract.ContentSyncLog.ID)
                    .eq(entryLogId)
                    .on(mStorage.db(), "Get the stats with id: " + entryLogId);
        }
        return entryLogCursor;
    }

    /**
     * Provides the sync log for the given module name.
     *
     * @param moduleName The module name or null for all.
     * @return The cursor for this module name logs.
     */
    @NonNull
    public Cursor getSyncLog(@Nullable String moduleName) {
        Select.FromSyntax query = Select.all().from(HaloContentContract.ContentSyncLog.class);
        Select.ExecutableExpression executable = query;
        if (moduleName != null) {
            executable = query.where(HaloContentContract.ContentSyncLog.MODULE_NAME).eq(moduleName);
        }
        return executable.on(mStorage.db());
    }

    /**
     * Provides a cursor with all the instances synced for the given module name.
     *
     * @param moduleName The module name.
     * @return The cursor created.
     */
    @NonNull
    public Cursor getSyncedModuleItems(@NonNull String moduleName) {
        return Select.all().from(ContentSync.class)
                .where(ContentSync.MODULE_NAME)
                .eq(moduleName)
                .on(mStorage.db(), "Fetch all the items for the given module.");
    }

    /**
     * Tries to synchronize the information.
     *
     * @param isFirstSync     True if this is the first sync attempt.
     * @param syncQuery       The query that will be synced.
     * @param instancesToSync The instances to sync.
     * @return The entry id of the log generated. -1 if there was some error creating the entry.
     */
    public long sync(boolean isFirstSync, @NonNull final SyncQuery syncQuery, @NonNull final HaloInstanceSync instancesToSync) throws HaloStorageGeneralException {
        final long[] entryId = {-1};

        //Ensure there is no instances for the first execution
        if (isFirstSync) {
            clearSyncModule(syncQuery.getModuleName());
        }

        //Transact the sync
        final HaloContentSyncQueryManager queryManager = new HaloContentSyncQueryManager(mStorage.db().getDatabase());
        mStorage.db().transaction(new HaloDataLite.HaloDataLiteTransaction() {
            @Override
            public void onTransaction(@NonNull SQLiteDatabase database) throws HaloStorageException {
                Halog.d(getClass(), "Sync in progress...");
                int creations = doSync(queryManager, syncQuery.getModuleName(), instancesToSync.getSyncDate(), instancesToSync.getCreations(), SYNC_OP_CREATION);
                int updates = doSync(queryManager, syncQuery.getModuleName(), instancesToSync.getSyncDate(), instancesToSync.getUpdates(), SYNC_OP_UPDATE);
                int deletions = doSync(queryManager, syncQuery.getModuleName(), instancesToSync.getSyncDate(), instancesToSync.getDeletions(), SYNC_OP_DELETION);

                //Store the execution log and append the entry
                HaloSyncLog syncLog = HaloSyncLog.create(syncQuery.getModuleName(), syncQuery.getLocale(), instancesToSync.getSyncDate(), creations, updates, deletions);
                entryId[0] = createSyncEntryLog(database, syncLog);
                Halog.d(getClass(), "Sync stats: " + syncLog.toString());

                //Store the last execution date
                saveLastSyncDate(syncQuery.getModuleName(), syncQuery.getLocale(), instancesToSync.getSyncDate());
            }
        });
        queryManager.release();
        return entryId[0];
    }

    /**
     * Performs the execution based on the execution operation for the given instances.
     *
     * @param syncDao       The dao to insert sync instances.
     * @param moduleName    The module name.
     * @param syncDate      The execution date.
     * @param data          The data to execution.
     * @param syncOperation The execution operation.
     * @return affectedItems
     * @throws HaloStorageGeneralException The storage exception.
     */
    private int doSync(@NonNull final HaloContentSyncQueryManager syncDao,
                       @NonNull final String moduleName,
                       @NonNull final Date syncDate,
                       @NonNull final List<HaloContentInstance> data,
                       @SyncOperation final int syncOperation) throws HaloStorageGeneralException {
        if (!data.isEmpty()) {
            switch (syncOperation) {
                case SYNC_OP_CREATION:
                    syncDao.insertOrFail(data, moduleName, syncDate);
                    break;
                case SYNC_OP_UPDATE:
                    syncDao.insertOrReplace(data, moduleName, syncDate);
                    break;
                case SYNC_OP_DELETION:
                    syncDao.delete(data);
                    break;
                default:
                    throw new IllegalArgumentException("Should not be called with another option");
            }
        }
        return data.size();
    }

    /**
     * Clears a sync module.
     *
     * @param moduleName The module name.
     */
    public void clearSyncModule(@NonNull String moduleName) {
        Delete.from(ContentSync.class)
                .where(ContentSync.MODULE_NAME)
                .eq(moduleName)
                .on(mStorage.db(), "Deletes the previously synchronized module with module name " + moduleName);
        clearSyncDate(moduleName);
    }

    /**
     * Creates a new execution entry in the database with the information provided in the execution stats.
     *
     * @param database The storage where the result will be stored.
     * @param syncLog  The stats.
     * @return The row id created.
     */
    public long createSyncEntryLog(@NonNull SQLiteDatabase database, @NonNull HaloSyncLog syncLog) {
        ContentValues values = HaloSyncLog.createSyncStatsValues(syncLog);
        String tableName = ORMUtils.getTableName(HaloContentContract.ContentSyncLog.class);
        return database.insert(tableName, null, values);
    }

    /**
     * @param module The module to check.
     * @param locale The locale.
     * @return The date or null if it is not available.
     */
    @Nullable
    public Date getLastSyncDate(@Nullable String module, @HaloLocale.LocaleDefinition @Nullable String locale) {
        Date date = null;
        if (module != null) {
            String lastModuleLocale = mStorage.prefs().getString(module + LOCALE_CONSTANT, null);
            if (lastModuleLocale == null || lastModuleLocale.equals(locale)) {
                Long millis = mStorage.prefs().getLong(module, null);
                if (millis != null) {
                    date = new Date(millis);
                }
            }
        }
        return date;
    }

    /**
     * Saves the time for the sync given the locale.
     *
     * @param moduleName The module.
     * @param locale The locale to store.
     * @param date   The date to store.
     */
    public void saveLastSyncDate(@NonNull String moduleName, @HaloLocale.LocaleDefinition @Nullable String locale, @NonNull Date date) {
        mStorage.prefs().edit()
                .putLong(moduleName, date.getTime())
                .putString(moduleName + LOCALE_CONSTANT, locale)
                .commit();
    }

    /**
     * Clears the sync date given a locale and a module.
     *
     * @param moduleName The module name.
     */
    private void clearSyncDate(@NonNull String moduleName) {
        mStorage.prefs().edit()
                .remove(moduleName)
                .remove(moduleName + LOCALE_CONSTANT)
                .commit();
    }

    /**
     * Sync object that caches the queries for performance improvements.
     * Remember to call release to free the memory of the requests stored.
     */
    private static class HaloContentSyncQueryManager {

        /**
         * Generic insert to add data into the database.
         */
        private static final String INSERT = " (" +
                ContentSync.ID + "," +
                ContentSync.MODULE_ID + "," +
                ContentSync.NAME + "," +
                ContentSync.VALUES + "," +
                ContentSync.AUTHOR + "," +
                ContentSync.PUBLISHED + "," +
                ContentSync.CREATED_AT + "," +
                ContentSync.UPDATED_AT + "," +
                ContentSync.REMOVED + "," +
                ContentSync.LAST_SYNCED + "," +
                ContentSync.MODULE_NAME +
                ") VALUES (?,?,?,?,?,?,?,?,?,?,?);";

        /**
         * Insert or fail statement.
         */
        private static final String INSERT_FAIL_STATEMENT = "INSERT OR FAIL INTO " + ORMUtils.getTableName(ContentSync.class) + INSERT;

        /**
         * Insert or replace statement.
         */
        private static final String INSERT_REPLACE_STATEMENT = "INSERT OR REPLACE INTO " + ORMUtils.getTableName(ContentSync.class) + INSERT;

        /**
         * Delete statement name.
         */
        private static final String DELETE_STATEMENT = "DELETE FROM " +
                ORMUtils.getTableName(ContentSync.class) +
                " WHERE " +
                ContentSync.ID +
                " = ?;";

        /**
         * The database instance.
         */
        private SQLiteDatabase mDatabase;
        /**
         * Insert statement to introduce the instances.
         */
        private SQLiteStatement mInsertOrFailStatement;
        /**
         * Inserts or replace statement to update the instances.
         */
        private SQLiteStatement mInsertOrReplaceStatement;
        /**
         * Delete statement to remove the instances.
         */
        private SQLiteStatement mDeleteStatement;

        private HaloContentSyncQueryManager(@NonNull SQLiteDatabase database) {
            AssertionUtils.notNull(database, "database");
            mDatabase = database;
        }

        /**
         * Inserts or fail a list of content instances.
         *
         * @param instances  The instances.
         * @param moduleName The module name.
         * @param syncDate   The sync date.
         */
        private void insertOrFail(@NonNull List<HaloContentInstance> instances, @NonNull String moduleName, @NonNull Date syncDate) {
            if (mInsertOrFailStatement == null) {
                mInsertOrFailStatement = mDatabase.compileStatement(INSERT_FAIL_STATEMENT);
            }
            for (HaloContentInstance instance : instances) {
                insert(mInsertOrFailStatement, instance, moduleName, syncDate);
            }
        }

        /**
         * Inserts or replaces the list of content instances.
         *
         * @param instances  The instances.
         * @param moduleName The module name.
         * @param syncDate   The the sync date.
         */
        private void insertOrReplace(@NonNull List<HaloContentInstance> instances, @NonNull String moduleName, @NonNull Date syncDate) {
            if (mInsertOrReplaceStatement == null) {
                mInsertOrReplaceStatement = mDatabase.compileStatement(INSERT_REPLACE_STATEMENT);
            }
            for (HaloContentInstance instance : instances) {
                insert(mInsertOrReplaceStatement, instance, moduleName, syncDate);
            }
        }

        /**
         * Deletes a list of instances.
         *
         * @param instances The instances.
         */
        private void delete(List<HaloContentInstance> instances) {
            if (mDeleteStatement == null) {
                mDeleteStatement = mDatabase.compileStatement(DELETE_STATEMENT);
            }
            for (HaloContentInstance instance : instances) {
                mDeleteStatement.clearBindings();
                ORMUtils.bindStringOrNull(mDeleteStatement, 1, instance.getItemId());
                mDeleteStatement.executeUpdateDelete();
            }
        }

        /**
         * Releases the memory taken from the statement.
         */
        private void release() {
            if (mInsertOrFailStatement != null) {
                mInsertOrFailStatement.close();
                mInsertOrFailStatement = null;
            }
            if (mInsertOrReplaceStatement != null) {
                mInsertOrReplaceStatement.close();
                mInsertOrReplaceStatement = null;
            }
            if (mDeleteStatement != null) {
                mDeleteStatement.close();
                mDeleteStatement = null;
            }
        }

        /**
         * Inserts the statement into the database binding the values.
         *
         * @param statement  The statement.
         * @param instance   The instance.
         * @param moduleName The module name.
         * @param syncDate   The sync date.
         * @return The line of the value inserted.
         */
        private long insert(@NonNull SQLiteStatement statement, @NonNull HaloContentInstance instance, @NonNull String moduleName, @NonNull Date syncDate) {
            statement.clearBindings();
            ORMUtils.bindStringOrNull(statement, 1, instance.getItemId());
            ORMUtils.bindStringOrNull(statement, 2, instance.getModuleId());
            ORMUtils.bindStringOrNull(statement, 3, instance.getName());
            String values = instance.getValues() != null ? instance.getValues().toString() : null;
            ORMUtils.bindStringOrNull(statement, 4, values);
            ORMUtils.bindStringOrNull(statement, 5, instance.getAuthor());
            ORMUtils.bindDateOrNull(statement, 6, instance.getPublishedDate());
            ORMUtils.bindDateOrNull(statement, 7, instance.getCreatedDate());
            ORMUtils.bindDateOrNull(statement, 8, instance.getLastUpdate());
            ORMUtils.bindDateOrNull(statement, 9, instance.getRemoveDate());
            ORMUtils.bindDateOrNull(statement, 10, syncDate);
            ORMUtils.bindStringOrNull(statement, 11, moduleName);
            return statement.executeInsert();
        }
    }
}
