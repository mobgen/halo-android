package com.mobgen.halo.android.content.generated;

import android.database.Cursor;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.selectors.SelectorCursor2CustomClass;
import com.mobgen.halo.android.content.utils.ContentCursor2GeneratedModelClassConverter;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.sdk.core.selectors.SelectorProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @hide Factory that provides generic model parsed as a model type.
 */
@Keep
public class Cursor2GeneratedModelClassConverterFactory<P> implements SelectorCursor2CustomClass.Factory<Paginated<P>, Cursor> {

    /**
     * The parser.
     */
    private Parser.Factory mParser;

    /**
     * The converter constructor.
     *
     * @param parser The halo parser.
     */
    public Cursor2GeneratedModelClassConverterFactory(@NonNull Parser.Factory parser) {
        mParser = parser;
    }

    @NonNull
    @Override
    public <T> SelectorCursor2CustomClass<List<T>, Paginated<P>, Cursor> createList(@NonNull SelectorProvider<Paginated<P>, Cursor> dataProvider, @NonNull Class<T> clazz, @Data.Policy int mode) {
        return new SelectorCursor2CustomClass<>(
                dataProvider,
                new ContentCursor2GeneratedModelClassConverter<>(clazz, mParser),
                null,
                mode);
    }
}
