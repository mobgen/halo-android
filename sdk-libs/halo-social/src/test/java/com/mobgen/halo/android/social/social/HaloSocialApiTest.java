package com.mobgen.halo.android.social.social;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.pm.PackageManager;

import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.device.DeviceLocalDatasource;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.social.HaloSocialApi;
import com.mobgen.halo.android.social.authenticator.AccountManagerHelper;
import com.mobgen.halo.android.social.authenticator.AuthTokenType;
import com.mobgen.halo.android.social.mock.instrumentation.StringShadowResources;
import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.social.models.HaloSocialProfile;
import com.mobgen.halo.android.social.models.HaloUserProfile;
import com.mobgen.halo.android.social.models.IdentifiedUser;
import com.mobgen.halo.android.social.providers.SocialNotAvailableException;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.junit.Test;
import org.robolectric.annotation.Config;

import java.io.IOException;

import static com.mobgen.halo.android.social.mock.fixtures.ServerFixtures.LOGIN_SUCESS;
import static com.mobgen.halo.android.social.mock.fixtures.ServerFixtures.REGISTER_SUCESS;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static com.mobgen.halo.android.social.mock.instrumentation.HaloMock.givenADefaultHalo;
import static com.mobgen.halo.android.social.mock.instrumentation.HaloSocialApiMock.givenASocialApiWithAllNetworksAvailable;
import static com.mobgen.halo.android.social.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.social.mock.instrumentation.HaloSocialApiInstrument.givenAHaloSocialProfileCallback;
import static com.mobgen.halo.android.social.mock.instrumentation.HaloSocialApiInstrument.givenAHaloSocialProfileRegisteredCallback;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;


@Config(shadows = StringShadowResources.class)
public class HaloSocialApiTest extends HaloRobolectricTest {

    private MockServer mMockServer;
    private Halo mHalo;
    private HaloSocialApi mHaloSocialApi;
    private CallbackFlag mCallbackFlag;
    private Context mContext;

    @Override
    public void onStart() throws IOException, HaloParsingException {
        mMockServer = MockServer.create();
        mHalo = givenADefaultHalo(mMockServer.start());
        givenAValidDevice();
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
    public void thatCanCreateAHaloSocialApiAttachedToHalo(){
        assertThat(mHaloSocialApi).isNotNull();
    }

    @Test
    public void thatRecoverySessionWithHaloProvider() throws SecurityException, IOException {
        enqueueServerFile(mMockServer,LOGIN_SUCESS);
        givenAHaloAccount();
        mHaloSocialApi.recoverLogin();
        assertThat(mHaloSocialApi.isSocialNetworkAvailable(HaloSocialApi.SOCIAL_HALO)).isTrue();
    }

    @Test
    public void thatRecoverySessionWithFacebookProvider() throws SecurityException, IOException{
        enqueueServerFile(mMockServer,LOGIN_SUCESS);
        givenAFacebookAccount();
        mHaloSocialApi.recoverLogin();
        assertThat(mHaloSocialApi.isSocialNetworkAvailable(HaloSocialApi.SOCIAL_FACEBOOK)).isTrue();
    }

    @Test
    public void thatRecovrySessionWithGoogleProvider() throws SecurityException, IOException{
        enqueueServerFile(mMockServer,LOGIN_SUCESS);
        givenAGoogleAccount();
        mHaloSocialApi.recoverLogin();
        assertThat(mHaloSocialApi.isSocialNetworkAvailable(HaloSocialApi.SOCIAL_GOOGLE_PLUS)).isTrue();
    }

    @Test
    public void thatAuthenticateWithHaloProvider() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer,LOGIN_SUCESS);
        CallbackV2<IdentifiedUser> callback = givenAHaloSocialProfileCallback(mCallbackFlag,"account@mobgen.com");
        mHaloSocialApi.loginWithHalo("account@mobgen.com", "mypass")
                .threadPolicy(Threading.SAME_THREAD_POLICY)
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatLoginWithHaloProvider() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer,LOGIN_SUCESS);
        CallbackV2<HaloSocialProfile> callback = givenAHaloSocialProfileRegisteredCallback(mCallbackFlag,"account@mobgen.com");
        HaloAuthProfile authProfile = new HaloAuthProfile("account@mobgen.com", "securepass", "myalias");
        mHaloSocialApi.login(HaloSocialApi.SOCIAL_HALO,authProfile,callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }


    @Test
    public void thatLoginWithFacebookProvider() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer,LOGIN_SUCESS);
        givenAFacebookAccount();
        CallbackV2<HaloSocialProfile> callback = givenAHaloSocialProfileRegisteredCallback(mCallbackFlag,"account@mobgen.com");
        mHaloSocialApi.login(HaloSocialApi.SOCIAL_FACEBOOK,callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }


    @Test
    public void thatLoginWithGoogleProvider() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer,LOGIN_SUCESS);
        givenAGoogleAccount();
        CallbackV2<HaloSocialProfile> callback = givenAHaloSocialProfileRegisteredCallback(mCallbackFlag,"account@mobgen.com");
        mHaloSocialApi.login(HaloSocialApi.SOCIAL_GOOGLE_PLUS,callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatAuthenticateWithSocialProvider() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer,LOGIN_SUCESS);
        CallbackV2<IdentifiedUser> callback = givenAHaloSocialProfileCallback(mCallbackFlag,"account@mobgen.com");
        mHaloSocialApi.loginWithANetwork("facebook","mytoke")
                .threadPolicy(Threading.SAME_THREAD_POLICY)
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatRegisterWithHaloProvider() throws SocialNotAvailableException, IOException {
        enqueueServerFile(mMockServer,REGISTER_SUCESS);
        CallbackV2<HaloSocialProfile> callback = givenAHaloSocialProfileRegisteredCallback(mCallbackFlag,"account@mobgen.com");
        HaloAuthProfile authProfile = new HaloAuthProfile("account@mobgen.com", "securepass", "myalias");
        HaloUserProfile userProfile = new HaloUserProfile(null, "name surname","name","surname","","account@mobgen.com");
        mHaloSocialApi.register(authProfile,userProfile)
                .threadPolicy(Threading.SAME_THREAD_POLICY)
                .execute(callback);
        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatRecoverProfilePolicy(){
        assertThat(mHaloSocialApi.getRecoveryPolicy()).isEqualTo(HaloSocialApi.RECOVERY_ALWAYS);
    }

    @Test
    public void thatSocialNetworkExistOnProviders(){
        assertThat(mHaloSocialApi.isSocialNetworkAvailable(HaloSocialApi.SOCIAL_HALO)).isTrue();
    }

    @Test
    public void thatReleaseResources(){
        mHaloSocialApi.release();
        assertThat(mHaloSocialApi.isSocialNetworkAvailable(HaloSocialApi.SOCIAL_HALO)).isFalse();
    }

    @Test
    public void thatGetCurrentDeviceAlias(){
        assertThat(mHaloSocialApi.getCurrentAlias()).isEqualTo("myTestUser");
    }

    @Test
    public void thatCanGetHaloAccessToken(){
        AccountManagerHelper accountManagerHelper = new AccountManagerHelper(mContext);
        String haloAccessToken = accountManagerHelper.getHaloAccessToken(givenAHaloAccount());
        assertThat(haloAccessToken).isNotNull();
    }

    private Account givenAHaloAccount() {
        String ACCOUNT_TYPE = "halo.account.manager";
        Account account = new Account("account@mobgen.com", ACCOUNT_TYPE);
        AccountManager accountManager = AccountManager.get(mHalo.context());
        shadowOf(accountManager).addAccount(account);
        accountManager.setPassword(account,"thisisaveryissecurepass");
        accountManager.setUserData(account,AuthTokenType.HALO_AUTH_TOKEN,"thisisaverylongtoken");
        accountManager.setUserData(account,"token_type", AuthTokenType.HALO_AUTH_TOKEN);
        return account;
    }

    private void givenAGoogleAccount() {
        String ACCOUNT_TYPE = "halo.account.manager";
        Account account = new Account("account@mobgen.com", ACCOUNT_TYPE);
        AccountManager accountManager = AccountManager.get(mHalo.context());
        shadowOf(accountManager).addAccount(account);
        accountManager.setAuthToken(account,AuthTokenType.GOOGLE_AUTH_TOKEN,"thisisthetoken");
        accountManager.setUserData(account,AuthTokenType.GOOGLE_AUTH_TOKEN,"thisisaverylongtoken");
        accountManager.setUserData(account,"token_type", AuthTokenType.GOOGLE_AUTH_TOKEN);
    }

    private void givenAFacebookAccount() {
        String ACCOUNT_TYPE = "halo.account.manager";
        Account account = new Account("account@mobgen.com", ACCOUNT_TYPE);
        AccountManager accountManager = AccountManager.get(mHalo.context());
        shadowOf(accountManager).addAccount(account);
        accountManager.setAuthToken(account,AuthTokenType.FACEBOOK_AUTH_TOKEN,"thisisthetoken");
        accountManager.setUserData(account,AuthTokenType.FACEBOOK_AUTH_TOKEN,"thisisaverylongtoken");
        accountManager.setUserData(account,"token_type", AuthTokenType.FACEBOOK_AUTH_TOKEN);
    }

    private void givenAValidDevice() throws HaloParsingException {
        Device device = new Device("myTestUser","57fb592ff53f3f002aa99d78",null,null);
        DeviceLocalDatasource deviceLocalDatasource = new DeviceLocalDatasource(mHalo.getCore().manager().storage());
        deviceLocalDatasource.clearCurrentDevice();
        deviceLocalDatasource.cacheDevice(Device.serialize(device, mHalo.getCore().framework().parser()));
    }

    public void givenPermissions(){
        mContext = mock(Context.class);
        when(mContext.checkPermission(eq("AUTHENTICATE_ACCOUNTS"),anyInt(),anyInt()))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mContext.checkPermission(eq("GET_ACCOUNTS"),anyInt(),anyInt()))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mContext.checkPermission(eq("MANAGE_ACCOUNTS"),anyInt(),anyInt()))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
        when(mContext.checkPermission(eq("WRITE_SYNC_SETTINGS"),anyInt(),anyInt()))
                .thenReturn(PackageManager.PERMISSION_GRANTED);
    }
}
