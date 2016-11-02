package com.mobgen.halo.android.notifications.decorator;

import android.support.v4.app.NotificationCompat;

import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.Before;

import static org.mockito.Mockito.mock;

public abstract class BaseNotificationDecoratorTest<T extends HaloNotificationDecorator> extends HaloRobolectricTest {

    private NotificationCompat.Builder mBuilder;
    private T mDecorator;

    @Before
    public void initialize() {
        mBuilder = mock(NotificationCompat.Builder.class);
        mDecorator = createDecorator();
    }

    public abstract T createDecorator();

    public T getDecorator() {
        return mDecorator;
    }

    public NotificationCompat.Builder getBuilder() {
        return mBuilder;
    }
}
