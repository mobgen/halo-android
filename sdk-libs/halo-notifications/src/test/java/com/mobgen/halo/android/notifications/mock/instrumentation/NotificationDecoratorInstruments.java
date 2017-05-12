package com.mobgen.halo.android.notifications.mock.instrumentation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.mobgen.halo.android.notifications.decorator.HaloNotificationDecorator;
import com.mobgen.halo.android.testing.CallbackFlag;

public class NotificationDecoratorInstruments {

    public static HaloNotificationDecorator givenANullReturningDecorator() {
        return new HaloNotificationDecorator() {
            @Override
            public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
                return null;
            }
        };
    }

    public static HaloNotificationDecorator givenADefaultNotificationDecorator() {
        return new HaloNotificationDecorator() {
            @Override
            public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
                return builder;
            }
        };
    }

    public static HaloNotificationDecorator givenADefaultNotificationDecorator(@NonNull final CallbackFlag flag) {
        return new HaloNotificationDecorator() {
            @Override
            public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
                flag.flagExecuted();
                return builder;
            }
        };
    }
}
