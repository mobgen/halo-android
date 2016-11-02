package com.mobgen.halo.android.content.selectors;

import android.support.annotation.CheckResult;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.selectors.HaloSelectorFactory;
import com.mobgen.halo.android.sdk.core.selectors.ISelectorConverter;
import com.mobgen.halo.android.sdk.core.selectors.SelectorProvider;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;
import com.mobgen.halo.android.sdk.core.threading.InteractorExecutionCallback;

import java.util.List;

/**
 * This selector allows the user of the sdk to select which type of content
 * he needs. This selection is made based on the raw to content and from content
 * to custom class parsed content. Check the HaloSelectorFactory to get more information.
 */
public class HaloContentSelectorFactory<P, U> extends HaloSelectorFactory<P, U> {
    /**
     * The converter factory fo search instance parsers.
     */
    private final SelectorCursor2CustomClass.Factory<P, U> mConverterFactory;

    /**
     * The mode selector.
     *
     * @param halo              The content api.
     * @param dataProvider      The data provider.
     * @param converter         The converter between different data types.
     * @param converterFactory  The factory of converters for {@link com.mobgen.halo.android.content.models.HaloContentInstance}.
     * @param executionCallback The callback for execution hooks.
     * @param mode              The execution mode.
     * @param name              The name of this selector.
     */
    public HaloContentSelectorFactory(
            @NonNull Halo halo,
            @NonNull SelectorProvider<P, U> dataProvider,
            @NonNull ISelectorConverter<P, U> converter,
            @NonNull SelectorCursor2CustomClass.Factory<P, U> converterFactory,
            @Nullable InteractorExecutionCallback executionCallback,
            int mode,
            @NonNull String name) {
        super(halo, dataProvider, converter, executionCallback, mode, name);
        mConverterFactory = converterFactory;
    }

    /**
     * Provides the data as content.
     *
     * @param clazz The class to which the content will be parsed.
     * @return The parsed selector.
     */
    @Keep
    @Api(2.0)
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public <T> HaloInteractorExecutor<List<T>> asContent(@NonNull Class<T> clazz) {
        return new HaloInteractorExecutor<>(
                mHalo,
                mName,
                mConverterFactory.createList(mDataProvider, clazz, mMode),
                mExecutionCallback);
    }
}
