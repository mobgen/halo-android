package com.mobgen.halo.android.notifications.decorator;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;

import com.mobgen.halo.android.testing.BundleTestUtils;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NotificationIconDecoratorTest extends BaseNotificationDecoratorTest<NotificationIconDecorator> {

    private Context mContext;
    private Resources mResources;

    @Test
    public void setIconDrawableTest() {
        whenIcon(1, "drawable", 1);
    }

    @Test
    public void setIconMipmapTest() {
        whenIcon(1, "mipmap", 1);
    }

    @Test
    public void setDefaultApplicationIconTest() {
        PackageManager manager = mock(PackageManager.class);
        when(mContext.getPackageManager()).thenReturn(manager);
        ApplicationInfo info = mock(ApplicationInfo.class);
        info.icon = 2;
        try {
            when(manager.getApplicationInfo(null, PackageManager.GET_META_DATA)).thenReturn(info);
        } catch (PackageManager.NameNotFoundException e) {
            Assert.fail();
        }
        whenIcon(0, "drawable", 2);
    }

    @Test
    public void setDefaultApplicationIconFailTest() {
        PackageManager manager = mock(PackageManager.class);
        when(mContext.getPackageManager()).thenReturn(manager);
        ApplicationInfo info = mock(ApplicationInfo.class);
        info.icon = 2;
        try {
            when(manager.getApplicationInfo(null, PackageManager.GET_META_DATA)).thenThrow(new PackageManager.NameNotFoundException());
        } catch (PackageManager.NameNotFoundException e) {
            Assert.fail();
        }
        whenIcon(0, "drawable", 0);
    }

    private void whenIcon(final int iconId, String resourceDir, final int expectedId) {
        final boolean[] marker = new boolean[1];
        Bundle bundle = BundleTestUtils.builder().putString("icon", "myName").build();
        when(mResources.getIdentifier("myName", resourceDir, mContext.getPackageName())).thenReturn(iconId);
        when(getBuilder().setSmallIcon(any(Integer.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                marker[0] = true;
                Assert.assertEquals(expectedId, invocation.getArguments()[0]);
                return null;
            }
        });
        getDecorator().decorate(getBuilder(), bundle);
        Assert.assertTrue(marker[0]);
    }

    @Test
    public void emptyIconTest() {
        final boolean[] marker = new boolean[1];
        PackageManager manager = mock(PackageManager.class);
        when(mContext.getPackageManager()).thenReturn(manager);
        ApplicationInfo info = mock(ApplicationInfo.class);
        info.icon = 2;
        try {
            when(manager.getApplicationInfo(null, PackageManager.GET_META_DATA)).thenReturn(info);
        } catch (PackageManager.NameNotFoundException e) {
            Assert.fail();
        }
        Bundle bundle = BundleTestUtils.builder().build();
        when(getBuilder().setSmallIcon(any(Integer.class))).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                marker[0] = true;
                return null;
            }
        });
        getDecorator().decorate(getBuilder(), bundle);
        Assert.assertTrue(marker[0]);
    }

    @Override
    public NotificationIconDecorator createDecorator() {
        mContext = mock(Context.class);
        mResources = mock(Resources.class);
        when(mContext.getResources()).thenReturn(mResources);
        return new NotificationIconDecorator(mContext, null);
    }
}
