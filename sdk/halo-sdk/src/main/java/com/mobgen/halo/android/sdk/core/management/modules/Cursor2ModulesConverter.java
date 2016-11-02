package com.mobgen.halo.android.sdk.core.management.modules;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.management.models.HaloModule;
import com.mobgen.halo.android.sdk.core.selectors.ISelectorConverter;

import java.util.List;

/**
 * Converts from a cursor to the model modules.
 */
public class Cursor2ModulesConverter implements ISelectorConverter<List<HaloModule>,Cursor> {

    @NonNull
    @Override
    public HaloResultV2<List<HaloModule>> convert(@NonNull HaloResultV2<Cursor> item) throws Exception {
        Cursor cursor = item.data();
        List<HaloModule> finalData = null;
        if(cursor != null){
            finalData = HaloModule.fromCursor(cursor, true);
        }
        return new HaloResultV2<>(item.status(), finalData);
    }
}