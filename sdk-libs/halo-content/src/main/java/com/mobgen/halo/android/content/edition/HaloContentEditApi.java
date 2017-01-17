package com.mobgen.halo.android.content.edition;

import android.support.annotation.CheckResult;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloPluginApi;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 *  The edit content api allows you to add, modify or delete general content from the Android SDK.
 *
 */
@Keep
public class HaloContentEditApi extends HaloPluginApi {


    /**
     * Constructor that accepts halo.
     *
     * @param halo The halo instance.
     */
    private HaloContentEditApi(@NonNull Halo halo) {
        super(halo);
    }

    /**
     * Creates the content edit api.
     *
     * @param halo The halo instance.
     * @return The content edit api instance.
     */
    @Api(2.2)
    @Keep
    @NonNull
    public static HaloContentEditApi with(@NonNull Halo halo) {
        return new Builder(halo).build();
    }

    /**
     * Add general content instance
     * @param haloContentInstance The new general content instance.
     * @return HaloInteractorExecutor
     */
    @Api(2.2)
    @Keep
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<HaloContentInstance> addContent(@NonNull HaloContentInstance haloContentInstance) {
        AssertionUtils.notNull(haloContentInstance, "haloContentInstance");
        return new HaloInteractorExecutor<>(halo(),
                "Sign in with halo",
                new ContentManipulationInteractor(new ContentManipulationRepository(HaloContentApi.with(halo()),new ContentManipulationRemoteDataSource(halo().framework().network())),
                        haloContentInstance, HaloRequestMethod.POST)
        );
    }


    /**
     * Update a given general content instance.
     * @param haloContentInstance The general content instance to delete.
     * @return HaloInteractorExecutor
     */
    @Api(2.2)
    @Keep
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<HaloContentInstance> updateContent(@NonNull HaloContentInstance haloContentInstance) {
        AssertionUtils.notNull(haloContentInstance, "haloContentInstance");
        return new HaloInteractorExecutor<>(halo(),
                "Sign in with halo",
                new ContentManipulationInteractor(new ContentManipulationRepository(HaloContentApi.with(halo()),new ContentManipulationRemoteDataSource(halo().framework().network()))
                        , haloContentInstance, HaloRequestMethod.PUT)
        );
    }


    /**
     * Delete a given general content instance.
     * @param haloContentInstance The general content instance to delete.
     * @@return HaloInteractorExecutor
     */
    @Api(2.2)
    @Keep
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<HaloContentInstance> deleteContent(@NonNull HaloContentInstance haloContentInstance) {
        AssertionUtils.notNull(haloContentInstance, "haloContentInstance");
        return new HaloInteractorExecutor<>(halo(),
                "Sign in with halo",
                new ContentManipulationInteractor(new ContentManipulationRepository(HaloContentApi.with(halo()),new ContentManipulationRemoteDataSource(halo().framework().network())),
                        haloContentInstance, HaloRequestMethod.DELETE)
        );
    }


    /**
     * The builder for the content edit api.
     */
    @Keep
    public static class Builder implements IBuilder<HaloContentEditApi> {
        /**
         * The edit content api.
         */
        @NonNull
        private HaloContentEditApi mEditContentApi;

        /**
         * The social api builder.
         *
         * @param halo The halo builder.
         */
        private Builder(@NonNull final Halo halo) {
            mEditContentApi = new HaloContentEditApi(halo);
        }

        @NonNull
        @Override
        public HaloContentEditApi build() {
            return mEditContentApi;
        }
    }
}
