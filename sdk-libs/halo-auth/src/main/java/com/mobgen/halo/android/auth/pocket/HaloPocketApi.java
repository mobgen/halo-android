package com.mobgen.halo.android.auth.pocket;

import android.support.annotation.CheckResult;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.auth.models.Pocket;
import com.mobgen.halo.android.auth.models.PocketOperation;
import com.mobgen.halo.android.auth.models.ReferenceFilter;
import com.mobgen.halo.android.auth.models.ReferenceContainer;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloPluginApi;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * Created by f.souto.gonzalez on 19/06/2017.
 */

/**
 * Plugin for the pocket data of identified user. It allows to store custom user data information
 * and arrays of references.
 */
@Keep
public class HaloPocketApi extends HaloPluginApi {

    /**
     * Constructor for the halo plugin.
     *
     * @param halo The halo instance.
     */
    private HaloPocketApi(@NonNull Halo halo) {
        super(halo);
    }

    /**
     * Creates the pocket api.
     *
     * @param halo The halo instance.
     * @return The social api instance.
     */
    @Keep
    public static HaloPocketApi.Builder with(@NonNull Halo halo) {
        return new HaloPocketApi.Builder(halo);
    }

    /**
     * Get the current pocket references and data.
     */
    @Keep
    @Api(2.4)
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<Pocket> get() {
        String referenceFilter = new ReferenceFilter.Builder().build().getAll();
        return new HaloInteractorExecutor<>(halo(),
                "Get user data and references",
                new PocketInteractor(new PocketRepository(new PocketRemoteDataSource(halo().framework().network())), referenceFilter, null, PocketOperation.GET)
        );
    }

    /**
     * Save currente pocket with references and data.
     *
     * @param pocket The pocket to save.
     */
    @Keep
    @Api(2.4)
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<Pocket> save(Pocket pocket) {
        return new HaloInteractorExecutor<>(halo(),
                "Save user data and references",
                new PocketInteractor(new PocketRepository(new PocketRemoteDataSource(halo().framework().network())), null, pocket, PocketOperation.SAVE)
        );
    }

    /**
     * Get only filtered references.
     *
     * @param referenceFilters The references filter.
     */
    @Keep
    @Api(2.4)
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<Pocket> getReferences(ReferenceFilter referenceFilters) {
        String referenceFilter = referenceFilters.getCurrentReferences();
        return new HaloInteractorExecutor<>(halo(),
                "Get references",
                new PocketInteractor(new PocketRepository(new PocketRemoteDataSource(halo().framework().network())), referenceFilter, null, PocketOperation.GET)
        );
    }

    /**
     * Save the references on the current pocket.
     *
     * @param referenceContainer The references to save.
     */
    @Keep
    @Api(2.4)
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<Pocket> saveReferences(ReferenceContainer... referenceContainer) {
        Pocket pocket = new Pocket.Builder().withReferences(referenceContainer).build();
        return new HaloInteractorExecutor<>(halo(),
                "Save references",
                new PocketInteractor(new PocketRepository(new PocketRemoteDataSource(halo().framework().network())), null, pocket, PocketOperation.SAVE)
        );
    }

    /**
     * Get the custom pocket data.
     */
    @Keep
    @Api(2.4)
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<Pocket> getData() {
        String referenceFilter = new ReferenceFilter.Builder().build().noReferences();
        return new HaloInteractorExecutor<>(halo(),
                "Get custom user data without references",
                new PocketInteractor(new PocketRepository(new PocketRemoteDataSource(halo().framework().network())), referenceFilter, null, PocketOperation.GET)
        );
    }

    /**
     * Save current custom pocket data.
     *
     * @param data The data to save.
     */
    @Keep
    @Api(2.4)
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<Pocket> saveData(Object data) {
        Pocket pocket = new Pocket.Builder().withData(data).build();
        return new HaloInteractorExecutor<>(halo(),
                "Save custom user data without references",
                new PocketInteractor(new PocketRepository(new PocketRemoteDataSource(halo().framework().network())), null, pocket, PocketOperation.SAVE)
        );
    }

    /**
     * The builder for the pocket api.
     */
    @Keep
    public static class Builder implements IBuilder<HaloPocketApi> {

        HaloPocketApi mPocketApi;

        /**
         * The pocket api builder.
         *
         * @param halo The halo builder.
         */
        private Builder(@NonNull final Halo halo) {
            mPocketApi = new HaloPocketApi(halo);
        }

        @NonNull
        @Override
        public HaloPocketApi build() {
            return mPocketApi;
        }
    }
}
