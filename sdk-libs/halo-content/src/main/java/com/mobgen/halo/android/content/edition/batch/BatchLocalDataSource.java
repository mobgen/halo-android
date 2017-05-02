package com.mobgen.halo.android.content.edition.batch;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.BatchOperations;
import com.mobgen.halo.android.content.models.BatchOperator;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.spec.HaloContentContract;
import com.mobgen.halo.android.framework.api.HaloStorageApi;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;
import com.mobgen.halo.android.framework.storage.database.HaloDataLite;
import com.mobgen.halo.android.framework.storage.database.dsl.ORMUtils;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Select;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.sdk.api.Halo;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Local data source for batch content manipulation.
 */
public class BatchLocalDataSource {

    /**
     * The storage api.
     */
    private HaloStorageApi mStorage;

    /**
     * Constructor datasource.
     *
     * @param storageApi The storage api.
     */
    public BatchLocalDataSource(@NonNull HaloStorageApi storageApi) {
        mStorage = storageApi;
    }

    /**
     * Get all pending batch operations from local data source.
     *
     * @return Pending batch operations.
     * @throws HaloStorageGeneralException
     */
    @NonNull
    public BatchOperations getPendingBatchOperations() throws HaloStorageGeneralException {
        Cursor cursor = null;
        BatchOperations.Builder advancedBatchBuilder = new BatchOperations.Builder();
        try {
            cursor = Select.columns(HaloContentContract.Batch.CONTENT_INSTANCE, HaloContentContract.Batch.OPERATION)
                    .from(HaloContentContract.Batch.class)
                    .on(mStorage.db(), "Query to get all content instances from erroneous batch");

            List<HaloContentInstance> instances = new ArrayList<>();
            List<String> operations = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    HaloContentInstance instance = HaloContentInstance.deserialize(
                            cursor.getString(cursor.getColumnIndex(cursor.getColumnNames()[0])),
                            Halo.instance().framework().parser());
                    instances.add(instance);
                    operations.add(cursor.getString(cursor.getColumnIndex(cursor.getColumnNames()[1])));

                } while (cursor.moveToNext());

                for (int i = 0; i < instances.size(); i++) {
                    if (operations.get(i).equals(BatchOperator.TRUNCATE)) {
                        advancedBatchBuilder.truncate(instances.get(i));
                    }
                    if (operations.get(i).equals(BatchOperator.CREATE)) {
                        advancedBatchBuilder.create(instances.get(i));
                    }
                    if (operations.get(i).equals(BatchOperator.CREATEORUPDATE)) {
                        advancedBatchBuilder.createOrUpdate(instances.get(i));
                    }
                    if (operations.get(i).equals(BatchOperator.UPDATE)) {
                        advancedBatchBuilder.update(instances.get(i));
                    }
                    if (operations.get(i).equals(BatchOperator.DELETE)) {
                        advancedBatchBuilder.delete(instances.get(i));
                    }
                }
            }
        } catch (HaloParsingException parseException) {
            Halog.d(getClass(), "Parsing instance error");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return advancedBatchBuilder.build();
    }

    /**
     * Save pending batch operations on local data source.
     *
     * @param batchOperations The batch operations to store.
     * @throws HaloStorageGeneralException
     */
    public void saveErrors(@NonNull final BatchOperations batchOperations) throws HaloStorageGeneralException {
        AssertionUtils.notNull(batchOperations, "batchOperations");
        final BatchLocalDataSource.HaloContentBatchQueryManager queryManager = new BatchLocalDataSource.HaloContentBatchQueryManager(mStorage.db().getDatabase());
        mStorage.db().transaction(new HaloDataLite.HaloDataLiteTransaction() {
            @Override
            public void onTransaction(@NonNull SQLiteDatabase database) throws HaloStorageException {
                Halog.d(getClass(), "Insert Batch in progress...");
                try {
                    queryManager.insertOrReplace(batchOperations);
                } catch (HaloParsingException e) {
                    Halog.d(getClass(), "Parsing instance error");
                }
                Halog.d(getClass(), "Insert Batch ends");
            }
        });
        queryManager.release();
    }

    /**
     * Remove pending batch operation from local data source.
     *
     * @param instances The instances to remove from database.
     * @throws HaloStorageGeneralException
     */
    public boolean deleteErrors(@NonNull final HaloContentInstance... instances) throws HaloStorageGeneralException {
        AssertionUtils.notNull(instances, "instances");
        final boolean[] result = {false};
        final BatchLocalDataSource.HaloContentBatchQueryManager queryManager = new BatchLocalDataSource.HaloContentBatchQueryManager(mStorage.db().getDatabase());
        mStorage.db().transaction(new HaloDataLite.HaloDataLiteTransaction() {
            @Override
            public void onTransaction(@NonNull SQLiteDatabase database) throws HaloStorageException {
                Halog.d(getClass(), "Delete from Batch in progress...");
                queryManager.delete(instances);
                result[0] = true;
            }
        });
        queryManager.release();
        return result[0];
    }

    /**
     * Batch operation object that saves the queries for performance improvements.
     * Remember to call release to free the memory of the requests stored.
     */
    private static class HaloContentBatchQueryManager {

        /**
         * Generic insert to add data into the database.
         */
        private static final String INSERT = " (" +
                HaloContentContract.Batch.ID + "," +
                HaloContentContract.Batch.LAST_ATTEMPT + "," +
                HaloContentContract.Batch.CONTENT_INSTANCE + "," +
                HaloContentContract.Batch.OPERATION +
                ") VALUES (?,?,?,?);";

        /**
         * Insert or replace statement.
         */
        private static final String INSERT_REPLACE_STATEMENT = "INSERT OR REPLACE INTO " + ORMUtils.getTableName(HaloContentContract.Batch.class) + INSERT;

        /**
         * Delete statement name.
         */
        private static final String DELETE_STATEMENT = "DELETE FROM " +
                ORMUtils.getTableName(HaloContentContract.Batch.class) +
                " WHERE " +
                HaloContentContract.Batch.ID +
                " = ?;";

        /**
         * The database instance.
         */
        private SQLiteDatabase mDatabase;
        /*
         * Inserts or replace statement to update the instances.
         */
        private SQLiteStatement mInsertOrReplaceStatement;
        /**
         * Delete statement to remove the instances.
         */
        private SQLiteStatement mDeleteStatement;

        /**
         * Constructor for content batch query manager.
         *
         * @param database The database
         */
        private HaloContentBatchQueryManager(@NonNull SQLiteDatabase database) {
            AssertionUtils.notNull(database, "database");
            mDatabase = database;
        }


        /**
         * Insert or update batch operations on database.
         *
         * @param batchOperations The database operations to perfom.
         * @throws HaloStorageGeneralException
         * @throws HaloParsingException
         */
        private void insertOrReplace(@NonNull BatchOperations batchOperations) throws HaloStorageGeneralException, HaloParsingException {
            AssertionUtils.notNull(batchOperations, "batchOperations");
            if (mInsertOrReplaceStatement == null) {
                mInsertOrReplaceStatement = mDatabase.compileStatement(INSERT_REPLACE_STATEMENT);
            }
            Date attempt = new Date();
            if (batchOperations.getTruncate() != null) {
                for (HaloContentInstance instance : batchOperations.getTruncate()) {
                    insert(mInsertOrReplaceStatement, instance, BatchOperator.TRUNCATE, attempt);
                }
            }
            if (batchOperations.getCreated() != null) {
                for (HaloContentInstance instance : batchOperations.getCreated()) {
                    insert(mInsertOrReplaceStatement, instance, BatchOperator.CREATE, attempt);
                }
            }
            if (batchOperations.getCreatedOrUpdated() != null) {
                for (HaloContentInstance instance : batchOperations.getCreatedOrUpdated()) {
                    insert(mInsertOrReplaceStatement, instance, BatchOperator.CREATEORUPDATE, attempt);
                }
            }
            if (batchOperations.getUpdated() != null) {
                for (HaloContentInstance instance : batchOperations.getUpdated()) {
                    insert(mInsertOrReplaceStatement, instance, BatchOperator.UPDATE, attempt);
                }
            }
            if (batchOperations.getDeleted() != null) {
                for (HaloContentInstance instance : batchOperations.getDeleted()) {
                    insert(mInsertOrReplaceStatement, instance, BatchOperator.DELETE, attempt);
                }
            }
        }

        /**
         * Deletes a list of instances.
         *
         * @param instances The instances.
         */
        private void delete(@NonNull HaloContentInstance... instances) {
            AssertionUtils.notNull(instances, "instances");
            if (mDeleteStatement == null) {
                mDeleteStatement = mDatabase.compileStatement(DELETE_STATEMENT);
            }
            for (HaloContentInstance instance : instances) {
                mDeleteStatement.clearBindings();
                String hashId;
                try {
                    hashId = HaloUtils.sha1(instance.toString());
                } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                    hashId = null;
                }
                ORMUtils.bindStringOrNull(mDeleteStatement, 1, hashId);
                mDeleteStatement.executeUpdateDelete();
            }
        }

        /**
         * Inserts the statement into the database binding the values.
         *
         * @param statement   The statement.
         * @param instance    The instance.
         * @param operation   The pending operation.
         * @param lastAttempt The timestamp of the operation.
         * @return The line of the value inserted.
         * @throws HaloStorageGeneralException
         * @throws HaloParsingException
         */
        private long insert(@NonNull SQLiteStatement statement, @NonNull HaloContentInstance instance, @BatchOperator.BatchOperation String operation, @NonNull Date lastAttempt) throws HaloStorageGeneralException, HaloParsingException {
            statement.clearBindings();
            String hashId;
            try {
                hashId = HaloUtils.sha1(instance.toString());
            } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                hashId = null;
            }
            ORMUtils.bindStringOrNull(statement, 1, hashId);
            ORMUtils.bindDateOrNull(statement, 2, lastAttempt);
            String contentInstace = HaloContentInstance.serialize(instance, Halo.instance().framework().parser());
            ORMUtils.bindStringOrNull(statement, 3, contentInstace);
            ORMUtils.bindStringOrNull(statement, 4, operation);
            return statement.executeInsert();
        }

        /**
         * Releases the memory taken from the statement.
         */
        private void release() {
            if (mInsertOrReplaceStatement != null) {
                mInsertOrReplaceStatement.close();
                mInsertOrReplaceStatement = null;
            }
            if (mDeleteStatement != null) {
                mDeleteStatement.close();
                mDeleteStatement = null;
            }
        }
    }
}
