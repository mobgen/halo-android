package com.mobgen.halo.android.content.sync;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.selectors.SelectorCursor2CustomClass;
import com.mobgen.halo.android.content.utils.ContentCursor2ClassConverter;
import com.mobgen.halo.android.content.utils.HaloContentHelper;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.selectors.ISelectorConverter;
import com.mobgen.halo.android.sdk.core.selectors.SelectorProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @hide Factory that provides content instances parsed as a model type.
 */
public class Cursor2ClassSyncConverterFactory implements SelectorCursor2CustomClass.Factory<List<HaloContentInstance>, Cursor> {

    /**
     * The parser.
     */
    private Parser.Factory mParser;

    /**
     * The converter constructor.
     *
     * @param parser The halo parser.
     */
    public Cursor2ClassSyncConverterFactory(@NonNull Parser.Factory parser) {
        mParser = parser;
    }

    @NonNull
    @Override
    public <T> SelectorCursor2CustomClass<List<T>, List<HaloContentInstance>, Cursor> createList(@NonNull SelectorProvider<List<HaloContentInstance>, Cursor> dataProvider, @NonNull Class<T> clazz, @Data.Policy int mode) {
        return new SelectorCursor2CustomClass<>(
                dataProvider,
                new ContentCursor2ClassConverter<>(clazz, mParser),
                new ContentList2ClassConverter<>(clazz),
                mode);
    }

    /**
     * Converts the {@link HaloContentInstance} into a custom class.
     */
    private class ContentList2ClassConverter<T> implements ISelectorConverter<List<T>, List<HaloContentInstance>> {

        /**
         * The class type.
         */
        private Class<T> mClazz;

        /**
         * Constructor for the instance class converter.
         *
         * @param clazz The to change from and to.
         */
        public ContentList2ClassConverter(@NonNull Class<T> clazz) {
            mClazz = clazz;
        }

        @NonNull
        @Override
        public HaloResultV2<List<T>> convert(@NonNull HaloResultV2<List<HaloContentInstance>> data) throws Exception {
            List<HaloContentInstance> instances = data.data();
            List<T> result = null;
            if (instances != null) {
                result = new ArrayList<>(instances.size());
                for (HaloContentInstance instance : instances) {
                    result.add(HaloContentHelper.from(instance, mClazz, mParser));
                }
            }
            return new HaloResultV2<>(data.status(), result);
        }
    }
}
