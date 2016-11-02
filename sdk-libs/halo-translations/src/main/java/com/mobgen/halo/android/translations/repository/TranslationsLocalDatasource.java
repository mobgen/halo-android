package com.mobgen.halo.android.translations.repository;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.content.spec.HaloContentContract.ContentSync;
import com.mobgen.halo.android.framework.api.HaloStorageApi;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.storage.database.HaloDataLite;
import com.mobgen.halo.android.framework.storage.database.dsl.ORMUtils;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Delete;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Select;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;
import com.mobgen.halo.android.translations.spec.HaloTranslationsContract;
import com.mobgen.halo.android.translations.spec.HaloTranslationsContract.Translations;

import org.json.JSONException;
import org.json.JSONObject;

import static com.mobgen.halo.android.framework.common.utils.AssertionUtils.notNull;

/**
 * @hide Internal data source that manages the access to the local database to bring
 * the translations.
 */
public class TranslationsLocalDatasource {

    /**
     * The translations alias.
     */
    private static final String TRANSLATIONS_ATTACH_NAME = "translations";

    /**
     * The database name with the attached translations.
     */
    private static final String TRANSLATIONS_DATABASE_ATTACHED = TRANSLATIONS_ATTACH_NAME + "." + ORMUtils.getTableName(Translations.class);

    /**
     * The prefix for the translations preferences stored.
     */
    private static final String PREFIX_PREFERENCES_TRANSLATIONS = "translations_";

    /**
     * Selects all the items not in the sync table.
     */
    private static final String SELECT_ITEMS_NOT_IN_SYNC_TABLE = "SELECT " + Translations.ITEM_ID +
            " FROM " + TRANSLATIONS_DATABASE_ATTACHED +
            " LEFT JOIN " + ORMUtils.getTableName(ContentSync.class) + " ON " + ContentSync.ID + " = " + Translations.ITEM_ID +
            " WHERE " + ContentSync.ID + " IS NULL";

    /**
     * Query to delete items that remain in the translations API but are not available in the sync, so they were deleted.
     */
    private static final String DELETE_REMOVED_IN_SYNC_QUERY = "DELETE FROM " + TRANSLATIONS_DATABASE_ATTACHED +
            " WHERE " + Translations.ITEM_ID + " IN (" + SELECT_ITEMS_NOT_IN_SYNC_TABLE + ");";

    /**
     * The storage api for the translations. It contains the database
     * definition where all the translations will be stored.
     */
    private HaloStorageApi mTranslationsStorageApi;

    /**
     * Creates the local data source.
     *
     * @param storageApi The storage api.
     */
    public TranslationsLocalDatasource(@NonNull HaloStorageApi storageApi) {
        notNull(storageApi, "storageApi");
        mTranslationsStorageApi = storageApi;
        //Ensure translations exists just in case
        mTranslationsStorageApi.db().ensureInitialized();
    }

    /**
     * Provides the cursor with the translations brought,
     *
     * @param moduleName The module name of the translations that we are trying to fetch.
     * @param locale     The locale definition for this translations.
     * @return The cursor.
     */
    @NonNull
    public Cursor getTranslations(@NonNull String moduleName, @HaloLocale.LocaleDefinition @NonNull String locale) {
        notNull(moduleName, "moduleName");
        notNull(locale, "locale");
        return Select.columns(Translations.KEY, Translations.VALUE)
                .from(Translations.class)
                .where(Translations.MODULE_NAME)
                .eq(moduleName)
                .and(Translations.LOCALE)
                .eq(locale)
                .on(mTranslationsStorageApi.db(), "Fetch the translations with the given language and module");
    }

    /**
     * Syncs the translations api with the content storage api.
     *
     * @param contentStorageApi The content storage api to sync the translations with.
     * @param moduleName        The module name that will be synced.
     * @param locale            The locale definition that will be synced.
     * @param keyName           The key name of the values in the json content instance.
     * @param valueName         The value name of the json content instance.
     * @throws HaloStorageGeneralException
     */
    public void syncTranslationsWith(@NonNull HaloStorageApi contentStorageApi, @NonNull String moduleName, @HaloLocale.LocaleDefinition @NonNull String locale, @NonNull String keyName, @NonNull String valueName) throws HaloStorageGeneralException {
        notNull(contentStorageApi, "contentStorageApi");
        notNull(moduleName, "moduleName");
        notNull(keyName, "keyName");
        notNull(valueName, "valueName");
        notNull(locale, "locale");

        final Long lastSyncTimestamp = mTranslationsStorageApi.prefs().getLong(getSyncNameForPrefs(moduleName), 0L);
        //Get new synced data
        Cursor cursor = Select.columns(
                ContentSync.ID,
                ContentSync.MODULE_NAME,
                ContentSync.VALUES,
                ContentSync.LAST_SYNCED)
                .from(ContentSync.class)
                .where(ContentSync.MODULE_NAME)
                .eq(moduleName)
                .and(ContentSync.LAST_SYNCED)
                .gt(lastSyncTimestamp)
                .on(contentStorageApi.db());

        //Save all new items in translations storage
        Long lastDate = resyncCursor(contentStorageApi, cursor, keyName, valueName, moduleName, locale, lastSyncTimestamp);
        if (lastDate != 0L) {
            mTranslationsStorageApi.prefs().edit().putLong(getSyncNameForPrefs(moduleName), lastDate).commit();
        }
    }

    /**
     * Clears the stored module from translations.
     *
     * @param moduleName The module id to remove from translations.
     */
    public void clearTranslations(@NonNull String moduleName) {
        notNull(moduleName, "moduleName");
        Delete.from(Translations.class)
                .where(Translations.MODULE_NAME)
                .eq(moduleName)
                .on(mTranslationsStorageApi.db(), "Removing the translations for module " + moduleName);
        mTranslationsStorageApi.prefs().edit().remove(getSyncNameForPrefs(moduleName)).commit();
    }

    /**
     * Provides the last synced timestamp for the module id provided.
     *
     * @param moduleName The module name.
     * @return The timestamp.
     */
    @Nullable
    public Long getLastSyncTimestamp(String moduleName) {
        return mTranslationsStorageApi.prefs().getLong(getSyncNameForPrefs(moduleName), null);
    }

    /**
     * Saves the items for translations that are not in sync.
     *
     * @param contentStorageApi The api.
     * @param cursor            The cursor.
     * @param keyName           The key name for the values objects.
     * @param valueName         The value name.
     * @param moduleName        The module id.
     * @param locale            The locale.
     * @return The last time sync considered.
     * @throws HaloStorageGeneralException The storage exception.
     */
    private Long resyncCursor(@NonNull final HaloStorageApi contentStorageApi, @NonNull final Cursor cursor, @NonNull final String keyName, @NonNull final String valueName, @NonNull final String moduleName, @NonNull final String locale, @NonNull Long lastTimestampStored) throws HaloStorageGeneralException {
        final Long[] lastSyncTimestamp = new Long[]{lastTimestampStored};

        //Attach content and translations
        contentStorageApi.db().attachDatabase(HaloTranslationsContract.HALO_TRANSLATIONS_STORAGE, TRANSLATIONS_ATTACH_NAME);
        //Sync the tables
        contentStorageApi.db().transaction(new HaloDataLite.HaloDataLiteTransaction() {
            @Override
            public void onTransaction(@NonNull SQLiteDatabase database) throws HaloStorageException {
                if (cursor.moveToFirst()) {
                    //Start syncing the tables
                    do {
                        String values = cursor.getString(cursor.getColumnIndexOrThrow(ContentSync.VALUES));
                        String itemId = cursor.getString(cursor.getColumnIndexOrThrow(ContentSync.ID));
                        Long currentTimestamp = cursor.getLong(cursor.getColumnIndexOrThrow(ContentSync.LAST_SYNCED));
                        lastSyncTimestamp[0] = Math.max(lastSyncTimestamp[0], currentTimestamp);
                        if (values != null) {
                            try {
                                JSONObject json = new JSONObject(values);
                                String key = json.optString(keyName);
                                String value = json.optString(valueName);
                                insert(database, key, value, itemId, moduleName, locale);
                            } catch (JSONException e) {
                                throw new HaloStorageGeneralException("Error parsing one of the values. Key name or value name are not well defined for module name " + moduleName, e);
                            }
                        }
                    } while (cursor.moveToNext());
                }
                //Delete items removed in the sync table to keep this in sync
                Halog.d(TranslationsLocalDatasource.this.getClass(), "Remove translations not in sync with general content");
                database.execSQL(DELETE_REMOVED_IN_SYNC_QUERY);
            }
        });
        //Detach content and translations
        contentStorageApi.db().detachDatabase(TRANSLATIONS_ATTACH_NAME);
        return lastSyncTimestamp[0];
    }

    /**
     * Inserts the item in the database.
     *
     * @param database   The database.
     * @param key        The key of the item.
     * @param value      The value of the item
     * @param moduleName The module name.
     * @param locale     The locale.
     */
    private void insert(@NonNull SQLiteDatabase database, @NonNull String key, @NonNull String value, @NonNull String itemId, @NonNull String moduleName, @NonNull String locale) {
        ContentValues values = new ContentValues();
        values.put(Translations.KEY, key);
        values.put(Translations.VALUE, value);
        values.put(Translations.MODULE_NAME, moduleName);
        values.put(Translations.ITEM_ID, itemId);
        values.put(Translations.LOCALE, locale);
        database.insertWithOnConflict(ORMUtils.getTableName(Translations.class), null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Provides the name for the module name in preferences.
     *
     * @param moduleName The module name.
     * @return The name created.
     */
    @NonNull
    private String getSyncNameForPrefs(@NonNull String moduleName) {
        return PREFIX_PREFERENCES_TRANSLATIONS + moduleName;
    }
}
