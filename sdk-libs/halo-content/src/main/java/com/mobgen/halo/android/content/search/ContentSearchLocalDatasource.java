package com.mobgen.halo.android.content.search;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;

import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.spec.HaloContentContract;
import com.mobgen.halo.android.content.utils.HaloContentHelper;
import com.mobgen.halo.android.framework.api.HaloStorageApi;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.storage.database.HaloDataLite;
import com.mobgen.halo.android.framework.storage.database.dsl.ORMUtils;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Delete;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Select;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageParseException;
import com.mobgen.halo.android.sdk.core.management.models.Device;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mobgen.halo.android.content.spec.HaloContentContract.ContentSearch;
import static com.mobgen.halo.android.content.spec.HaloContentContract.ContentSearchQuery;

/**
 * @hide Dao object to handle the access from the database.
 */
public class ContentSearchLocalDatasource {

    /**
     * Provides the storage access.
     */
    private HaloStorageApi mStorage;

    /**
     * Creates the local data source to search instances.
     *
     * @param storage The storage instance.
     */
    public ContentSearchLocalDatasource(@NonNull HaloStorageApi storage) {
        mStorage = storage;
    }

    /**
     * Provides the cursor for the given query.
     *
     * @param query The query.
     * @return The cursor.
     * @throws HaloStorageException Storage exception.
     */
    @NonNull
    public Cursor findByQuery(@NonNull SearchQuery query) throws HaloStorageException {
        Cursor optionsData = null;
        //Bring the data to life
        try {
            //Clear previous items based on the EXPIRES_ON
            clearExpiredSearchItems(mStorage.db().getDatabase());

            //Fetch
            optionsData = Select.columns(ContentSearchQuery.INSTANCE_IDS)
                    .from(ContentSearchQuery.class)
                    .where(ContentSearchQuery.QUERY_ID)
                    .eq(query.createHash(mStorage.framework().parser()))
                    .on(mStorage.db(), "Queries the search table to get the ids separated by commas from the previous search");

            //Get all the ids from the search row
            String[] ids = new String[0];
            if (optionsData.moveToFirst()) {
                String idsString = optionsData.getString(optionsData.getColumnIndexOrThrow(ContentSearchQuery.INSTANCE_IDS));
                if (idsString != null) {
                    ids = idsString.split(",");
                }
            }
            optionsData.close();

            // Select the ids based on the search
            return Select.all().from(ContentSearch.class)
                    .where(ContentSearch.HASH_ID)
                    .in(ids)
                    .order(HaloContentContract.ROW_ID, Select.ORDER_ASC)
                    .on(mStorage.db(), "Queries the found instances in the search table by hash id");
        } catch (HaloParsingException e) {
            throw new HaloStorageParseException("Error creating options hash.", e);
        } finally {
            if(optionsData!=null && !optionsData.isClosed()){
                optionsData.close();
            }
        }
    }

    /**
     * Saves the current query and the result that this query produces.
     *
     * @param query     The query.
     * @param instances The instances.
     * @throws HaloStorageGeneralException Error while operating with the query.
     */
    public void save(@NonNull final SearchQuery query, @NonNull final Paginated<HaloContentInstance> instances) throws HaloStorageGeneralException {
        mStorage.db().transaction(new HaloDataLite.HaloDataLiteTransaction() {
            @Override
            public void onTransaction(@NonNull SQLiteDatabase database) throws HaloStorageException {
                long expireDate = new Date().getTime() + query.getTTL();
                //Clear previous items based on the EXPIRES_ON
                clearExpiredSearchItems(database);
                //Insert the new values
                try {
                    insertSearch(createContentValues(query, instances, expireDate, mStorage.framework().parser()), database);
                } catch (HaloParsingException e) {
                    throw new HaloStorageParseException("Error while creating the content values for one item.", e);
                }
            }
        });
    }

    /**
     * Clears all the expired items from the databse.
     *
     * @param database The database.
     */
    private void clearExpiredSearchItems(@NonNull SQLiteDatabase database) {
        long requestTime = new Date().getTime();

        //Delete search items expired
        Delete.from(ContentSearchQuery.class)
                .where(ContentSearchQuery.EXPIRES_ON)
                .lt(requestTime)
                .on(database, "Removes all the expired search rows");

        //Delete general content items expired
        Delete.from(ContentSearch.class)
                .where(ContentSearch.EXPIRES_ON)
                .lt(requestTime)
                .on(database, "Removes all the expired general content instances");
    }

    /**
     * Inserts the search into the database.
     *
     * @param contentValues The values that will be inserted.
     * @param database      The database on which they will be inserted.
     * @throws HaloStorageGeneralException The general exception in case of errors.
     */
    private void insertSearch(final Pair<ContentValues, List<ContentValues>> contentValues, SQLiteDatabase database) throws HaloStorageGeneralException {
        //Insert search row and instance rows.
        database.insertWithOnConflict(ORMUtils.getTableName(ContentSearchQuery.class), null, contentValues.first, SQLiteDatabase.CONFLICT_REPLACE);
        //Insert each row.
        for (ContentValues value : contentValues.second) {
            try {
                database.insertOrThrow(ORMUtils.getTableName(ContentSearch.class), null, value);
            } catch (SQLiteException e) {
                // There was a conflict so lets check ttl values to update to the latest
                // This guarantees the case of two search processes with same instance versions are not deleted
                // conflicting between each other
                Cursor conflicted = Select.columns(ContentSearch.EXPIRES_ON)
                        .from(ContentSearch.class)
                        .where(ContentSearch.HASH_ID)
                        .eq(value.get(ContentSearch.HASH_ID))
                        .on(database, "Get conflicted hash that is being stored");
                if (conflicted.moveToFirst()) {
                    long storedTTL = conflicted.getLong(conflicted.getColumnIndexOrThrow(ContentSearch.EXPIRES_ON));
                    //If the stored ttl is smaller, upgrade the entity
                    conflicted.close();
                    if (storedTTL < value.getAsLong(ContentSearch.EXPIRES_ON)) {
                        database.insertWithOnConflict(ORMUtils.getTableName(ContentSearch.class), null, value, SQLiteDatabase.CONFLICT_REPLACE);
                    }
                }
            }
        }
    }

    /**
     * Create the content values for the options and the data provided.
     *
     * @param options    The options.
     * @param data       The data returned by the search.
     * @param expireDate The date of expiration.
     * @param parser     The parser.
     * @return The pair with the first item containing the options item, and as second value the list of content
     * values for available.
     * @throws HaloStorageGeneralException The storage error while creating the algorithm.
     */
    private Pair<ContentValues, List<ContentValues>> createContentValues(SearchQuery options, Paginated<HaloContentInstance> data, long expireDate, @NonNull Parser.Factory parser) throws HaloStorageException, HaloParsingException {
        try {
            ContentValues values = new ContentValues();
            //Create the content values for the search options
            values.put(ContentSearchQuery.QUERY_ID, options.createHash(parser));
            values.put(ContentSearchQuery.DEBUG_QUERY, options.serializerFrom(parser).convert(options));
            values.put(ContentSearchQuery.PAGINATION_PAGE, data.getPage());
            values.put(ContentSearchQuery.PAGINATION_LIMIT, data.getLimit());
            values.put(ContentSearchQuery.PAGINATION_COUNT, data.getCount());
            values.put(ContentSearchQuery.EXPIRES_ON, expireDate);
            List<HaloContentInstance> instances = data.data();
            List<ContentValues> itemsContentValues = new ArrayList<>();
            //Create the content values for the instances
            List<String> ids = new ArrayList<>(instances.size());
            for (HaloContentInstance instance : instances) {
                ids.add(HaloContentHelper.createDatabaseId(instance));
                ContentValues instanceValue = HaloContentHelper.createSearchContentValues(instance, new ContentValues(), expireDate);
                itemsContentValues.add(instanceValue);
            }
            values.put(ContentSearchQuery.INSTANCE_IDS, TextUtils.join(",", ids));
            return new Pair<>(values, itemsContentValues);
        } catch (IOException e) {
            throw new HaloParsingException("Problems while generating the hash of the query", e);
        }
    }

    /**
     * Clears the search from the search tables.
     * @throws HaloStorageGeneralException
     */
    public void clearSearch() throws HaloStorageGeneralException {
        mStorage.db().transaction(new HaloDataLite.HaloDataLiteTransaction() {
            @Override
            public void onTransaction(@NonNull SQLiteDatabase database) throws HaloStorageException {
                Delete.from(HaloContentContract.ContentSearchQuery.class).on(database, "Remove all the search queries");
                Delete.from(HaloContentContract.ContentSearch.class).on(database, "Remove all the search instances");
            }
        });
    }
}
