package com.mobgen.halo.android.translations.repository;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.selectors.ISelectorConverter;
import com.mobgen.halo.android.translations.spec.HaloTranslationsContract;

import java.util.HashMap;
import java.util.Map;

/**
 * @hide Converts a cursor into a map of string string. This is useful to map the key value
 * map for the module provided.
 */
public class Cursor2MapTranslationConverter implements ISelectorConverter<Map<String, String>, Cursor> {

    /**
     * Converts the cursor into a map.
     *
     * @param result The result obtained from the translations.
     * @return The result.
     * @throws Exception Some exception that could happen.
     */
    @NonNull
    @Override
    public HaloResultV2<Map<String, String>> convert(@NonNull HaloResultV2<Cursor> result) throws Exception {
        Cursor cursor = result.data();
        Map<String, String> map = null;
        if (cursor != null) {
            map = new HashMap<>(cursor.getCount());
            if (cursor.moveToFirst()) {
                do {
                    String key = cursor.getString(cursor.getColumnIndexOrThrow(HaloTranslationsContract.Translations.KEY));
                    String value = cursor.getString(cursor.getColumnIndexOrThrow(HaloTranslationsContract.Translations.VALUE));
                    map.put(key, value);
                } while (cursor.moveToNext());
            }
        }
        return new HaloResultV2<>(result.status(), map);
    }
}
