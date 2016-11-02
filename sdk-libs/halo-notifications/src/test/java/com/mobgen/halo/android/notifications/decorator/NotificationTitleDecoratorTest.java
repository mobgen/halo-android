package com.mobgen.halo.android.notifications.decorator;

import android.os.Bundle;

import com.mobgen.halo.android.testing.BundleTestUtils;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class NotificationTitleDecoratorTest extends BaseNotificationDecoratorTest<NotificationTitleDecorator> {

    @Test
    public void messageTest() {
        final boolean[] marker = new boolean[1];
        final String text = "This is my Text";
        Bundle bundle = BundleTestUtils.builder().putString("title", text).build();
        when(getBuilder().setContentTitle(any(String.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                marker[0] = true;
                Assert.assertEquals(text, invocation.getArguments()[0]);
                return null;
            }
        });
        getDecorator().decorate(getBuilder(), bundle);
        Assert.assertTrue(marker[0]);
    }

    @Test
    public void emptyMessageTest() {
        final boolean[] marker = new boolean[1];
        Bundle bundle = BundleTestUtils.builder().build();
        when(getBuilder().setContentTitle(any(String.class))).then(new Answer<Object>() {
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
    public NotificationTitleDecorator createDecorator() {
        return new NotificationTitleDecorator(null);
    }
}
