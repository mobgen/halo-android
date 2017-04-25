package com.mobgen.halo.android.content.edition.batch;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.BatchOperationResults;
import com.mobgen.halo.android.content.models.BatchOperations;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.EventId;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.threading.HaloSchedule;


import static com.mobgen.halo.android.content.edition.HaloContentEditApi.BATCH_FINISHED_EVENT;

/**
 * Created by fernandosouto on 12/04/17.
 */

/**
 * Schedule a job to perfom pending batch operations when there is a problem with internet connection.
 */
public class BatchSchedule extends HaloSchedule {

    /**
     * Content sync repository.
     */
    private BatchRepository mBatchRepository;

    /**
     * Constructor for the scheduler.
     *
     * @param batchRepository The sync repository.
     */
    public BatchSchedule(@NonNull Halo halo, @NonNull BatchRepository batchRepository) {
        super(halo);
        AssertionUtils.notNull(batchRepository, "batchRepository");
        mBatchRepository = batchRepository;
    }

    @Override
    public void executeWhenReady() {
        HaloResultV2<BatchOperationResults> result = null;
        try {
            //get all operations from database
            BatchOperations pendingOperations = mBatchRepository.getPendingOperations();
            //remove pending operations from database
            mBatchRepository.removeOperations();
            result = mBatchRepository.batchOperation(pendingOperations);
            //notify the user with response
            Bundle batchResult = BatchBundleizeHelper.bundleizeBatchOperationsResults(new HaloResultV2<>(result.status(), result.data()));
            Halo.instance().framework().emit(new Event(EventId.create(BATCH_FINISHED_EVENT), batchResult));
        } catch (HaloStorageGeneralException e) {
            Halog.d(BatchSchedule.class, "Could not use storage to perfom batch operation");
        }
    }
}
