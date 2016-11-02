package com.mobgen.halo.android.content.sync;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.utils.HaloContentHelper;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageParseException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.selectors.ISelectorConverter;

import java.util.List;

/**
 * @hide Parses the cursor into a list of content instances.
 */
public class Cursor2ContentInstanceSyncConverter implements ISelectorConverter<List<HaloContentInstance>, Cursor> {

    @NonNull
    @Override
    public HaloResultV2<List<HaloContentInstance>> convert(@NonNull HaloResultV2<Cursor> cursor) throws HaloStorageParseException {
        return new HaloResultV2<>(cursor.status(), parse(cursor.data()));
    }

    /**
     * Parses the cursor into a list of content instances.
     *
     * @param cursor The cursor to parse.
     * @return The list of content instances.
     * @throws HaloStorageParseException The parsing error.
     */
    private List<HaloContentInstance> parse(Cursor cursor) throws HaloStorageParseException {
        List<HaloContentInstance> instances = null;
        if (cursor != null) {
            instances = HaloContentHelper.createList(cursor, true);
        }
        return instances;
    }
}