package com.mobgen.halo.android.cache.adapters.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.mobgen.halo.android.cache.Cache;
import com.mobgen.halo.android.cache.CacheConverter;
import com.mobgen.halo.android.cache.CacheException;
import com.mobgen.halo.android.cache.CacheType;
import com.mobgen.halo.android.cache.Transaction;
import com.mobgen.halo.android.cache.adapters.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mobgen.halo.android.cache.Assert.notNull;

public class SQLiteCache extends Cache<String> {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "sqlite_cache";

    @NonNull
    private SQLiteOpenHelper mOpenHelper;
    private SQLiteDatabase mDatabase;
    private final long mTtl;
    private boolean mShouldCompress;

    private SQLiteCache(@NonNull Builder builder) {
        super(builder.mConverter);
        notNull(builder.mContext, "context");
        mOpenHelper = new DatabaseHelper(builder.mContext, builder.mCacheName, new SQLiteErrorHandler());
        mTtl = builder.mTtl;
        mShouldCompress = builder.mShouldCompress;
    }

    public static Builder builder(@NonNull Context context) {
        return new Builder(context);
    }

    @Override
    public void init() throws CacheException {
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
        mDatabase = mOpenHelper.getWritableDatabase();
    }

    @Override
    public void dropCache() {
        Contract.dropAndCreate(mDatabase);
        notifyDropped();
    }

    @Override
    protected void beginTransaction(@NonNull Transaction transaction) {
        mDatabase.beginTransaction();
    }

    @Override
    protected void onTransactionSuccess(Transaction transaction) {
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    @Override
    protected void onTransactionError(Transaction transaction) {
        mDatabase.endTransaction();
    }

    @NonNull
    @Override
    public <T> String[] insert(@NonNull List<T> data, @NonNull String groupId, @NonNull IdGenerator<T> idGenerator, @NonNull Transaction<String> transaction) throws CacheException {
        String[] insertions = new String[data.size()];
        ContentValues values = new ContentValues();
        long expirationDate = createExpirationDate();
        int index = 0;
        for (T item : data) {
            insertions[index++] = insert(item, groupId, idGenerator.idFromObject(item), expirationDate, values, transaction);
            values.clear();
        }
        return insertions;
    }

    @NonNull
    @Override
    public String insert(@Nullable Object data, @NonNull String groupId, @NonNull String itemId, @NonNull Transaction<String> transaction) throws CacheException {
        long expirationDate = createExpirationDate();
        return insert(data, groupId, itemId, expirationDate, new ContentValues(), transaction);
    }

    @Override
    public void delete(@NonNull String groupId, @NonNull String id, @NonNull Transaction<String> transaction) throws CacheException {
        Contract.delete(mDatabase, groupId, id);
        notifyDeleted(groupId, id, transaction);
    }

    @Override
    public void delete(@NonNull String groupId, @NonNull Transaction<String> transaction) throws CacheException {
        Contract.delete(mDatabase, groupId);
        notifyDeleted(groupId, transaction);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(CacheType<T> clazz, @NonNull String groupId, @NonNull String id) throws CacheException {
        clearExpired(groupId);
        Cursor cursor = Contract.find(mDatabase, groupId, id);
        T result = null;
        if (cursor.moveToFirst()) {
            result = deserialize((Class<T>) clazz.rawType, cursor);
        }
        return result;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> get(CacheType<T> clazz, @NonNull String groupId, @NonNull String... ids) throws CacheException {
        clearExpired(groupId);
        return deserializeList((Class<T>) clazz.rawType, Contract.find(mDatabase, groupId, ids));
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> get(CacheType<T> clazz, @NonNull String groupId) throws CacheException {
        clearExpired(groupId);
        return deserializeList((Class<T>) clazz.rawType, Contract.find(mDatabase, groupId));
    }

    @Override
    public long ttl() {
        return mTtl;
    }

    private String insert(@Nullable Object item, @NonNull String groupId, @NonNull String itemId, long expirationDate, @NonNull ContentValues values, Transaction transaction) throws CacheException {
        String id = null;
        if (item != null) {
            Contract.insert(mDatabase,
                    serialize(item),
                    groupId,
                    itemId,
                    expirationDate,
                    values);
            notifyInserted(groupId, itemId, item, expirationDate, transaction);
            id = itemId;
        }
        return id;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    private <T> List<T> deserializeList(@NonNull Class<T> clazz, @NonNull Cursor cursor) throws CacheException {
        List<T> dataList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                dataList.add(deserialize(clazz, cursor));
            } while (cursor.moveToNext());
        }
        return dataList;
    }

    @SuppressWarnings("unchecked")
    private <T> T deserialize(@NonNull Class<T> clazz, @NonNull Cursor cursor) throws CacheException {
        CacheConverter<String, T> converter = (CacheConverter<String, T>) converter().deserializeCacheConverter(clazz, null, this);
        T item = null;
        if (converter != null) {
            String data = cursor.getString(cursor.getColumnIndexOrThrow(Contract.DATA));
            item = converter.convert(Utils.decompress(data, mShouldCompress));
        }
        return item;
    }

    @SuppressWarnings("unchecked")
    private String serialize(Object item) throws CacheException {
        CacheConverter<Object, String> converter = (CacheConverter<Object, String>) converter().serializeCacheConverter(item.getClass(), null, null, this);
        String serialized = null;
        if (converter != null) {
            serialized = Utils.compress(converter.convert(item), mShouldCompress);
        }
        return serialized;
    }

    private long createExpirationDate() {
        long expiration = UNLIMITED_TTL;
        if (mTtl != UNLIMITED_TTL) {
            expiration = new Date().getTime() + mTtl;
        }
        return expiration;
    }

    @Override
    protected void clearExpired(@NonNull String groupId) {
        if (mTtl != UNLIMITED_TTL) {
            Contract.deleteExpired(mDatabase, groupId);
        }
    }

    private class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context, String cacheName, DatabaseErrorHandler errorHandler) {
            super(context, cacheName, null, VERSION, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Contract.create(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            dropCache();
        }
    }

    private class SQLiteErrorHandler implements DatabaseErrorHandler {

        @Override
        public void onCorruption(SQLiteDatabase dbObj) {
            dropCache();
        }
    }

    public static class Contract {

        private Contract() {
            //Do not allow instances
        }

        public static final String TABLE_NAME = "general_items";

        public static final String GROUP_ID = "group_id";
        public static final String ITEM_ID = "item_id";
        public static final String DATA = "data";
        public static final String EXPIRATION_DATE = "ttl";
        public static final String UPDATE_DATE = "update_date";

        public static void create(@NonNull SQLiteDatabase database) {
            String statement = String.format("CREATE TABLE IF NOT EXISTS %s (" +
                            "%s TEXT, " +
                            "%s TEXT, " +
                            "%s TEXT, " +
                            "%s DATETIME, " +
                            "%s DATETIME DEFAULT CURRENT_TIMESTAMP," +
                            " PRIMARY KEY (%s, %s));",
                    TABLE_NAME, //Table name
                    GROUP_ID,
                    ITEM_ID,
                    DATA,
                    EXPIRATION_DATE,
                    UPDATE_DATE,
                    GROUP_ID, ITEM_ID); //Primary keys
            database.execSQL(statement);
        }

        public static void dropAndCreate(@NonNull SQLiteDatabase database) {
            database.delete(TABLE_NAME, null, null);
            create(database);
        }

        public static void insert(@NonNull SQLiteDatabase database, @Nullable String serializedData, @NonNull String groupId, @NonNull String id, long expireDate, @NonNull ContentValues contentValues) {
            contentValues.put(GROUP_ID, groupId);
            contentValues.put(ITEM_ID, id);
            contentValues.put(EXPIRATION_DATE, expireDate);
            contentValues.put(DATA, serializedData);
            database.insertWithOnConflict(TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        }

        public static void deleteExpired(@NonNull SQLiteDatabase database, @NonNull String groupId) {
            database.delete(TABLE_NAME, String.format("`%s` = ? AND `%s` < ? AND `%s` != -1", GROUP_ID, EXPIRATION_DATE, EXPIRATION_DATE), new String[]{groupId, String.valueOf(new Date().getTime())});
        }

        public static Cursor find(@NonNull SQLiteDatabase database, @NonNull String groupId, @NonNull String id) {
            String statement = String.format("SELECT * FROM `%s` WHERE `%s` = ? AND `%s` = ?;",
                    TABLE_NAME,
                    GROUP_ID,
                    ITEM_ID);
            return database.rawQuery(statement, new String[]{groupId, id});
        }

        public static Cursor find(@NonNull SQLiteDatabase database, @NonNull String groupId, @NonNull String... ids) {
            String idTexts = "'" + TextUtils.join("','", ids) + "'";
            String statement = String.format("SELECT * FROM `%s` WHERE `%s` = ? AND `%s` IN (%s)",
                    TABLE_NAME,
                    GROUP_ID,
                    ITEM_ID,
                    idTexts);
            return database.rawQuery(statement, new String[]{groupId});
        }

        public static Cursor find(@NonNull SQLiteDatabase database, @NonNull String groupId) {
            String statement = String.format("SELECT * FROM `%s` WHERE `%s` = ?;",
                    TABLE_NAME,
                    GROUP_ID);
            return database.rawQuery(statement, new String[]{groupId});
        }

        public static void delete(@NonNull SQLiteDatabase database, @NonNull String groupId) {
            database.delete(TABLE_NAME, String.format("`%s` = ?", GROUP_ID), new String[]{groupId});
        }

        public static void delete(@NonNull SQLiteDatabase database, @NonNull String groupId, @NonNull String id) {
            database.delete(TABLE_NAME, String.format("`%s` = ? AND `%s` = ?", GROUP_ID, ITEM_ID), new String[]{groupId, id});
        }
    }

    public static class Builder {

        private Context mContext;
        private CacheConverter.Factory<String> mConverter;
        private String mCacheName;
        private long mTtl;
        private boolean mShouldCompress;

        private Builder(@NonNull Context context) {
            mContext = context.getApplicationContext();
            mTtl = UNLIMITED_TTL;
            mCacheName = DATABASE_NAME;
            mShouldCompress = true;
        }

        @NonNull
        public Builder converterFactory(CacheConverter.Factory<String> factory) {
            mConverter = factory;
            return this;
        }

        @NonNull
        public Builder ttl(long ttl, TimeUnit unit) {
            mTtl = unit.toMillis(ttl);
            return this;
        }

        @NonNull
        public Builder name(@NonNull String cacheName) {
            mCacheName = cacheName;
            return this;
        }

        @NonNull
        public Builder shouldCompress(boolean shouldCompress) {
            mShouldCompress = shouldCompress;
            return this;
        }

        @NonNull
        public SQLiteCache build() {
            notNull(mCacheName, "cacheName");
            notNull(mConverter, "converterFactory");
            return new SQLiteCache(this);
        }
    }
}