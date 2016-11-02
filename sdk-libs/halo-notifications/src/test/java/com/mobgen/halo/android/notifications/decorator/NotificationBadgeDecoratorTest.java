package com.mobgen.halo.android.notifications.decorator;

import android.os.Bundle;

import com.mobgen.halo.android.testing.BundleTestUtils;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class NotificationBadgeDecoratorTest extends BaseNotificationDecoratorTest<NotificationBadgeDecorator> {

    @Test
    public void badgeTest() {
        final boolean[] marker = new boolean[1];
        Bundle bundle = BundleTestUtils.builder().putString("badge", "1").build();
        when(getBuilder().setNumber(any(Integer.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                marker[0] = true;
                Assert.assertEquals(1, invocation.getArguments()[0]);
                return null;
            }
        });
        getDecorator().decorate(getBuilder(), bundle);
        Assert.assertTrue(marker[0]);
    }

    @Test
    public void emptyBadgeTest() {
        final boolean[] marker = new boolean[1];
        Bundle bundle = BundleTestUtils.builder().build();
        when(getBuilder().setNumber(any(Integer.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                marker[0] = true;
                Assert.assertEquals(1, invocation.getArguments()[0]);
                return null;
            }
        });
        getDecorator().decorate(getBuilder(), bundle);
        Assert.assertFalse(marker[0]);
    }

    @Test
    public void notNumericBadgeTest() {
        final boolean[] marker = new boolean[1];
        Bundle bundle = BundleTestUtils.builder().putString("badge", "aaa").build();
        when(getBuilder().setNumber(any(Integer.class))).then(new Answer<Object>() {
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
    public NotificationBadgeDecorator createDecorator() {
        return new NotificationBadgeDecorator(null);
    }
}
