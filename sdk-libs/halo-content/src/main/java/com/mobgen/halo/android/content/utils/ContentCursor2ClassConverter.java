package com.mobgen.halo.android.content.utils;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageParseException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.selectors.ISelectorConverter;

import java.util.List;

/**
 * @hide The instance class converter.
 */
public class ContentCursor2ClassConverter<T> implements ISelectorConverter<List<T>, Cursor> {

    /**
     * The parser.
     */
    private Parser.Factory mParser;
    /**
     * The class type.
     */
    private Class<T> mClazz;

    /**
     * Constructor for the instance class converter.
     *
     * @param clazz The to change from and to.
     */
    public ContentCursor2ClassConverter(@NonNull Class<T> clazz, @NonNull Parser.Factory factory) {
        mClazz = clazz;
        mParser = factory;
    }

    @NonNull
    @Override
    public HaloResultV2<List<T>> convert(@NonNull HaloResultV2<Cursor> cursorResult) throws HaloParsingException, HaloStorageParseException {
        Cursor cursor = cursorResult.data();
        List<T> parsedData = null;
        if (cursor != null) {
            parsedData = HaloContentHelper.createList(cursor, true, mClazz, mParser);
        }
        return new HaloResultV2<>(cursorResult.status(), parsedData);
    }
}