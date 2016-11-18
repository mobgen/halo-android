package com.mobgen.halo.android.social.mock.instrumentation;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.SparseArray;

import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.social.HaloSocialApi;
import com.mobgen.halo.android.social.authenticator.AccountManagerHelper;
import com.mobgen.halo.android.social.models.HaloUserProfile;
import com.mobgen.halo.android.social.models.IdentifiedUser;
import com.mobgen.halo.android.social.providers.SocialProvider;
import com.mobgen.halo.android.social.providers.facebook.FacebookSocialProvider;
import com.mobgen.halo.android.social.providers.google.GoogleSocialProvider;
import com.mobgen.halo.android.testing.CallbackFlag;

import java.lang.reflect.Field;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

public class HaloSocialApiInstrument {

    public static CallbackV2<IdentifiedUser> givenAHaloSocialProfileIdentifiedCallback(final CallbackFlag flag, final String emailUser) {
        return new CallbackV2<IdentifiedUser>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<IdentifiedUser> result) {
                flag.flagExecuted();
                assertThat(result.status().isOk()).isTrue();
                assertThat(result.data().getUser().getEmail()).isEqualTo(emailUser);
            }
        };
    }

    public static CallbackV2<HaloUserProfile> givenAHaloSocialProfileRegisteredCallback(final CallbackFlag flag, final String emailUser) {
        return new CallbackV2<HaloUserProfile>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<HaloUserProfile> result) {
                flag.flagExecuted();
                assertThat(result.status().isOk()).isTrue();
                assertThat(result.data().getEmail()).isEqualTo(emailUser);
            }
        };
    }


    public static SocialProvider getSocialProvider(HaloSocialApi haloSocialApi, int type) {
        Field f = null;
        try {
            f = HaloSocialApi.class.getDeclaredField("mProviders");
        } catch (NoSuchFieldException e) {

        }
        f.setAccessible(true);
        try {
            SparseArray<SocialProvider> mProviders = (SparseArray<SocialProvider>) f.get(haloSocialApi);
            return mProviders.get(type);
        } catch (IllegalAccessException e) {

        }
        return null;
    }

    public static void setFacebookSocialProviderToken(FacebookSocialProvider facebookSocialProvider){
        Field f = null;
        try {
            f = FacebookSocialProvider.class.getDeclaredField("mFacebookToken");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        try {
            f.set(facebookSocialProvider, "atoken");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void setGooglesSocialProviderToken(GoogleSocialProvider googlesSocialProvider) {
        Field f = null;
        try {
            f = GoogleSocialProvider.class.getDeclaredField("mGoogleToken");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
        try {
            f.set(googlesSocialProvider, "atoken");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
