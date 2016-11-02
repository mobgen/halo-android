package com.mobgen.halo.android.translations.repository;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.api.HaloStorageApi;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;
import com.mobgen.halo.android.sdk.core.selectors.SelectorProviderAdapter;

import java.util.Map;

/**
 * @hide The translations repository to access the translations.
 */
public final class TranslationsRepository {

    /**
     * The local data source.
     */
    private TranslationsLocalDatasource mLocalDatasource;

    /**
     * Constructor for the local repository.
     *
     * @param translationsLocalDatasource The local datasource.
     */
    public TranslationsRepository(TranslationsLocalDatasource translationsLocalDatasource) {
        mLocalDatasource = translationsLocalDatasource;
    }

    /**
     * Syncs the translations with the current storage api.
     *
     * @param contentStorageApi The content storage already created to sync to.
     * @param moduleId          The module id.
     * @param locale            The locale to sync.
     * @param keyName           The name of the json key in the map of values to bring the key of the translation.
     * @param valueName         The name of the json key in the map of values to bring the value of the translation.
     * @return The result with no data but the status to check if the sync was correct.
     */
    @NonNull
    public HaloResultV2<Cursor> syncTranslations(@NonNull HaloStorageApi contentStorageApi, @NonNull String moduleId, @HaloLocale.LocaleDefinition @NonNull String locale, @NonNull String keyName, @NonNull String valueName, boolean shouldResync) {
        HaloStatus.Builder status = HaloStatus.builder().dataLocal();
        Cursor cursor;
        try {
            if (shouldResync) {
                mLocalDatasource.syncTranslationsWith(contentStorageApi, moduleId, locale, keyName, valueName);
            }
        } catch (HaloStorageGeneralException e) {
            status.error(e);
        } finally {
            //Get the translations synced
            cursor = mLocalDatasource.getTranslations(moduleId, locale);
        }
        return new HaloResultV2<>(status.build(), cursor);
    }

    /**
     * Clears the translations for the given module.
     *
     * @param moduleId The module id.
     * @return The translations to clear.
     */
    @NonNull
    public HaloResultV2<Void> clearTranslations(@NonNull String moduleId) {
        HaloStatus.Builder status = HaloStatus.builder().dataLocal();
        try {
            mLocalDatasource.clearTranslations(moduleId);
        } catch (Exception e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), null);
    }

    /**
     * Mimics the last synced timestamp for the local datasource.
     *
     * @param moduleId The module id to sync.
     * @return The timestamp or null if it was not synced.
     */
    @Nullable
    public Long getLastSyncTimestamp(@NonNull String moduleId) {
        return mLocalDatasource.getLastSyncTimestamp(moduleId);
    }

    /**
     * Clears the translations for a module id.
     */
    public static class ClearTranslationsDataProvider extends SelectorProviderAdapter<Void, Void> {

        /**
         * The translations repository.
         */
        @NonNull
        private TranslationsRepository mTranslationsRepository;
        /**
         * The module id.
         */
        @NonNull
        private String mModuleId;

        /**
         * Constructor for the translations.
         *
         * @param translations The translations.
         * @param moduleId     The module id.
         */
        public ClearTranslationsDataProvider(@NonNull TranslationsRepository translations, @NonNull String moduleId) {
            mTranslationsRepository = translations;
            mModuleId = moduleId;
        }

        @NonNull
        @Override
        public HaloResultV2<Void> fromStorage() throws HaloStorageException {
            return mTranslationsRepository.clearTranslations(mModuleId);
        }
    }

    /**
     * The translations provider.
     */
    public static class SyncTranslationsInteractor extends SelectorProviderAdapter<Map<String, String>, Cursor> {
        /**
         * The translations repository.
         */
        @NonNull
        private TranslationsRepository mTranslationsRepository;
        /**
         * The storage api.
         */
        @NonNull
        private HaloStorageApi mContentStorage;
        /**
         * The module id.
         */
        @NonNull
        private String mModuleId;
        /**
         * The locale.
         */
        @NonNull
        private String mLocale;
        /**
         * The key name.
         */
        @NonNull
        private String mKeyName;
        /**
         * The value name.
         */
        @NonNull
        private String mValueName;
        /**
         * Tells if this needs a resync.
         */
        private boolean mShouldRefresh;

        /**
         * The constructor for the translations provider.
         *
         * @param translationsRepository The translations repository.
         * @param contentStorageApi      The content storage api.
         * @param moduleId               The module id.
         * @param locale                 The locale.
         * @param keyName                The key name.
         * @param valueName              The value name.
         */
        public SyncTranslationsInteractor(@NonNull TranslationsRepository translationsRepository, @NonNull HaloStorageApi contentStorageApi, @NonNull String moduleId, @NonNull String locale, @NonNull String keyName, @NonNull String valueName, boolean shouldRefresh) {
            mTranslationsRepository = translationsRepository;
            mModuleId = moduleId;
            mLocale = locale;
            mContentStorage = contentStorageApi;
            mKeyName = keyName;
            mValueName = valueName;
            mShouldRefresh = shouldRefresh;
        }

        @NonNull
        @Override
        public HaloResultV2<Cursor> fromStorage() throws HaloStorageException {
            return mTranslationsRepository.syncTranslations(mContentStorage, mModuleId, mLocale, mKeyName, mValueName, mShouldRefresh);
        }
    }
}
