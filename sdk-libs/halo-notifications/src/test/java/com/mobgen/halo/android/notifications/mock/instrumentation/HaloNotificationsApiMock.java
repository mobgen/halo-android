package com.mobgen.halo.android.notifications.mock.instrumentation;

import android.support.annotation.NonNull;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mobgen.halo.android.notifications.HaloNotificationsApi;
import com.mobgen.halo.android.notifications.events.HaloPushEventsApi;
import com.mobgen.halo.android.sdk.api.Halo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HaloNotificationsApiMock {

    public static final String TOKEN = "hardcoded_token";

    public static HaloNotificationsApi givenAContentApi(@NonNull Halo halo) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<HaloNotificationsApi> mockCreationMethod = HaloNotificationsApi.class.getDeclaredConstructor(Halo.class, FirebaseInstanceId.class);
        mockCreationMethod.setAccessible(true);
        HaloNotificationsApi api = mockCreationMethod.newInstance(halo, givenAMockedInstanceId());
        mockCreationMethod.setAccessible(false);
        return api;
    }

    public static FirebaseInstanceId givenAMockedInstanceId() {
        FirebaseInstanceId firebaseInstanceId = mock(FirebaseInstanceId.class);
        when(firebaseInstanceId.getToken()).thenReturn(TOKEN);
        return firebaseInstanceId;
    }

    public static HaloPushEventsApi givenANotificationEventApi(@NonNull Halo halo) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<HaloPushEventsApi> mockCreationMethod = HaloPushEventsApi.class.getDeclaredConstructor(Halo.class);
        mockCreationMethod.setAccessible(true);
        HaloPushEventsApi api = mockCreationMethod.newInstance(halo);
        mockCreationMethod.setAccessible(false);
        return api;
    }
}
