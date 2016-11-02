package com.mobgen.halo.android.sdk.core.selectors;

import android.support.annotation.CheckResult;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;
import com.mobgen.halo.android.sdk.core.threading.InteractorExecutionCallback;

/**
 * ThreadContext that helps to do the different flows based on the needs
 * of a query.
 */
@Keep
public class HaloSelectorFactory<P, U> {
    /**
     * The halo instance
     */
    protected Halo mHalo;
    /**
     * The search query that can be performed.
     */
    protected SelectorProvider<P, U> mDataProvider;
    /**
     *
     * Converter between parsed or unparsed data.
     */
    protected ISelectorConverter<P, U> mConverter;
    /**
     * Callback that notifies the execution.
     */
    protected InteractorExecutionCallback mExecutionCallback;
    /**
     * The execution mode.
     */
    @Data.Policy
    protected int mMode;
    /**
     * The name of the action to perform.
     */
    protected String mName;

    /**
     * The mode selector.
     *
     * @param halo              The content api.
     * @param dataProvider      The data provider.
     * @param converter         The converter between different data types.
     * @param executionCallback The callback for execution hooks.
     * @param mode              The execution mode.
     * @param name              The name of this selector.
     */
    @Api(2.0)
    public HaloSelectorFactory(@NonNull Halo halo,
                               @NonNull SelectorProvider<P, U> dataProvider,
                               @Nullable ISelectorConverter<P, U> converter,
                               @Nullable InteractorExecutionCallback executionCallback,
                               @Data.Policy int mode,
                               @NonNull String name) {
        AssertionUtils.notNull(halo, "halo");
        AssertionUtils.notNull(dataProvider, "dataProvider");
        AssertionUtils.notNull(name, "name ");
        mHalo = halo;
        mDataProvider = dataProvider;
        mConverter = converter;
        mMode = mode;
        mExecutionCallback = executionCallback;
        mName = name;
    }

    /**
     * Provides the content as a cursor loader.
     *
     * @return The cursor selector.
     */
    @Api(2.0)
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<U> asRaw() {
        return new HaloInteractorExecutor<>(
                mHalo,
                mName,
                new SelectorRaw2Unparse<>(mDataProvider, mMode),
                mExecutionCallback);
    }

    /**
     * Provides the instance selector.
     *
     * @return The instance selector.
     */
    @NonNull
    @Api(2.0)
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<P> asContent() {
        return new HaloInteractorExecutor<>(
                mHalo,
                mName,
                new SelectorUnparse2Parse<>(mDataProvider, mConverter, mMode),
                mExecutionCallback);
    }
}