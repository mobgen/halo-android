package com.mobgen.halo.android.content.edition.batch;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.BatchOperationResults;
import com.mobgen.halo.android.content.models.BatchOperations;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.EventId;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.framework.toolbox.scheduler.Job;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;

import static com.mobgen.halo.android.content.edition.HaloContentEditApi.BATCH_FINISHED_EVENT;
import static com.mobgen.halo.android.sdk.api.HaloApplication.halo;

/**
 * General content instance content manipulation reporsitory
 */
public class BatchRepository {

    /**
     * Remote data source.
     */
    private BatchRemoteDataSource mRemoteDatasource;

    /**
     * Local data source.
     */
    private BatchLocalDataSource mLocalDataSource;

    /**
     * The halo content api.
     */
    private HaloContentApi mHaloContentApi;

    /**
     * Constructor for the repository.
     *
     * @param batchRemoteDataSource The remote data source.
     * @param haloContentApi        The halo content api.
     * @param batchLocalDataSource  The local data source.
     */
    public BatchRepository(@NonNull HaloContentApi haloContentApi,
                           @NonNull BatchRemoteDataSource batchRemoteDataSource,
                           @NonNull BatchLocalDataSource batchLocalDataSource) {
        AssertionUtils.notNull(haloContentApi, "haloContentApi");
        AssertionUtils.notNull(batchRemoteDataSource, "batchRemoteDataSource");
        AssertionUtils.notNull(batchLocalDataSource, "batchLocalDataSource");
        mRemoteDatasource = batchRemoteDataSource;
        mLocalDataSource = batchLocalDataSource;
        mHaloContentApi = haloContentApi;
    }

    /**
     * Perfom a batch operation. This operation catch HaloNetException to store on the local data
     * source all pending batch operations.
     *
     * @return A HaloResultV2<BatchOperationResults> with the result
     * @throws HaloParsingException
     */
    @NonNull
    public HaloResultV2<BatchOperationResults> batchOperation(@NonNull BatchOperations batchOperations) throws HaloStorageGeneralException {
        HaloStatus.Builder status = HaloStatus.builder();
        BatchOperationResults response = null;
        try {
            response = mRemoteDatasource.batchOperation(batchOperations);
            removeOperations();
        } catch (HaloNetException haloException) {
            //net error so we need to store data when we have connection
            status.error(haloException);
            mLocalDataSource.saveErrors(batchOperations);
            //emit onError result to all listeners
            Bundle batchResult = BatchBundleizeHelper.bundleizeBatchOperations(new HaloResultV2<>(status.build(), batchOperations));
            Halo.instance().framework().emit(new Event(EventId.create(BATCH_FINISHED_EVENT), batchResult));
            //schedule a job to execute when network works again
            Job.Builder job = Job.builder(new BatchSchedule(halo(), this))
                    .persist(true)
                    .thread(Threading.SINGLE_QUEUE_POLICY)
                    .tag("batchOperation")
                    .needsNetwork(Job.NETWORK_TYPE_ANY);
            halo().framework().toolbox().schedule(job.build());
        } catch (HaloParsingException haloParsingException) {
            status.error(haloParsingException);
        }
        return new HaloResultV2<>(status.build(), response);
    }

    /**
     * Get all pending batch operations from local data source.
     *
     * @return The pending batch operations to perfom.
     * @throws HaloStorageGeneralException
     */
    @NonNull
    public BatchOperations getPendingOperations() throws HaloStorageGeneralException {
        BatchOperations instances = mLocalDataSource.getBatchOperations();
        return instances;
    }

    /**
     * Get all pending batch operations from local data source.
     *
     * @return The pending batch operations to perfom.
     * @throws HaloStorageGeneralException
     */
    @NonNull
    public boolean removePendingOperations(HaloContentInstance... instances) throws HaloStorageGeneralException {
        return mLocalDataSource.deleteErrors(instances);
    }

    /**
     * Remove all pending operations to restore status.
     *
     * @throws HaloStorageGeneralException
     */
    private void removeOperations() throws HaloStorageGeneralException {
        //remove pending tasks if batchOperation works on remote data source
        BatchOperations instances = mLocalDataSource.getBatchOperations();
        if (instances.getTruncate() != null && instances.getTruncate().size() > 0) {
            removePendingOperations(instances.getTruncate().toArray(new HaloContentInstance[instances.getTruncate().size()]));
        }
        if (instances.getCreated() != null && instances.getCreated().size() > 0) {
            removePendingOperations(instances.getCreated().toArray(new HaloContentInstance[instances.getCreated().size()]));
        }
        if (instances.getCreatedOrUpdated() != null && instances.getCreatedOrUpdated().size() > 0) {
            removePendingOperations(instances.getCreatedOrUpdated().toArray(new HaloContentInstance[instances.getCreatedOrUpdated().size()]));
        }
        if (instances.getUpdated() != null && instances.getUpdated().size() > 0) {
            removePendingOperations(instances.getUpdated().toArray(new HaloContentInstance[instances.getUpdated().size()]));
        }
        if (instances.getDeleted() != null && instances.getDeleted().size() > 0) {
            removePendingOperations(instances.getDeleted().toArray(new HaloContentInstance[instances.getDeleted().size()]));
        }
    }

}
