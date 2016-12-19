package com.mobgen.halo.android.auth.social;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.auth.HaloAuthApi;
import com.mobgen.halo.android.auth.authenticator.AccountManagerHelper;
import com.mobgen.halo.android.auth.mock.instrumentation.HaloManagerApiShadow;
import com.mobgen.halo.android.auth.mock.instrumentation.StringShadowResources;
import com.mobgen.halo.android.auth.models.HaloAuthProfile;
import com.mobgen.halo.android.auth.models.HaloUserProfile;
import com.mobgen.halo.android.auth.models.IdentifiedUser;
import com.mobgen.halo.android.auth.providers.SocialNotAvailableException;
import com.mobgen.halo.android.auth.providers.SocialProvider;
import com.mobgen.halo.android.auth.providers.facebook.FacebookSocialProvider;
import com.mobgen.halo.android.auth.providers.google.GoogleSocialProvider;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.junit.Test;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static com.mobgen.halo.android.auth.mock.fixtures.ServerFixtures.LOGIN_SUCESS;
import static com.mobgen.halo.android.auth.mock.fixtures.ServerFixtures.REGISTER_SUCESS;
import static com.mobgen.halo.android.auth.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.auth.mock.instrumentation.HaloMock.givenADefaultHalo;
import static com.mobgen.halo.android.auth.mock.instrumentation.HaloSocialApiInstrument.getSocialProvider;
import static com.mobgen.halo.android.auth.mock.instrumentation.HaloSocialApiInstrument.givenAHaloSocialProfileIdentifiedCallback;
import static com.mobgen.halo.android.auth.mock.instrumentation.HaloSocialApiInstrument.givenAHaloSocialProfileRegisteredCallback;
import static com.mobgen.halo.android.auth.mock.instrumentation.HaloSocialApiInstrument.setFacebookSocialProviderToken;
import static com.mobgen.halo.android.auth.mock.instrumentation.HaloSocialApiInstrument.setGooglesSocialProviderToken;
import static com.mobgen.halo.android.auth.mock.instrumentation.HaloSocialApiMock.givenASocialApiWithAllNetworksAvailable;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;


@Config(shadows = {StringShadowResources.class, HaloManagerApiShadow.class})
public class HaloAuthApiTest extends HaloRobolectricTest {

    private MockServer mMockServer;
    private static Halo mHalo;
    private HaloAuthApi mHaloAuthApi;
    private CallbackFlag mCallbackFlag;
    private Context mContext;

    @Override
    public void onStart() throws IOException, HaloParsingException {
        mMockServer = MockServer.create();
        mHalo = givenADefaultHalo(mMockServer.start());
        mHaloAuthApi = givenASocialApiWithAllNetworksAvailable(mHalo);
        mCallbackFlag = newCallbackFlag();
        mContext = mHalo.context();
        givenPermissions();
    }

    @Override
    public void onDestroy() throws IOException {
        mHalo.uninstall();
        mMockServer.shutdown();
    }

    @Test
    public void thatCanCreateAHaloSocialApiAttachedToHalo() {
        assertThat(mHaloAuthApi).isNotNull();
    }

    @Test
    public void thatRecoverySessionWithHaloProvider() throws SecurityException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        givenAHaloAccount();
        mHalo.getCore().haloAuthRecover().recoverAccount();
        assertThat(mHaloAuthApi.isSocialNetworkAvailable(HaloAuthApi.SOCIAL_HALO)).isTrue();
    }

    @Test
    public void thatRecoverySessionWithFacebookProvider() throws SecurityException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        givenAFacebookAccount();
        mHalo.getCore().haloAuthRecover().recoverAccount();
        assertThat(mHaloAuthApi.isSocialNetworkAvailable(HaloAuthApi.SOCIAL_FACEBOOK)).isTrue();
    }

    @Test
    public void thatRecoverySessionWithGoogleProvider() throws SecurityException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        givenAGoogleAccount();
        mHalo.getCore().haloAuthRecover().recoverAccount();
        assertThat(mHaloAuthApi.isSocialNetworkAvailable(HaloAuthApi.SOCIAL_GOOGLE_PLUS)).isTrue();
    }

    @Test
    public void thatLoginWithHaloProvider() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        CallbackV2<IdentifiedUser> callback = givenAHaloSocialProfileIdentifiedCallback(mCallbackFlag, "account@mobgen.com");
        HaloAuthProfile authProfile = new HaloAuthProfile("account@mobgen.com", "securepass");
        mHaloAuthApi.loginWithHalo(HaloAuthApi.SOCIAL_HALO, authProfile, callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }


    @Test
    public void thatNotLoginWithFacebookProviderWithoutGivenToken() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        givenAFacebookAccount();
        CallbackV2<IdentifiedUser> callback = givenAHaloSocialProfileIdentifiedCallback(mCallbackFlag, "account@mobgen.com");
        mHaloAuthApi.loginWithSocial(HaloAuthApi.SOCIAL_FACEBOOK, callback);
        assertThat(mCallbackFlag.isFlagged()).isFalse();
    }


    @Test
    public void thatNotLoginWithGoogleProviderWithoutGivenToken() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        givenAGoogleAccount();
        CallbackV2<IdentifiedUser> callback = givenAHaloSocialProfileIdentifiedCallback(mCallbackFlag, "account@mobgen.com");
        mHaloAuthApi.loginWithSocial(HaloAuthApi.SOCIAL_GOOGLE_PLUS, callback);
        assertThat(mCallbackFlag.isFlagged()).isFalse();
    }

    @Test
    public void thatLoginWithFacebookProviderWithAGivenToken() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        givenAFacebookAccount();
        CallbackV2<IdentifiedUser> callback = givenAHaloSocialProfileIdentifiedCallback(mCallbackFlag, "account@mobgen.com");
        SocialProvider facebookSocialProvider = getSocialProvider(mHaloAuthApi, HaloAuthApi.SOCIAL_FACEBOOK);
        setFacebookSocialProviderToken((FacebookSocialProvider) facebookSocialProvider);
        mHaloAuthApi.loginWithSocial(HaloAuthApi.SOCIAL_FACEBOOK, callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }


    @Test
    public void thatLoginWithGoogleProviderWithAGivenToken() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        givenAGoogleAccount();
        CallbackV2<IdentifiedUser> callback = givenAHaloSocialProfileIdentifiedCallback(mCallbackFlag, "account@mobgen.com");
        SocialProvider googleSocialProvider = getSocialProvider(mHaloAuthApi, HaloAuthApi.SOCIAL_GOOGLE_PLUS);
        setGooglesSocialProviderToken((GoogleSocialProvider) googleSocialProvider);
        mHaloAuthApi.loginWithSocial(HaloAuthApi.SOCIAL_GOOGLE_PLUS, callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatRegisterWithHaloProvider() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer, REGISTER_SUCESS);
        CallbackV2<HaloUserProfile> callback = givenAHaloSocialProfileRegisteredCallback(mCallbackFlag, "account@mobgen.com");
        HaloAuthProfile authProfile = new HaloAuthProfile("account@mobgen.com", "securepass");
        HaloUserProfile userProfile = new HaloUserProfile(null, "name surname", "name", "surname", "", "account@mobgen.com");
        mHaloAuthApi.register(authProfile, userProfile)
                .threadPolicy(Threading.SAME_THREAD_POLICY)
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatSocialNetworkExistOnProviders() {
        assertThat(mHaloAuthApi.isSocialNetworkAvailable(HaloAuthApi.SOCIAL_HALO)).isTrue();
    }

    @Test
    public void thatReleaseResources() {
        mHaloAuthApi.release();
        assertThat(mHaloAuthApi.isSocialNetworkAvailable(HaloAuthApi.SOCIAL_HALO)).isFalse();
    }

    //instruments
    private static Account givenAHaloAccount() {
        String ACCOUNT_TYPE = "halo.account.manager";
        Account account = new Account("account@mobgen.com", ACCOUNT_TYPE);
        AccountManager accountManager = AccountManager.get(mHalo.context());
        shadowOf(accountManager).addAccount(account);
        accountManager.setPassword(account, "thisisaveryissecurepass");
        accountManager.setUserData(account, AccountManagerHelper.HALO_AUTH_PROVIDER, "thisisaverylongtoken");
        accountManager.setUserData(account, "token_provider_type", AccountManagerHelper.HALO_AUTH_PROVIDER);
        return account;
    }

    private static void givenAGoogleAccount() {
        String ACCOUNT_TYPE = "halo.account.manager";
        Account account = new Account("account@mobgen.com", ACCOUNT_TYPE);
        AccountManager accountManager = AccountManager.get(mHalo.context());
        shadowOf(accountManager).addAccount(account);
        accountManager.setAuthToken(account, AccountManagerHelper.GOOGLE_AUTH_PROVIDER, "thisisthetoken");
        accountManager.setUserData(account, AccountManagerHelper.GOOGLE_AUTH_PROVIDER, "thisisaverylongtoken");
        accountManager.setUserData(account, "token_provider_type", AccountManagerHelper.GOOGLE_AUTH_PROVIDER);
    }

    private static void givenAFacebookAccount() {
        String ACCOUNT_TYPE = "halo.account.manager";
        Account account = new Account("account@mobgen.com", ACCOUNT_TYPE);
        AccountManager accountManager = AccountManager.get(mHalo.context());
        shadowOf(accountManager).addAccount(account);
        accountManager.setAuthToken(account, AccountManagerHelper.FACEBOOK_AUTH_PROVIDER, "thisisthetoken");
        accountManager.setUserData(account, AccountManagerHelper.FACEBOOK_AUTH_PROVIDER, "thisisaverylongtoken");
        accountManager.setUserData(account, "token_provider_type", AccountManagerHelper.FACEBOOK_AUTH_PROVIDER);
    }

    public void givenPermissions() {
        mContext = mock(Context.class);
        when(mContext.checkPermission(eq("AUTHENTICATE_ACCOUNTS"), anyInt(), anyInt()))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mContext.checkPermission(eq("GET_ACCOUNTS"), anyInt(), anyInt()))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mContext.checkPermission(eq("MANAGE_ACCOUNTS"), anyInt(), anyInt()))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
    }
}
