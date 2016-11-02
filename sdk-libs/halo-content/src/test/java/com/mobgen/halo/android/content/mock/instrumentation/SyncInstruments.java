package com.mobgen.halo.android.content.mock.instrumentation;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.mock.dummy.DummyItem;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.HaloSyncLog;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.testing.CallbackFlag;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class SyncInstruments {

    @NonNull
    public static HaloContentApi.HaloSyncListener givenASyncListener(final CallbackFlag flag) {
        return new HaloContentApi.HaloSyncListener() {
            @Override
            public void onSyncFinished(@NonNull HaloStatus status, @Nullable HaloSyncLog log) {
                flag.flagExecuted();
                assertThat(status.isOk()).isTrue();
                assertThat(log).isNotNull();
            }
        };
    }

    public static CallbackV2<List<HaloContentInstance>> givenACallbackWithEmptyData(@NonNull final CallbackFlag flag) {
        return new CallbackV2<List<HaloContentInstance>>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<List<HaloContentInstance>> result) {
                flag.flagExecuted();
                assertThat(result.status().isOk()).isTrue();
                assertThat(result.status().isLocal()).isTrue();
                assertThat(result.data()).isEmpty();
            }
        };
    }

    @SuppressWarnings("all")
    public static CallbackV2<List<HaloContentInstance>> givenACallbackThatCheckNewModuleInstances(@NonNull final CallbackFlag flag) {
        return new CallbackV2<List<HaloContentInstance>>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<List<HaloContentInstance>> result) {
                flag.flagExecuted();
                assertThat(result.status().isLocal()).isTrue();
                assertThat(result.status().isError()).isFalse();
                assertThat(result.data()).isNotNull();
                assertThat(result.data().size()).isEqualTo(3);
                assertThat(result.data()).extracting("values").isNotNull();
            }
        };
    }

    @SuppressWarnings("all")
    public static CallbackV2<List<DummyItem>> givenACallbackThatCheckParsedInstances(@NonNull final CallbackFlag flag, final String... fooExpectations) {
        return new CallbackV2<List<DummyItem>>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<List<DummyItem>> result) {
                flag.flagExecuted();
                assertThat(result.status().isLocal()).isTrue();
                assertThat(result.status().isError()).isFalse();
                assertThat(result.data()).isNotNull();
                assertThat(result.data().size()).isEqualTo(fooExpectations.length);
                assertThat(result.data()).extracting("foo").containsExactly((Object[]) fooExpectations);
            }
        };
    }

    @SuppressWarnings("all")
    public static CallbackV2<List<HaloSyncLog>> givenACallbackThatChecksLogs(final CallbackFlag flag, final int logAmount) {
        return new CallbackV2<List<HaloSyncLog>>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<List<HaloSyncLog>> result) {
                flag.flagExecuted();
                assertThat(result.status().isLocal()).isTrue();
                assertThat(result.status().isOk()).isTrue();
                assertThat(result.data()).isNotEmpty();
                assertThat(result.data().size()).isEqualTo(logAmount);
            }
        };
    }
}
