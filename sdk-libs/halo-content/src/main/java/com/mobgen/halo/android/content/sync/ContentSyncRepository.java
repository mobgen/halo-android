package com.mobgen.halo.android.content.sync;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.mobgen.halo.android.content.models.HaloInstanceSync;
import com.mobgen.halo.android.content.models.SyncQuery;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;

import java.util.Date;

/**
 * @hide The repository that handles the operations to synchronize information
 * from the sync service.
 */
public class ContentSyncRepository {
    /**
     * The local data source.
     */
    private ContentSyncLocalDatasource mLocalDatasource;
    /**
     * The remote data source.
     */
    private ContentSyncRemoteDatasource mRemoteDatasource;

    /**
     * Constructor for the repository.
     *
     * @param remoteDatasource The remote repository.
     * @param localDatasource  The local repository.
     */
    public ContentSyncRepository(@NonNull ContentSyncRemoteDatasource remoteDatasource, @NonNull ContentSyncLocalDatasource localDatasource) {
        AssertionUtils.notNull(remoteDatasource, "remoteDatasource");
        AssertionUtils.notNull(localDatasource, "localDatasource");
        mRemoteDatasource = remoteDatasource;
        mLocalDatasource = localDatasource;
    }

    /**
     * Syncs the instances and caches them into the local database.
     *
     * @param syncQuery The query for the sync.
     * @return The result with the raw cursor data.
     */
    @NonNull
    @WorkerThread
    public HaloResultV2<Cursor> syncInstances(@NonNull SyncQuery syncQuery) {
        AssertionUtils.notNull(syncQuery, "syncQuery");
        Cursor result = null;
        HaloStatus.Builder status = HaloStatus.builder().dataLocal();
        Date lastSyncDate = mLocalDatasource.getLastSyncDate(syncQuery.getModuleName(), syncQuery.getLocale());
        try {
            HaloInstanceSync instanceSync = mRemoteDatasource.syncModule(syncQuery.getModuleName(), syncQuery.getLocale(), lastSyncDate);
            long logId = mLocalDatasource.sync(lastSyncDate == null, syncQuery, instanceSync);
            result = mLocalDatasource.getSyncedModuleLog(logId);
        } catch (Exception e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), result);
    }

    /**
     * Provides the synced instances.
     *
     * @param moduleName The module name.
     * @return The result of getting the synced instances.
     */
    @NonNull
    @WorkerThread
    public HaloResultV2<Cursor> getSyncedInstances(@NonNull String moduleName) {
        HaloStatus.Builder status = HaloStatus.builder().dataLocal();
        Cursor result = null;
        try {
            result = mLocalDatasource.getSyncedModuleItems(moduleName);
        } catch (Exception e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), result);
    }

    /**
     * Provides the sync log.
     *
     * @param moduleName The module name of the log.
     * @return The result.
     */
    @WorkerThread
    public HaloResultV2<Cursor> getSyncLog(@Nullable String moduleName) {
        HaloStatus.Builder status = HaloStatus.builder().dataLocal();
        Cursor result = null;
        try {
            result = mLocalDatasource.getSyncLog(moduleName);
        } catch (Exception e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), result);
    }

    /**
     * Clears all the synced instances.
     *
     * @param moduleName The module name.
     * @return The resulting data. In this case there is no data that related.
     */
    @NonNull
    @WorkerThread
    public HaloResultV2<Void> clearSyncedInstances(@NonNull String moduleName) {
        HaloStatus.Builder status = HaloStatus.builder().dataLocal();
        try {
            mLocalDatasource.clearSyncModule(moduleName);
        } catch (Exception e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), null);
    }
}
