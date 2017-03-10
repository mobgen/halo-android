package com.mobgen.halo.android.twofactor.mock.instrumentation;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.mobgen.halo.android.notifications.services.InstanceIDService;
import com.mobgen.halo.android.notifications.services.NotificationService;
import com.mobgen.halo.android.testing.TestUtils;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;

public class NotificationServiceInstruments {

    public static NotificationService givenANotificationService(Context context) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return TestUtils.prepareService(new NotificationService(), context);
    }

    public static InstanceIDService givenTheInstanceIDService(@NotNull Context context) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        return TestUtils.prepareService(new InstanceIDService(), context);
    }

    public static void initFirebase(@NonNull Context context) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseApp.initializeApp(context, new FirebaseOptions.Builder()
                    .setApplicationId("com.mobgen.fake")
                    .setGcmSenderId("fakeId")
                    .setApiKey("fakeApiKey")
                    .build());
        }
    }
}
