package com.mobgen.halo.android.notifications.decorator;

import android.os.Bundle;

import com.mobgen.halo.android.testing.BundleTestUtils;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class NotificationColorDecoratorTest extends BaseNotificationDecoratorTest<NotificationColorDecorator> {

    @Test
    public void addColorTest() {
        final boolean[] marker = new boolean[1];
        Bundle bundle = BundleTestUtils.builder().putString("color", "#00000000").build();
        when(getBuilder().setColor(any(Integer.class))).then(new Answer<Object>() {
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
    public void noColorSetTest() {
        final boolean[] marker = new boolean[1];
        Bundle bundle = BundleTestUtils.builder().build();
        when(getBuilder().setColor(any(Integer.class))).then(new Answer<Object>() {
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
    public NotificationColorDecorator createDecorator() {
        return new NotificationColorDecorator(null);
    }
}
