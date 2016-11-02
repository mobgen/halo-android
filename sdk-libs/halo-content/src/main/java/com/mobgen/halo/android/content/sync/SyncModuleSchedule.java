package com.mobgen.halo.android.content.sync;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.HaloSyncLog;
import com.mobgen.halo.android.content.models.SyncQuery;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.EventId;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.threading.HaloSchedule;

import static com.mobgen.halo.android.content.HaloContentApi.SYNC_FINISHED_EVENT;

/**
 * @hide Schedule to sync a module based on the request.
 */
public class SyncModuleSchedule extends HaloSchedule {

    /**
     * Content sync repository.
     */
    private ContentSyncRepository mSyncRepository;
    /**
     * Module id.
     */
    private SyncQuery mSyncQuery;

    /**
     * Constructor for the scheduler.
     *
     * @param syncRepository The sync repository.
     * @param syncQuery      The sync query.
     */
    public SyncModuleSchedule(@NonNull Halo halo, @NonNull ContentSyncRepository syncRepository, SyncQuery syncQuery) {
        super(halo);
        AssertionUtils.notNull(syncRepository, "syncRepository");
        AssertionUtils.notNull(syncQuery, "syncQuery");
        mSyncRepository = syncRepository;
        mSyncQuery = syncQuery;
    }

    @Override
    public void executeWhenReady() {
        HaloResultV2<Cursor> cursorResult = mSyncRepository.syncInstances(mSyncQuery);
        HaloSyncLog log = null;
        if (cursorResult.data() != null) {
            log = HaloSyncLog.create(cursorResult.data(), true);
        }
        Bundle syncResult = ModuleSyncHelper.bundleizeSync(new HaloResultV2<>(cursorResult.status(), log));
        mHalo.framework().emit(new Event(EventId.create(SYNC_FINISHED_EVENT + mSyncQuery.getModuleName()), syncResult));
    }
}
