package com.mobgen.halo.android.content.sync;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;

import com.mobgen.halo.android.content.models.HaloSyncLog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;

/**
 * @hide Helper to do the synchronization process.
 */
public final class ModuleSyncHelper {

    /**
     * The status for the log of halo.
     */
    private static final String BUNDLE_STATUS = "halo_status";
    /**
     * The synchronization log for halo.
     */
    private static final String BUNDLE_SYNC_LOG = "halo_sync_log";

    /**
     * The private constructor for the helper.
     */
    private ModuleSyncHelper() {
        //Private constructor to avoid instances for this helper.
    }

    /**
     * Creates a bundle given the log of the sync.
     *
     * @param synchronizationResult The synchronization result.
     * @return The bundle created.
     */
    public static Bundle bundleizeSync(HaloResultV2<HaloSyncLog> synchronizationResult) {
        AssertionUtils.notNull(synchronizationResult, "synchronizationResult");
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_STATUS, synchronizationResult.status());
        bundle.putParcelable(BUNDLE_SYNC_LOG, synchronizationResult.data());
        return bundle;
    }

    /**
     * Changes the bundle into the status and the sync log.
     *
     * @param bundle The bundle.
     * @return The pair of status and sync log.
     */
    @NonNull
    public static Pair<HaloStatus, HaloSyncLog> debundleizeSync(@NonNull Bundle bundle) {
        AssertionUtils.notNull(bundle, "bundle");
        HaloStatus status = bundle.getParcelable(BUNDLE_STATUS);
        HaloSyncLog syncLog = bundle.getParcelable(BUNDLE_SYNC_LOG);
        return new Pair<>(status, syncLog);
    }
}
