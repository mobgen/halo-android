package com.mobgen.halo.android.social.social;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.HaloSocialApi;
import com.mobgen.halo.android.social.authenticator.AccountManagerHelper;
import com.mobgen.halo.android.social.mock.instrumentation.HaloManagerApiShadow;
import com.mobgen.halo.android.social.mock.instrumentation.StringShadowResources;
import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.social.models.HaloUserProfile;
import com.mobgen.halo.android.social.models.IdentifiedUser;
import com.mobgen.halo.android.social.providers.SocialNotAvailableException;
import com.mobgen.halo.android.social.providers.SocialProvider;
import com.mobgen.halo.android.social.providers.facebook.FacebookSocialProvider;
import com.mobgen.halo.android.social.providers.google.GoogleSocialProvider;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.junit.Test;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static com.mobgen.halo.android.social.mock.fixtures.ServerFixtures.LOGIN_SUCESS;
import static com.mobgen.halo.android.social.mock.fixtures.ServerFixtures.REGISTER_SUCESS;
import static com.mobgen.halo.android.social.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.social.mock.instrumentation.HaloMock.givenADefaultHalo;
import static com.mobgen.halo.android.social.mock.instrumentation.HaloSocialApiInstrument.givenAHaloSocialProfileIdentifiedCallback;
import static com.mobgen.halo.android.social.mock.instrumentation.HaloSocialApiInstrument.givenAHaloSocialProfileRegisteredCallback;
import static com.mobgen.halo.android.social.mock.instrumentation.HaloSocialApiMock.givenASocialApiWithAllNetworksAvailable;
import static com.mobgen.halo.android.social.mock.instrumentation.HaloSocialApiInstrument.getSocialProvider;
import static com.mobgen.halo.android.social.mock.instrumentation.HaloSocialApiInstrument.setFacebookSocialProviderToken;
import static com.mobgen.halo.android.social.mock.instrumentation.HaloSocialApiInstrument.setGooglesSocialProviderToken;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;


@Config(shadows = {StringShadowResources.class, HaloManagerApiShadow.class})
public class HaloSocialApiTest extends HaloRobolectricTest {

    private MockServer mMockServer;
    private static Halo mHalo;
    private HaloSocialApi mHaloSocialApi;
    private CallbackFlag mCallbackFlag;
    private Context mContext;

    @Override
    public void onStart() throws IOException, HaloParsingException {
        mMockServer = MockServer.create();
        mHalo = givenADefaultHalo(mMockServer.start());
        mHaloSocialApi = givenASocialApiWithAllNetworksAvailable(mHalo);
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
        assertThat(mHaloSocialApi).isNotNull();
    }

    @Test
    public void thatRecoverySessionWithHaloProvider() throws SecurityException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        givenAHaloAccount();
        mHalo.getCore().haloSocialRecover().recoverAccount();
        assertThat(mHaloSocialApi.isSocialNetworkAvailable(HaloSocialApi.SOCIAL_HALO)).isTrue();
    }

    @Test
    public void thatRecoverySessionWithFacebookProvider() throws SecurityException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        givenAFacebookAccount();
        mHalo.getCore().haloSocialRecover().recoverAccount();
        assertThat(mHaloSocialApi.isSocialNetworkAvailable(HaloSocialApi.SOCIAL_FACEBOOK)).isTrue();
    }

    @Test
    public void thatRecoverySessionWithGoogleProvider() throws SecurityException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        givenAGoogleAccount();
        mHalo.getCore().haloSocialRecover().recoverAccount();
        assertThat(mHaloSocialApi.isSocialNetworkAvailable(HaloSocialApi.SOCIAL_GOOGLE_PLUS)).isTrue();
    }

    @Test
    public void thatLoginWithHaloProvider() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        CallbackV2<IdentifiedUser> callback = givenAHaloSocialProfileIdentifiedCallback(mCallbackFlag, "account@mobgen.com");
        HaloAuthProfile authProfile = new HaloAuthProfile("account@mobgen.com", "securepass");
        mHaloSocialApi.loginWithHalo(HaloSocialApi.SOCIAL_HALO, authProfile, callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }


    @Test
    public void thatNotLoginWithFacebookProviderWithoutGivenToken() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        givenAFacebookAccount();
        CallbackV2<IdentifiedUser> callback = givenAHaloSocialProfileIdentifiedCallback(mCallbackFlag, "account@mobgen.com");
        mHaloSocialApi.loginWithSocial(HaloSocialApi.SOCIAL_FACEBOOK, callback);
        assertThat(mCallbackFlag.isFlagged()).isFalse();
    }


    @Test
    public void thatNotLoginWithGoogleProviderWithoutGivenToken() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        givenAGoogleAccount();
        CallbackV2<IdentifiedUser> callback = givenAHaloSocialProfileIdentifiedCallback(mCallbackFlag, "account@mobgen.com");
        mHaloSocialApi.loginWithSocial(HaloSocialApi.SOCIAL_GOOGLE_PLUS, callback);
        assertThat(mCallbackFlag.isFlagged()).isFalse();
    }

    @Test
    public void thatLoginWithFacebookProviderWithAGivenToken() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        givenAFacebookAccount();
        CallbackV2<IdentifiedUser> callback = givenAHaloSocialProfileIdentifiedCallback(mCallbackFlag, "account@mobgen.com");
        SocialProvider facebookSocialProvider = getSocialProvider(mHaloSocialApi, HaloSocialApi.SOCIAL_FACEBOOK);
        setFacebookSocialProviderToken((FacebookSocialProvider) facebookSocialProvider);
        mHaloSocialApi.loginWithSocial(HaloSocialApi.SOCIAL_FACEBOOK, callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }


    @Test
    public void thatLoginWithGoogleProviderWithAGivenToken() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer, LOGIN_SUCESS);
        givenAGoogleAccount();
        CallbackV2<IdentifiedUser> callback = givenAHaloSocialProfileIdentifiedCallback(mCallbackFlag, "account@mobgen.com");
        SocialProvider googleSocialProvider = getSocialProvider(mHaloSocialApi, HaloSocialApi.SOCIAL_GOOGLE_PLUS);
        setGooglesSocialProviderToken((GoogleSocialProvider) googleSocialProvider);
        mHaloSocialApi.loginWithSocial(HaloSocialApi.SOCIAL_GOOGLE_PLUS, callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatRegisterWithHaloProvider() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer, REGISTER_SUCESS);
        CallbackV2<HaloUserProfile> callback = givenAHaloSocialProfileRegisteredCallback(mCallbackFlag, "account@mobgen.com");
        HaloAuthProfile authProfile = new HaloAuthProfile("account@mobgen.com", "securepass");
        HaloUserProfile userProfile = new HaloUserProfile(null, "name surname", "name", "surname", "", "account@mobgen.com");
        mHaloSocialApi.register(authProfile, userProfile)
                .threadPolicy(Threading.SAME_THREAD_POLICY)
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatRecoverProfilePolicy() {
        assertThat(mHaloSocialApi.recoveryPolicy()).isEqualTo(HaloSocialApi.RECOVERY_ALWAYS);
    }

    @Test
    public void thatSocialNetworkExistOnProviders() {
        assertThat(mHaloSocialApi.isSocialNetworkAvailable(HaloSocialApi.SOCIAL_HALO)).isTrue();
    }

    @Test
    public void thatReleaseResources() {
        mHaloSocialApi.release();
        assertThat(mHaloSocialApi.isSocialNetworkAvailable(HaloSocialApi.SOCIAL_HALO)).isFalse();
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
