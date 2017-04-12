package com.mobgen.halo.android.content.edition.batch;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.mobgen.halo.android.content.models.BatchOperationResults;
import com.mobgen.halo.android.content.models.BatchOperations;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;

/**
 * @hide Helper to do the batch process.
 */
public final class BatchBundleizeHelper {

    /**
     * The status for the batch operation.
     */
    private static final String BUNDLE_STATUS = "halo_status";
    /**
     * The batch operations.
     */
    private static final String BUNDLE_BATH_OPERATIONS = "halo_batch_opertions";

    /**
     * The private constructor for the helper.
     */
    private BatchBundleizeHelper() {
        //Private constructor to avoid instances for this helper.
    }

    public static boolean isBatchOperation(Bundle data){
        if(data.getParcelable(BUNDLE_BATH_OPERATIONS).getClass().equals(BatchOperations.class)){
            return true;
        } else {
            return false;
        }
    }
    /**
     * Creates a bundle given the batch operation.
     *
     * @param batchOperations The batch operation.
     * @return The bundle created.
     */
    public static Bundle bundleizeBatchOperations(HaloResultV2<BatchOperations> batchOperations) {
        AssertionUtils.notNull(batchOperations, "batchOperations");
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_STATUS, batchOperations.status());
        bundle.putParcelable(BUNDLE_BATH_OPERATIONS, batchOperations.data());
        return bundle;
    }

    /**
     * Changes the bundle into the status and the sync log.
     *
     * @param bundle The bundle.
     * @return The pair of status and batch operation.
     */
    @NonNull
    public static Pair<HaloStatus, BatchOperations> debundleizeBatchOperations(@NonNull Bundle bundle) {
        AssertionUtils.notNull(bundle, "bundle");
        HaloStatus status = bundle.getParcelable(BUNDLE_STATUS);
        BatchOperations operations = bundle.getParcelable(BUNDLE_BATH_OPERATIONS);
        return new Pair<>(status, operations);
    }

    /**
     * Creates a bundle given the batch operation result.
     *
     * @param batchOperationsResults The batch operation result.
     * @return The bundle created.
     */
    public static Bundle bundleizeBatchOperationsResults(HaloResultV2<BatchOperationResults> batchOperationsResults) {
        AssertionUtils.notNull(batchOperationsResults, "batchOperations");
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_STATUS, batchOperationsResults.status());
        bundle.putParcelable(BUNDLE_BATH_OPERATIONS, batchOperationsResults.data());
        return bundle;
    }

    /**
     * Changes the bundle into the status and the sync log.
     *
     * @param bundle The bundle.
     * @return The pair of status and batch operation.
     */
    @NonNull
    public static Pair<HaloStatus, BatchOperationResults> debundleizeBatchOperationsResults(@NonNull Bundle bundle) {
        AssertionUtils.notNull(bundle, "bundle");
        HaloStatus status = bundle.getParcelable(BUNDLE_STATUS);
        BatchOperationResults operations = bundle.getParcelable(BUNDLE_BATH_OPERATIONS);
        return new Pair<>(status, operations);
    }
}
