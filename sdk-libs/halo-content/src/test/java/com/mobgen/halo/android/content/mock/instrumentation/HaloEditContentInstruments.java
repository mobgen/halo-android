package com.mobgen.halo.android.content.mock.instrumentation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.content.edition.HaloContentEditApi;
import com.mobgen.halo.android.content.edition.batch.BatchBundleizeHelper;
import com.mobgen.halo.android.content.models.BatchOperationResults;
import com.mobgen.halo.android.content.models.BatchOperations;
import com.mobgen.halo.android.content.models.BatchOperator;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.EventId;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.CallbackFlag;

import java.util.HashMap;
import java.util.Map;

import static com.mobgen.halo.android.content.edition.HaloContentEditApi.BATCH_FINISHED_EVENT;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloEditContentInstruments {

    public static CallbackV2<HaloContentInstance> givenAContentSuccessCallback(final CallbackFlag flag, final String textToTest) {
        return new CallbackV2<HaloContentInstance>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<HaloContentInstance> result) {
                flag.flagExecuted();
                assertThat(textToTest).isEqualTo(result.data().getItemId());
            }
        };
    }

    public static CallbackV2<BatchOperationResults> givenABatchContentSuccessCallback(final CallbackFlag flag, final boolean isFromBackground) {
        return new CallbackV2<BatchOperationResults>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<BatchOperationResults> result) {
                flag.flagExecuted();
                if (isFromBackground) {
                    Bundle batchResult = BatchBundleizeHelper.bundleizeBatchOperationsResults(result);
                    Halo.instance().framework().emit(new Event(EventId.create(BATCH_FINISHED_EVENT), batchResult));
                } else {
                    if (result.status().isOk()) {
                        assertThat(BatchOperator.TRUNCATE).isEqualTo(result.data().getContentResult().get(0).getOperation());
                        assertThat(BatchOperator.TRUNCATE).isEqualTo(result.data().getContentResult().get(1).getOperation());
                        assertThat(BatchOperator.CREATE).isEqualTo(result.data().getContentResult().get(2).getOperation());
                        assertThat(BatchOperator.UPDATE).isEqualTo(result.data().getContentResult().get(3).getOperation());
                        assertThat(BatchOperator.CREATEORUPDATE).isEqualTo(result.data().getContentResult().get(4).getOperation());
                        assertThat(BatchOperator.DELETE).isEqualTo(result.data().getContentResult().get(5).getOperation());
                    }
                }
            }
        };
    }

    public static ISubscription givenABatchOperationsEventSubscription(Halo halo, final boolean isFromBackground) {
        return HaloContentEditApi.with(halo)
                .subscribeToBatch(new HaloContentEditApi.HaloBatchListener() {
                    @Override
                    public void onBatchConflict(@Nullable BatchOperations operations) {
                        if (!isFromBackground) {
                            assertThat("From Android SDK").isEqualTo(operations.getCreated().get(0).getName());
                            assertThat("586a47f836a6b01300ec9f00").isEqualTo(operations.getUpdated().get(0).getModuleId());
                            assertThat("From Android SDK").isEqualTo(operations.getDeleted().get(0).getName());
                        }
                    }

                    @Override
                    public void onBatchRetrySuccess(@NonNull HaloStatus status, @Nullable BatchOperationResults operations) {
                        if (status.isOk()) {
                            assertThat(BatchOperator.TRUNCATE).isEqualTo(operations.getContentResult().get(0).getOperation());
                            assertThat(BatchOperator.TRUNCATE).isEqualTo(operations.getContentResult().get(1).getOperation());
                            assertThat(BatchOperator.CREATE).isEqualTo(operations.getContentResult().get(2).getOperation());
                            assertThat(BatchOperator.UPDATE).isEqualTo(operations.getContentResult().get(3).getOperation());
                            assertThat(BatchOperator.CREATEORUPDATE).isEqualTo(operations.getContentResult().get(4).getOperation());
                            assertThat(BatchOperator.DELETE).isEqualTo(operations.getContentResult().get(5).getOperation());
                        }
                    }
                });
    }

    public static CallbackV2<BatchOperationResults> givenABatchContentSuccessScheduledCallback(final CallbackFlag flag) {
        return new CallbackV2<BatchOperationResults>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<BatchOperationResults> result) {
                flag.flagExecuted();
            }
        };
    }

    public static ISubscription givenABatchOperationsEventSubscriptionScheduled(Halo halo) {
        return HaloContentEditApi.with(halo)
                .subscribeToBatch(new HaloContentEditApi.HaloBatchListener() {
                    @Override
                    public void onBatchConflict(@Nullable BatchOperations operations) {
                    }

                    @Override
                    public void onBatchRetrySuccess(@NonNull HaloStatus status, @Nullable BatchOperationResults operations) {
                        assertThat(BatchOperator.TRUNCATE).isEqualTo(operations.getContentResult().get(0).getOperation());
                    }
                });
    }

    public static CallbackV2<HaloContentInstance> givenAContentAuthenticationErrorCallback(final CallbackFlag flag) {
        return new CallbackV2<HaloContentInstance>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<HaloContentInstance> result) {
                flag.flagExecuted();
                assertThat(result.status().isSecurityError()).isEqualTo(true);
            }
        };
    }

    public static HaloContentInstance givenAUpdateHaloContentEditOptions() {
        Map<String, String> values = new HashMap<>();
        values.put("Title", "My title");

        HaloContentInstance.Builder instanceBuilder = new HaloContentInstance.Builder("halomodulename")
                .withModuleId("586a47f836a6b01300ec9f00")
                .withName("From Android SDK")
                .withContentData(values);

        return instanceBuilder.build();
    }

    public static HaloContentInstance givenANewHaloContentEditOptions() {
        Map<String, String> values = new HashMap<>();
        values.put("Title", "My title");

        HaloContentInstance.Builder instanceBuilder = new HaloContentInstance.Builder("halomodulename")
                .withId("5874c5f06a3a0d1e00c8039d")
                .withModuleId("586a47f836a6b01300ec9f00")
                .withName("From Android SDK")
                .withContentData(values);

        return instanceBuilder.build();
    }
}
