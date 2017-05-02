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
    private static final String BUNDLE_BATH_OPERATIONS = "halo_batch_operations";

    /**
     * The private constructor for the helper.
     */
    private BatchBundleizeHelper() {
        //Private constructor to avoid instances for this helper.
    }

    /**
     * Check if is a result of a conflict or batch operation after recover connection.
     *
     * @param data The data.
     * @return True if its a conflict resolution notification; Otherwise false.
     */
    public static boolean isBatchOperation(@NonNull Bundle data){
        AssertionUtils.notNull(data, "data");
        return data.getParcelable(BUNDLE_BATH_OPERATIONS).getClass().equals(BatchOperations.class);
    }
    /**
     * Creates a bundle given the batch operation.
     *
     * @param batchOperations The batch operation.
     * @return The bundle created.
     */
    public static Bundle bundleizeBatchOperations(@NonNull BatchOperations batchOperations) {
        AssertionUtils.notNull(batchOperations, "batchOperations");
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_BATH_OPERATIONS, batchOperations);
        return bundle;
    }

    /**
     * Changes the bundle into the status and the sync log.
     *
     * @param bundle The bundle.
     * @return The pair of status and batch operation.
     */
    @NonNull
    public static BatchOperations debundleizeBatchOperations(@NonNull Bundle bundle) {
        AssertionUtils.notNull(bundle, "bundle");
        BatchOperations operations = bundle.getParcelable(BUNDLE_BATH_OPERATIONS);
        return operations;
    }

    /**
     * Creates a bundle given the batch operation result.
     *
     * @param batchOperationsResults The batch operation result.
     * @return The bundle created.
     */
    public static Bundle bundleizeBatchOperationsResults(@NonNull HaloResultV2<BatchOperationResults> batchOperationsResults) {
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
