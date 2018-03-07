package com.mobgen.halo.android.notifications.mock.instrumentation;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.notifications.events.NotificationEventsActions;
import com.mobgen.halo.android.notifications.models.HaloPushEvent;
import com.mobgen.halo.android.testing.CallbackFlag;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class NotificationActionsInstruments {

    public static HaloPushEvent givenAPushEvent(@NotificationEventsActions.EventType String type) {
        return new HaloPushEvent.Builder("my awesome alias")
                .withAction(type)
                .withSchedule("scheduleId")
                .build();
    }

    public static CallbackV2<HaloPushEvent> givenAPushEventCallback(final CallbackFlag flag, @NotificationEventsActions.EventType final String type) {
        return new CallbackV2<HaloPushEvent>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<HaloPushEvent> result) {
                flag.flagExecuted();
                assertThat(result.status().isOk()).isTrue();
                assertThat(result.data().getDevice()).isEqualTo("myalias");
                assertThat(result.data().getSchedule()).isEqualTo("mypushId");
                assertThat(result.data().getAction()).isEqualTo(type);
            }
        };
    }
}
