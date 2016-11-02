package com.mobgen.halo.android.notifications.decorator;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.mobgen.halo.android.testing.HaloRobolectricTest;

import junit.framework.Assert;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class NotificationDecoratorTest extends HaloRobolectricTest {

    @Test
    public void decorateTest() {
        StubNotificationDecorator secondDecorator = new StubNotificationDecorator();
        StubNotificationDecorator firstDecorator = new StubNotificationDecorator(secondDecorator);
        Assert.assertFalse(secondDecorator.chained);
        Assert.assertFalse(firstDecorator.chained);
        firstDecorator.decorate(mock(NotificationCompat.Builder.class), mock(Bundle.class));
        Assert.assertTrue(secondDecorator.chained);
        Assert.assertTrue(firstDecorator.chained);
    }

    public class StubNotificationDecorator extends HaloNotificationDecorator {

        private boolean chained;

        public StubNotificationDecorator() {
            super();
        }

        public StubNotificationDecorator(HaloNotificationDecorator decorator) {
            super(decorator);
        }

        @Override
        public NotificationCompat.Builder decorate(@NonNull NotificationCompat.Builder builder, @NonNull Bundle bundle) {
            chained = true;
            return chain(builder, bundle);
        }
    }
}
