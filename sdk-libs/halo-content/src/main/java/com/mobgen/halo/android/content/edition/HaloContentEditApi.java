package com.mobgen.halo.android.content.edition;

import android.support.annotation.CheckResult;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.edition.batch.BatchInteractor;
import com.mobgen.halo.android.content.edition.batch.BatchLocalDataSource;
import com.mobgen.halo.android.content.edition.batch.BatchRemoteDataSource;
import com.mobgen.halo.android.content.edition.batch.BatchRepository;
import com.mobgen.halo.android.content.edition.batch.BatchBundleizeHelper;
import com.mobgen.halo.android.content.models.BatchOperations;
import com.mobgen.halo.android.content.models.BatchOperationResults;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.spec.HaloContentContract;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.EventId;
import com.mobgen.halo.android.framework.toolbox.bus.Subscriber;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloPluginApi;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * The edit content api allows you to add, modify or delete general content from the Android SDK.
 */
@Keep
public class HaloContentEditApi extends HaloPluginApi {

    /**
     * Batch event id that is used to notify across all the
     * listeners that are subscribed into the framework.
     */
    @Keep
    @Api(2.3)
    public static final String BATCH_FINISHED_EVENT = ":halo:event:batch_finished:";

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
     *
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
                "Create content",
                new ContentManipulationInteractor(new ContentManipulationRepository(HaloContentApi.with(halo()), new ContentManipulationRemoteDataSource(halo().framework().network())),
                        haloContentInstance, HaloRequestMethod.POST)
        );
    }


    /**
     * Update a given general content instance.
     *
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
                "Update content",
                new ContentManipulationInteractor(new ContentManipulationRepository(HaloContentApi.with(halo()), new ContentManipulationRemoteDataSource(halo().framework().network()))
                        , haloContentInstance, HaloRequestMethod.PUT)
        );
    }


    /**
     * Delete a given general content instance.
     *
     * @param haloContentInstance The general content instance to delete.
     * @return HaloInteractorExecutor
     */
    @Api(2.2)
    @Keep
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<HaloContentInstance> deleteContent(@NonNull HaloContentInstance haloContentInstance) {
        AssertionUtils.notNull(haloContentInstance, "haloContentInstance");
        return new HaloInteractorExecutor<>(halo(),
                "Delete content",
                new ContentManipulationInteractor(new ContentManipulationRepository(HaloContentApi.with(halo()), new ContentManipulationRemoteDataSource(halo().framework().network())),
                        haloContentInstance, HaloRequestMethod.DELETE)
        );
    }

    /**
     * Advanced batch operations to create, delete or update content.
     *
     * @param batchOperations The batch operations to perfom.
     * @param syncResults     Flag to perfom sync after batch.
     * @return HaloInteractorExecutor
     */
    @Api(2.3)
    @Keep
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<BatchOperationResults> batch(@NonNull BatchOperations batchOperations, boolean syncResults) {
        AssertionUtils.notNull(batchOperations, "batchOperations");
        return new HaloInteractorExecutor<>(halo(),
                "Batch content manipulation operations",
                new BatchInteractor(new BatchRepository(HaloContentApi.with(halo()), new
                        BatchRemoteDataSource(halo().framework().network()),
                        new BatchLocalDataSource(halo().framework().storage(HaloContentContract.HALO_CONTENT_STORAGE)), syncResults),
                        batchOperations)
        );
    }

    /**
     * Subscribe to receive updates from batch operations. You can receive conflicts or batch operations after gain
     * net access.
     *
     * @param listener Listener when batch finishes.
     * @return Subscription to event.
     */
    @Keep
    @NonNull
    @Api(2.3)
    @CheckResult(suggest = "You should keep a reference to the subscription to call unsubscribe when you are done.")
    public ISubscription subscribeToBatch(@NonNull final HaloContentEditApi.HaloBatchListener listener) {
        AssertionUtils.notNull(listener, "listener");
        return framework().subscribe(new Subscriber() {
            @Override
            public void onEventReceived(@NonNull Event event) {
                if (event.getData() != null) {
                    if (BatchBundleizeHelper.isBatchOperation(event.getData())) {
                        BatchOperations result = BatchBundleizeHelper.debundleizeBatchOperations(event.getData());
                        listener.onBatchConflict(result);
                    } else {
                        Pair<HaloStatus, BatchOperationResults> result = BatchBundleizeHelper.debundleizeBatchOperationsResults(event.getData());
                        listener.onBatchRetryCompleted(result.first, result.second);
                    }
                }
            }
        }, EventId.create(BATCH_FINISHED_EVENT));
    }

    /**
     * The batch process listener that listens for the event received
     * when a batch process finishes or fails due a HaloNetException
     */
    @Keep
    public interface HaloBatchListener {

        /**
         * Notifies when the batch process has some conflicts.
         *
         * @param operations The operations with conflicts.
         */
        @Keep
        @Api(2.3)
        void onBatchConflict(@Nullable BatchOperations operations);

        /**
         * Notifies when the batch process has finished so the user can perform any action.
         *
         * @param status     The status of the data received.
         * @param operations The batch operations perfomed
         */
        @Keep
        @Api(2.3)
        void onBatchRetryCompleted(@NonNull HaloStatus status, @Nullable BatchOperationResults operations);
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
