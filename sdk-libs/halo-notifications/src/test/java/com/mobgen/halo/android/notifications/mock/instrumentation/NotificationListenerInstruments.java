package com.mobgen.halo.android.notifications.mock.instrumentation;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.notifications.HaloNotificationsApi;
import com.mobgen.halo.android.notifications.callbacks.HaloNotificationListener;
import com.mobgen.halo.android.testing.CallbackFlag;

import org.jetbrains.annotations.NotNull;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class NotificationListenerInstruments {

    public static HaloNotificationListener givenANotificationListener(@NotNull final HaloNotificationsApi api, @NonNull final CallbackFlag flag, final boolean isSilent) {
        return new HaloNotificationListener() {
            @Override
            public void onNotificationReceived(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @Nullable Bundle extra) {
                flag.flagExecuted();
                assertThat(from).isEqualTo(NotificationInstruments.NOTIFICATION_FROM);
                if (isSilent) {
                    assertThat(api.getNotificationId(data)).isNull();
                } else {
                    assertThat(api.getNotificationId(data)).isNotNull();
                }
            }
        };
    }

    public static HaloNotificationListener givenAnAllNotificationListener(@NonNull final CallbackFlag flag) {
        return new HaloNotificationListener() {
            @Override
            public void onNotificationReceived(@NonNull Context context, @NonNull String from, @NonNull Bundle data, @Nullable Bundle extra) {
                flag.flagExecuted();
                assertThat(from).isNotNull();
            }
        };
    }
}
