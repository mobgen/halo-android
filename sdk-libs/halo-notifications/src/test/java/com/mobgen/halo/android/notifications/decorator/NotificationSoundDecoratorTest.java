package com.mobgen.halo.android.notifications.decorator;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import com.mobgen.halo.android.testing.BundleTestUtils;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RuntimeEnvironment;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationSoundDecoratorTest extends BaseNotificationDecoratorTest<NotificationSoundDecorator> {

    @Test
    public void defaultSoundTest() {
        final boolean[] marker = new boolean[1];
        Bundle bundle = BundleTestUtils.builder().putString("sound", "default").build();
        when(getBuilder().setSound(any(Uri.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                marker[0] = true;
                Assert.assertEquals(Uri.parse("content://settings/system/notification_sound"), invocation.getArguments()[0]);
                return null;
            }
        });
        getDecorator().decorate(getBuilder(), bundle);
        Assert.assertTrue(marker[0]);
    }

    @Test
    public void soundTest() {
        //Mock decorator context
        final boolean[] marker = new boolean[1];
        final String ringtone = "adara";
        Context context = mock(Context.class);
        Resources res = mock(Resources.class);
        NotificationSoundDecorator decorator = new NotificationSoundDecorator(context, null);
        when(context.getResources()).thenReturn(res);
        when(res.getIdentifier(ringtone, "raw", context.getPackageName())).thenReturn(1);
        when(res.getResourcePackageName(1)).thenReturn("com.mobgen.halo.android.sdk");
        when(res.getResourceTypeName(1)).thenReturn("raw");
        when(res.getResourceEntryName(1)).thenReturn(ringtone);

        Bundle bundle = BundleTestUtils.builder().putString("sound", ringtone).build();
        when(getBuilder().setSound(any(Uri.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                marker[0] = true;
                Assert.assertEquals(Uri.parse("android.resource://com.mobgen.halo.android.sdk/raw/" + ringtone), invocation.getArguments()[0]);
                return null;
            }
        });
        decorator.decorate(getBuilder(), bundle);
        Assert.assertTrue(marker[0]);
    }

    @Test
    public void emptySoundTest() {
        final boolean[] marker = new boolean[1];
        Bundle bundle = BundleTestUtils.builder().build();
        when(getBuilder().setSound(any(Uri.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                marker[0] = true;
                return null;
            }
        });
        getDecorator().decorate(getBuilder(), bundle);
        Assert.assertFalse(marker[0]);
    }

    @Test
    public void invalidSoundTest() {
        final boolean[] marker = new boolean[1];
        Bundle bundle = BundleTestUtils.builder().putString("sound", "fakeSound").build();
        when(getBuilder().setSound(any(Uri.class))).then(new Answer<Object>() {
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
    public NotificationSoundDecorator createDecorator() {
        return new NotificationSoundDecorator(RuntimeEnvironment.application, null);
    }
}
