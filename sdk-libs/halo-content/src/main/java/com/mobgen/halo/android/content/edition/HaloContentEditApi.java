package com.mobgen.halo.android.content.edition;

import android.support.annotation.CheckResult;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.HaloEditContentOptions;
import com.mobgen.halo.android.content.models.SyncQuery;
import com.mobgen.halo.android.content.search.ContentSearchRepository;
import com.mobgen.halo.android.content.sync.ContentSyncRepository;
import com.mobgen.halo.android.framework.api.HaloStorageApi;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;


import static com.mobgen.halo.android.sdk.api.HaloApplication.halo;

/**
 *  The edit content api allows you to add, modify or delete general content from the Android SDK.
 *
 */
@Keep
public class HaloContentEditApi {

    /**
     * Add general content instance
     * @param haloEditContentOptions The new general content instance.
     * @return HaloInteractorExecutor
     */
    @Api(2.2)
    @Keep
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public static HaloInteractorExecutor<HaloContentInstance> addContent(@NonNull HaloEditContentOptions haloEditContentOptions) {
        AssertionUtils.notNull(haloEditContentOptions, "haloEditContentOptions");
        return new HaloInteractorExecutor<>(halo(),
                "Sign in with halo",
                new ContentManipulationInteractor(new ContentManipulationRepository(HaloContentApi.with(Halo.instance()),new ContentManipulationRemoteDataSource(halo().framework().network())),
                        haloEditContentOptions, HaloRequestMethod.POST)
        );
    }


    /**
     * Update a given general content instance.
     * @param haloEditContentOptions The general content instance to delete.
     * @return HaloInteractorExecutor
     */
    @Api(2.2)
    @Keep
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public static HaloInteractorExecutor<HaloContentInstance> updateContent(@NonNull HaloEditContentOptions haloEditContentOptions) {
        AssertionUtils.notNull(haloEditContentOptions, "haloEditContentOptions");
        return new HaloInteractorExecutor<>(halo(),
                "Sign in with halo",
                new ContentManipulationInteractor(new ContentManipulationRepository(HaloContentApi.with(Halo.instance()),new ContentManipulationRemoteDataSource(halo().framework().network()))
                        , haloEditContentOptions, HaloRequestMethod.PUT)
        );
    }


    /**
     * Delete a given general content instance.
     * @param haloEditContentOptions The general content instance to delete.
     * @@return HaloInteractorExecutor
     */
    @Api(2.2)
    @Keep
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public static HaloInteractorExecutor<HaloContentInstance> deleteContent(@NonNull HaloEditContentOptions haloEditContentOptions) {
        AssertionUtils.notNull(haloEditContentOptions, "haloEditContentOptions");
        return new HaloInteractorExecutor<>(halo(),
                "Sign in with halo",
                new ContentManipulationInteractor(new ContentManipulationRepository(HaloContentApi.with(Halo.instance()),new ContentManipulationRemoteDataSource(halo().framework().network())),
                        haloEditContentOptions, HaloRequestMethod.DELETE)
        );
    }
}
