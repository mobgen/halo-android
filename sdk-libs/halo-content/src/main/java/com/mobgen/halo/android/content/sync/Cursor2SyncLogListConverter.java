package com.mobgen.halo.android.content.sync;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.HaloSyncLog;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.selectors.ISelectorConverter;

import java.util.List;

/**
 * @hide Converts a cursor into a sync log list.
 */
public class Cursor2SyncLogListConverter implements ISelectorConverter<List<HaloSyncLog>, Cursor> {

    @NonNull
    @Override
    public HaloResultV2<List<HaloSyncLog>> convert(@NonNull HaloResultV2<Cursor> data) throws Exception {
        List<HaloSyncLog> logs = null;
        Cursor cursor = data.data();
        if (cursor != null) {
            logs = HaloSyncLog.createList(cursor, true);
        }
        return new HaloResultV2<>(data.status(), logs);
    }
}
