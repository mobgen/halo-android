package com.mobgen.halo.android.auth.pocket;

import android.support.annotation.CheckResult;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.auth.models.Pocket;
import com.mobgen.halo.android.auth.models.ReferenceContainer;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;
import com.mobgen.halo.android.sdk.core.threading.InteractorExecutionCallback;

import java.util.List;

/**
 * This selector allows the user of the sdk to select which type of content
 * he needs. This selection is made based on the raw to pocket and from pocket
 * to custom class parsed content. Check the HaloSelectorFactory to get more information.
 */
public class HaloPocketSelectorFactory<T> {
    /**
     * The converter factory for pocket data parsers.
     */
    private final SelectorPocket2DataClass.Factory mConverterFactory;

    /**
     * The halo instance
     */
    private Halo mHalo;
    /**
     * The data provider.
     */
    private PocketDataProvider mDataProvider;
    /**
     * The execution mode.
     */
    @Data.Policy
    private int mMode;
    /**
     * The name of the action to perform.
     */
    private String mName;


    /**
     * The mode selector.
     *
     * @param halo             The content api.
     * @param dataProvider     The data provider.
     * @param converterFactory The factory of converters for.
     * @param name             The name of this selector.
     */
    public HaloPocketSelectorFactory(
            @NonNull Halo halo,
            @NonNull PocketDataProvider dataProvider,
            @NonNull SelectorPocket2DataClass.Factory converterFactory,
            @NonNull String name) {
        mHalo = halo;
        mDataProvider = dataProvider;
        mName = name;

        mConverterFactory = converterFactory;
    }

    /**
     * Provides the data as Pocket.
     *
     * @return The parsed selector.
     */
    @Keep
    @Api(2.4)
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<Pocket> asPocket() {
        return new HaloInteractorExecutor<>(
                mHalo,
                mName,
                mConverterFactory.createResultData(mDataProvider, Pocket.class),
                null);
    }

    /**
     * Provides the data as custom data content.
     *
     * @param clazz The class to which the content will be parsed.
     * @return The parsed selector.
     */
    @Keep
    @Api(2.0)
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public <T> HaloInteractorExecutor<T> asCustomData(@NonNull Class<T> clazz) {
        return new HaloInteractorExecutor<>(
                mHalo,
                mName,
                mConverterFactory.createResultData(mDataProvider, clazz),
                null);
    }

    /**
     * Provides the data as Reference container
     *
     * @return The parsed selector.
     */
    @Keep
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    protected HaloInteractorExecutor<List<ReferenceContainer>> asReferencesContainer() {
        return new HaloInteractorExecutor<>(
                mHalo,
                mName,
                mConverterFactory.createResultReference(mDataProvider),
                null);
    }
}
