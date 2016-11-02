package com.mobgen.halo.android.notifications.decorator;

import android.os.Bundle;

import com.mobgen.halo.android.testing.BundleTestUtils;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class NotificationLedDecoratorTest extends BaseNotificationDecoratorTest<NotificationLedDecorator> {

    @Test
    public void addLedColorTest() {
        final boolean[] marker = new boolean[1];
        Bundle bundle = BundleTestUtils.builder().putString("color", "#00000000").build();
        when(getBuilder().setLights(any(Integer.class), eq(NotificationLedDecorator.ON_TIME), eq(NotificationLedDecorator.OFF_TIME))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Assert.assertEquals(0, invocation.getArguments()[0]); // Assert white
                marker[0] = true;
                return null;
            }
        });
        getDecorator().decorate(getBuilder(), bundle);
        Assert.assertTrue(marker[0]);
    }

    @Test
    public void noLedColorSetTest() {
        final boolean[] marker = new boolean[1];
        Bundle bundle = BundleTestUtils.builder().build();
        when(getBuilder().setLights(any(Integer.class), eq(NotificationLedDecorator.ON_TIME), eq(NotificationLedDecorator.OFF_TIME))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                marker[0] = true;
                return null;
            }
        });
        getDecorator().decorate(getBuilder(), bundle);
        Assert.assertFalse(marker[0]);
    }

    @Override
    public NotificationLedDecorator createDecorator() {
        return new NotificationLedDecorator(null);
    }
}
