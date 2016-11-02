package com.mobgen.halo.android.content.sync;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.HaloSyncLog;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.selectors.SelectorProviderAdapter;

import java.util.List;

/**
 * @hide Contains the data providers for sync operations.
 */
public abstract class SyncDataProviders {

    /**
     * Constructor to avoid instances of this class.
     */
    private SyncDataProviders(){
        //Do not allow the construction of instances
    }

    /**
     * Creates the sync log data provider.
     *
     * @param syncRepository The sync repository.
     * @param moduleName       The module name.
     * @return The provider.
     */
    @NonNull
    public static GetSyncLogInteractor syncLogInteractor(@NonNull ContentSyncRepository syncRepository, @Nullable String moduleName) {
        return new GetSyncLogInteractor(syncRepository, moduleName);
    }

    /**
     * Provides the synced instances.
     *
     * @param syncRepository The repository.
     * @param moduleName     The module name.
     * @return The provider.
     */
    @NonNull
    public static GetSyncedInstancesInteractor syncedInstancesInteractor(@NonNull ContentSyncRepository syncRepository, @NonNull String moduleName) {
        return new GetSyncedInstancesInteractor(syncRepository, moduleName);
    }

    /**
     * Clears the synced instances.
     *
     * @param syncRepository The sync repository.
     * @param moduleName     The module name to clear.
     * @return The provider.
     */
    @NonNull
    public static ClearSyncedInstancesInteractor clearSyncedInstancesInteractor(@NonNull ContentSyncRepository syncRepository, @NonNull String moduleName) {
        return new ClearSyncedInstancesInteractor(syncRepository, moduleName);
    }

    /**
     * Provides the synced instances.
     */
    public static class GetSyncedInstancesInteractor extends SelectorProviderAdapter<List<HaloContentInstance>, Cursor> {
        /**
         * The sync repository.
         */
        private ContentSyncRepository mSyncRepository;
        /**
         * The module id.
         */
        private String mModuleName;

        /**
         * The data providers.
         *
         * @param syncRepository The repository for synchronizing data.
         * @param moduleName     The module name.
         */
        private GetSyncedInstancesInteractor(@NonNull ContentSyncRepository syncRepository, @Nullable String moduleName) {
            mSyncRepository = syncRepository;
            mModuleName = moduleName;
        }

        @NonNull
        @Override
        public HaloResultV2<Cursor> fromStorage() throws HaloStorageException {
            return mSyncRepository.getSyncedInstances(mModuleName);
        }
    }

    /**
     * Provider that gives the sync logs.
     */
    public static class GetSyncLogInteractor extends SelectorProviderAdapter<List<HaloSyncLog>, Cursor> {

        /**
         * The sync repository.
         */
        private ContentSyncRepository mSyncRepository;
        /**
         * The module id.
         */
        private String mModuleName;

        /**
         * The data providers.
         *
         * @param syncRepository The repository for synchronizing data.
         * @param moduleName     The module name.
         */
        private GetSyncLogInteractor(@NonNull ContentSyncRepository syncRepository, @Nullable String moduleName) {
            mSyncRepository = syncRepository;
            mModuleName = moduleName;
        }

        @NonNull
        @Override
        public HaloResultV2<Cursor> fromStorage() throws HaloStorageException {
            return mSyncRepository.getSyncLog(mModuleName);
        }
    }

    /**
     * Clears the synced instances for the given module.
     */
    public static class ClearSyncedInstancesInteractor extends SelectorProviderAdapter<Void, Void> {
        /**
         * The sync repository.
         */
        private ContentSyncRepository mSyncRepository;
        /**
         * The module id.
         */
        private String mModuleName;

        /**
         * Clear instances data provider constructor.
         *
         * @param syncRepository The sync repository.
         * @param moduleName     The module name.
         */
        public ClearSyncedInstancesInteractor(ContentSyncRepository syncRepository, String moduleName) {
            mSyncRepository = syncRepository;
            mModuleName = moduleName;
        }

        @NonNull
        @Override
        public HaloResultV2<Void> fromStorage() throws HaloStorageException {
            return mSyncRepository.clearSyncedInstances(mModuleName);
        }
    }
}
