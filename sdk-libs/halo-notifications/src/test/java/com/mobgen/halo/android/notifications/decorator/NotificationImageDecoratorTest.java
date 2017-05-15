package com.mobgen.halo.android.notifications.decorator;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.BundleTestUtils;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;

import static com.mobgen.halo.android.notifications.mock.instrumentation.HaloMock.givenADefaultHalo;
import static com.mobgen.halo.android.notifications.mock.instrumentation.NotificationImageDecoratorInstruments.givenAImageNotification;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationImageDecoratorTest extends BaseNotificationDecoratorTest<NotificationImageDecorator> {

    Halo mHalo;

    @Override
    public void onStart() throws IOException, HaloParsingException {
        mHalo = givenADefaultHalo();
    }

    @Override
    public void onDestroy() throws IOException {
        mHalo.uninstall();
    }

    @Test
    public void thatCanHandleAImageOnTheNotificationWithDefaultType() throws IOException {
        final boolean[] marker = new boolean[1];
        final String text = "This is my Text And Body";
        final String image = givenAImageNotification(mHalo.context(),"default");
        Bundle bundle = BundleTestUtils.builder().putString("body", text).putString("title", text).build();
        when(bundle.get("image")).thenReturn(image);
        when(bundle.get("image").toString()).thenReturn(image);
        when(getBuilder().setLargeIcon(any(Bitmap.class))).then(new Answer<Object>() {
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
    public void thatCanHandleAImageOnTheNotificationWithExpandedType() throws IOException {
        final boolean[] marker = new boolean[1];
        final String text = "This is my Text And Body";
        final String image = givenAImageNotification(mHalo.context(),"expanded");
        Bundle bundle = BundleTestUtils.builder().putString("body", text).putString("title", text).build();
        when(bundle.get("image")).thenReturn(image);
        when(bundle.get("image").toString()).thenReturn(image);
        when(getBuilder().setStyle(any(NotificationCompat.Style.class))).then(new Answer<Object>() {
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
    public void thatCanHandleAImageOnTheNotificationWithBackgroundType() throws IOException {
        final boolean[] marker = new boolean[1];
        final String text = "This is my Text And Body";
        final String image = givenAImageNotification(mHalo.context(),"background");
        Bundle bundle = BundleTestUtils.builder().putString("body", text).putString("title", text).build();
        when(bundle.get("image")).thenReturn(image);
        when(bundle.get("image").toString()).thenReturn(image);
        when(getBuilder().setCustomContentView(any(RemoteViews.class))).then(new Answer<Object>() {
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
    public void thatCanHandleAImageOnTheNotificationWithLeftType() throws IOException {
        final boolean[] marker = new boolean[1];
        final String text = "This is my Text And Body";
        final String image = givenAImageNotification(mHalo.context(),"left");
        Bundle bundle = BundleTestUtils.builder().putString("body", text).putString("title", text).build();
        when(bundle.get("image")).thenReturn(image);
        when(bundle.get("image").toString()).thenReturn(image);
        when(getBuilder().setCustomContentView(any(RemoteViews.class))).then(new Answer<Object>() {
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
    public void thatCanHandleAImageOnTheNotificationWithRightType() throws IOException {
        final boolean[] marker = new boolean[1];
        final String text = "This is my Text And Body";
        final String image = givenAImageNotification(mHalo.context(),"right");
        Bundle bundle = BundleTestUtils.builder().putString("body", text).putString("title", text).build();
        when(bundle.get("image")).thenReturn(image);
        when(bundle.get("image").toString()).thenReturn(image);
        when(getBuilder().setCustomContentView(any(RemoteViews.class))).then(new Answer<Object>() {
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
    public void thatCanHandleAImageOnTheNotificationWithTopType() throws IOException {
        final boolean[] marker = new boolean[1];
        final String text = "This is my Text And Body";
        final String image = givenAImageNotification(mHalo.context(),"top");
        Bundle bundle = BundleTestUtils.builder().putString("body", text).putString("title", text).build();
        when(bundle.get("image")).thenReturn(image);
        when(bundle.get("image").toString()).thenReturn(image);
        when(getBuilder().setCustomContentView(any(RemoteViews.class))).then(new Answer<Object>() {
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
    public void thatCanHandleAImageOnTheNotificationWithBottomType() throws IOException {
        final boolean[] marker = new boolean[1];
        final String text = "This is my Text And Body";
        final String image = givenAImageNotification(mHalo.context(),"bottom");
        Bundle bundle = BundleTestUtils.builder().putString("body", text).putString("title", text).build();
        when(bundle.get("image")).thenReturn(image);
        when(bundle.get("image").toString()).thenReturn(image);
        when(getBuilder().setCustomContentView(any(RemoteViews.class))).then(new Answer<Object>() {
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
    public void thatCanHandleANullImage() {
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
    public NotificationImageDecorator createDecorator() {
        return new NotificationImageDecorator(mock(Context.class),null);
    }
}
