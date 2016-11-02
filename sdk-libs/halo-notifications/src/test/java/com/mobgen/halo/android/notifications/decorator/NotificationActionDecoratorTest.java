package com.mobgen.halo.android.notifications.decorator;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.mobgen.halo.android.testing.BundleTestUtils;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationActionDecoratorTest extends BaseNotificationDecoratorTest<NotificationActionDecorator> {

    @Test
    public void testDecorator() {
        final boolean[] marker = new boolean[1];
        Bundle bundle = BundleTestUtils.builder().putString("click_action", "action").build();
        getDecorator().setIntent(mock(Intent.class));
        when(getBuilder().setContentIntent(any(PendingIntent.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                marker[0] = true;
                return null;
            }
        });
        getDecorator().decorate(getBuilder(), bundle);
        Assert.assertTrue(marker[0]);
    }

    @Test
    public void testDecoratorEmptyAction() {
        final boolean[] marker = new boolean[1];
        Bundle bundle = BundleTestUtils.builder().build();
        Intent intent = mock(Intent.class);
        when(intent.setAction(any(String.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Assert.assertEquals("com.mobgen.halo.android.sdk.notifications.OPEN", invocation.getArguments()[0]);
                marker[0] = true;
                return null;
            }
        });
        getDecorator().setIntent(intent);
        getDecorator().decorate(getBuilder(), bundle);
        Assert.assertTrue(marker[0]);
    }

    @Override
    public NotificationActionDecorator createDecorator() {
        return new NotificationActionDecorator(mock(Context.class), null);
    }
}
