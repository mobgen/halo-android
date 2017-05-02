package com.mobgen.halo.android.content.edition.batch;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.BatchErrorInfo;
import com.mobgen.halo.android.content.models.BatchOperationResult;
import com.mobgen.halo.android.content.models.BatchOperationResults;
import com.mobgen.halo.android.content.models.BatchOperations;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.SyncQuery;
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

import java.util.List;

import static com.mobgen.halo.android.content.edition.HaloContentEditApi.BATCH_FINISHED_EVENT;
import static com.mobgen.halo.android.content.models.BatchOperator.CREATEORUPDATE;
import static com.mobgen.halo.android.content.models.BatchOperator.DELETE;
import static com.mobgen.halo.android.content.models.BatchOperator.UPDATE;
import static com.mobgen.halo.android.sdk.api.HaloApplication.halo;

/**
 * General content instance content manipulation reporsitory
 */
public class BatchRepository {

    /**
     * The job name to retry request.
     */
    private static final String JOB_NAME = "batchOperation";

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
     * Flag to force sync of module.
     */
    private boolean mSyncResults;

    /**
     * Constructor for the repository.
     *
     * @param batchRemoteDataSource The remote data source.
     * @param haloContentApi        The halo content api.
     * @param batchLocalDataSource  The local data source.
     * @param syncResults           Flag to perfom sync after batch.
     */
    public BatchRepository(@NonNull HaloContentApi haloContentApi,
                           @NonNull BatchRemoteDataSource batchRemoteDataSource,
                           @NonNull BatchLocalDataSource batchLocalDataSource,
                           boolean syncResults) {
        AssertionUtils.notNull(haloContentApi, "haloContentApi");
        AssertionUtils.notNull(batchRemoteDataSource, "batchRemoteDataSource");
        AssertionUtils.notNull(batchLocalDataSource, "batchLocalDataSource");
        mRemoteDatasource = batchRemoteDataSource;
        mLocalDataSource = batchLocalDataSource;
        mHaloContentApi = haloContentApi;
        mSyncResults = syncResults;
    }

    /**
     * Perfom a batch operation. This operation catch HaloNetException to store on the local data
     * source all pending batch operations.
     *
     * @return A HaloResultV2<BatchOperationResults> with the result
     * @throws HaloParsingException
     */
    @NonNull
    public HaloResultV2<BatchOperationResults> batchOperation(@NonNull final BatchOperations batchOperations) throws HaloStorageGeneralException {
        HaloStatus.Builder status = HaloStatus.builder();
        BatchOperationResults response = null;
        try {
            response = mRemoteDatasource.batchOperation(batchOperations);
            syncAndNofifyConflicts(response);
        } catch (HaloNetException haloException) {
            //net error so we need to store data when we have connection
            status.error(haloException);
            mLocalDataSource.saveErrors(batchOperations);
            //schedule a job to execute when network works again
            Job.Builder job = Job.builder(new BatchSchedule(halo(), this))
                    .persist(true)
                    .thread(Threading.SINGLE_QUEUE_POLICY)
                    .tag(JOB_NAME)
                    .needsNetwork(Job.NETWORK_TYPE_ANY);
            halo().framework().toolbox().schedule(job.build());
        } catch (HaloParsingException haloParsingException) {
            status.error(haloParsingException);
        }
        return new HaloResultV2<>(status.build(), response);
    }

    /**
     * Sync every module that contains success operations and verify all instances looking for conflicts.
     * It will notify via subscription a solution to the conflicts.
     *
     * @param batchOperationResults The batch operations.
     */
    private void syncAndNofifyConflicts(@NonNull final BatchOperationResults batchOperationResults) {
        final BatchOperations.Builder conflictBuilder = new BatchOperations.Builder();
        List<BatchOperationResult> batchResult = batchOperationResults.getContentResult();
        for (int i = 0; i < batchResult.size(); i++) {
            //sync every success module which contains the instance
            if (mSyncResults) {
                HaloContentInstance instance = batchResult.get(i).getData();
                if (instance != null && instance.getModuleName() != null && batchResult.get(i).isSuccess()) {
                    mHaloContentApi.sync(SyncQuery.create(instance.getModuleName(), Threading.POOL_QUEUE_POLICY), true);
                }
            }
            //look for conflict operations
            if (batchResult.get(i).getOperation().equals(DELETE)
                    && !batchResult.get(i).isSuccess()
                    && batchResult.get(i).getDataError().getError().getStatus() == BatchErrorInfo.CONFLICT_STATUS) {
                conflictBuilder.delete(batchResult.get(i).getDataError().getError().getExtraInstance());
            }
            if (batchResult.get(i).getOperation().equals(UPDATE)
                    && !batchResult.get(i).isSuccess()
                    && batchResult.get(i).getDataError().getError().getStatus() == BatchErrorInfo.CONFLICT_STATUS) {
                conflictBuilder.update(batchResult.get(i).getDataError().getError().getExtraInstance());
            }
            if (batchResult.get(i).getOperation().equals(CREATEORUPDATE)
                    && !batchResult.get(i).isSuccess()
                    && batchResult.get(i).getDataError().getError().getStatus() == BatchErrorInfo.CONFLICT_STATUS) {
                conflictBuilder.createOrUpdate(batchResult.get(i).getDataError().getError().getExtraInstance());
            }
        }
        //Notify the user the conflicts on delete, createorupdate or update operations
        BatchOperations conflictOperations = conflictBuilder.build();
        if (conflictOperations.getDeleted() != null || conflictOperations.getUpdated() != null
                || conflictOperations.getCreatedOrUpdated() != null) {
            Bundle batchConflict = BatchBundleizeHelper.bundleizeBatchOperations(conflictOperations);
            Halo.instance().framework().emit(new Event(EventId.create(BATCH_FINISHED_EVENT), batchConflict));
        }
    }

    /**
     * Get all pending batch operations from local data source.
     *
     * @return The pending batch operations to perfom.
     * @throws HaloStorageGeneralException
     */
    @NonNull
    public BatchOperations getPendingOperations() throws HaloStorageGeneralException {
        return mLocalDataSource.getPendingBatchOperations();
    }

    /**
     * Remove all pending operations to restore status.
     *
     * @throws HaloStorageGeneralException
     */
    public void removePendingOperations() throws HaloStorageGeneralException {
        //remove pending tasks if batchOperation works on remote data source
        BatchOperations instances = mLocalDataSource.getPendingBatchOperations();
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

    /**
     * Remove all pending batch operations from local data source.
     *
     * @return The pending batch operations to perfom.
     * @throws HaloStorageGeneralException
     */
    @NonNull
    private boolean removePendingOperations(HaloContentInstance... instances) throws HaloStorageGeneralException {
        return mLocalDataSource.deleteErrors(instances);
    }

}
