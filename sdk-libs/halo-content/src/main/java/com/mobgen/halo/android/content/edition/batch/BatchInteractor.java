package com.mobgen.halo.android.content.edition.batch;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.BatchOperationResults;
import com.mobgen.halo.android.content.models.BatchOperations;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * Created by fernandosouto on 05/04/17.
 */

/**
 * General content instance content batch manipulation interactor.
 */
public class BatchInteractor implements HaloInteractorExecutor.Interactor<BatchOperationResults> {

    /**
     * Batch operation repository.
     */
    private BatchRepository mBatchRepository;

    /**
     * The batch operations to perfom.
     */
    private BatchOperations mBatchOperations;


    /**
     * Constructor for the interactor.
     *
     * @param batchRepository The repository.
     */
    public BatchInteractor(@NonNull BatchRepository batchRepository, @NonNull BatchOperations batchOperations) {
        mBatchRepository = batchRepository;
        mBatchOperations = batchOperations;
    }


    @NonNull
    @Override
    public HaloResultV2<BatchOperationResults> executeInteractor() throws Exception {
        HaloResultV2<BatchOperationResults> result = null;
        result = mBatchRepository.batchOperation(mBatchOperations);
        return result;
    }
}
