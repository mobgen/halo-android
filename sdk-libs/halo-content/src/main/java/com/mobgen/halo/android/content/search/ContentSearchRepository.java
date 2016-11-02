package com.mobgen.halo.android.content.search;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;

/**
 * @hide Content search repository to make al the requests.
 */
public class ContentSearchRepository {

    /**
     * Provides the remote repository.
     */
    private ContentSearchRemoteDatasource mRemoteRepository;

    /**
     * The local repository.
     */
    private ContentSearchLocalDatasource mLocalDao;

    /**
     * Constructor that receives the content search.
     *
     * @param remoteDatasource The remote datasource.
     * @param localDatasource  The local datasource.
     */
    public ContentSearchRepository(@NonNull ContentSearchRemoteDatasource remoteDatasource,
                                   @NonNull ContentSearchLocalDatasource localDatasource) {
        mRemoteRepository = remoteDatasource;
        mLocalDao = localDatasource;
    }

    /**
     * Searches only based on network.
     *
     * @param query The query for the search.
     * @return The resulting value.
     */
    @NonNull
    @WorkerThread
    public HaloResultV2<Paginated<HaloContentInstance>> searchNetwork(@NonNull SearchQuery query) {
        HaloStatus.Builder status = HaloStatus.builder();
        Paginated<HaloContentInstance> instances = null;
        try {
            instances = mRemoteRepository.findByQuery(query);
        } catch (HaloNetException e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), instances);
    }

    /**
     * Searches based only on the storage.
     *
     * @param query The query to search.
     * @return The result with the cursor data.
     */
    @NonNull
    @WorkerThread
    public HaloResultV2<Cursor> searchStorage(@NonNull SearchQuery query) {
        HaloStatus.Builder status = HaloStatus.builder().dataLocal();
        Cursor cursor = null;
        try {
            cursor = mLocalDao.findByQuery(query);
        } catch (HaloStorageException e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), cursor);
    }

    /**
     * Searches on the network and caches the data retrieved.
     *
     * @param query The query.
     * @return The result with the cursor.
     */
    @NonNull
    @WorkerThread
    public HaloResultV2<Cursor> searchNetworkAndStorage(@NonNull SearchQuery query) {
        HaloStatus.Builder status = HaloStatus.builder();
        Cursor cursor = null;
        try {
            mLocalDao.save(query, mRemoteRepository.findByQuery(query));
        } catch (HaloNetException | HaloStorageGeneralException e) {
            Halog.e(getClass(), "Error saving instances", e);
            status.error(e);
            status.dataLocal();
        } finally {
            try {
                cursor = mLocalDao.findByQuery(query);
            } catch (HaloStorageException e) {
                status.error(e);
            }
        }
        return new HaloResultV2<>(status.build(), cursor);
    }

    /**
     * Clears the search data from the search local datasource.
     * @return The search result.
     */
    @NonNull
    public HaloResultV2<Void> clearSearch() {
        HaloStatus.Builder status = HaloStatus.builder().dataLocal();
        try {
            mLocalDao.clearSearch();
        } catch (HaloStorageGeneralException e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), null);
    }
}
