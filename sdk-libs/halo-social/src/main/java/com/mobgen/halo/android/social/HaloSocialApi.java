package com.mobgen.halo.android.social;

import android.accounts.Account;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.SparseArray;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloConfigurationException;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloPluginApi;
import com.mobgen.halo.android.sdk.core.internal.storage.HaloManagerContract;
import com.mobgen.halo.android.sdk.core.management.authentication.HaloSocialAuthenticator;
import com.mobgen.halo.android.sdk.core.management.device.DeviceLocalDatasource;
import com.mobgen.halo.android.sdk.core.management.device.DeviceRemoteDatasource;
import com.mobgen.halo.android.sdk.core.management.device.DeviceRepository;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;
import com.mobgen.halo.android.social.authenticator.AccountManagerHelper;
import com.mobgen.halo.android.social.authenticator.AuthTokenType;
import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.social.models.HaloSocialProfile;
import com.mobgen.halo.android.social.models.HaloUserProfile;
import com.mobgen.halo.android.social.providers.SocialNotAvailableException;
import com.mobgen.halo.android.social.providers.SocialProvider;
import com.mobgen.halo.android.social.providers.facebook.FacebookSocialProvider;
import com.mobgen.halo.android.social.providers.google.GoogleSocialProvider;
import com.mobgen.halo.android.social.login.LoginInteractor;
import com.mobgen.halo.android.social.login.SocialLoginInteractor;
import com.mobgen.halo.android.social.login.LoginRemoteDatasource;
import com.mobgen.halo.android.social.login.LoginRepository;
import com.mobgen.halo.android.social.models.IdentifiedUser;
import com.mobgen.halo.android.social.providers.halo.HaloSocialProvider;
import com.mobgen.halo.android.social.register.RegisterInteractor;
import com.mobgen.halo.android.social.register.RegisterRemoteDatasource;
import com.mobgen.halo.android.social.register.RegisterRepository;

/**
 * Plugin for the social network handling and login with different profiles of app +.
 * It allows to use the halo login, facebook, google plus or another declared provider.
 */
public class HaloSocialApi extends HaloPluginApi implements HaloSocialAuthenticator<HaloSocialProfile,HaloAuthProfile,HaloUserProfile,SocialNotAvailableException> {
    /**
     * Identifier for social login with halo.
     */
    public static final int SOCIAL_HALO = 0;
    /**
     * Identifier for social login with google plus.
     */
    public static final int SOCIAL_GOOGLE_PLUS = 1;
    /**
     * Identifier for social login with facebook.
     */
    public static final int SOCIAL_FACEBOOK = 2;
    /**
     * Policy to recover credentials from account manager
     */
    public static final int RECOVERY_NEVER = 0;
    /**
     * Policy to recover credentials from account manager
     */
    public static final int RECOVERY_ALWAYS = 1;

    /**
     * Possible errors that can happen when using a provider for loging in.
     */
    public class Error {
        /**
         * The user has no internet.
         */
        public static final int CODE_NO_INTERNET = 1;
        /**
         * There was an error related to the social network. This usually means
         * you have to perform some actions to get that social network to work, like
         * adding some credentials or registering an api key.
         */
        public static final int CODE_PROVIDER_ERROR = 2;
    }

    /**
     * The device info.
     */
    @Nullable
    private Device mDevice;
    /**
     * The Device repository to fetch a device.
     */
    private DeviceRepository mDeviceRepository;
    /**
     * The Account manager helper
     */
    private AccountManagerHelper mAccountManagerHelper;
    /**
     * The account type on account manager
     */
    @Nullable
    private  String mAccountType;
    /**
     * The recovery policy
     */
    private int mRecoveryPolicy = HaloSocialApi.RECOVERY_NEVER;

    /**
     * The map of providers.
     */
    private SparseArray<SocialProvider> mProviders;

    /**
     * Constructor that accepts halo.
     *
     * @param halo The halo instance.
     */
    private HaloSocialApi(@NonNull Halo halo) {
        super(halo);
        mProviders = new SparseArray<>(3);
        mAccountManagerHelper =  new AccountManagerHelper(context());
        mDeviceRepository = new DeviceRepository(framework().parser(), new DeviceRemoteDatasource(framework().network()), new DeviceLocalDatasource(framework().storage(HaloManagerContract.HALO_MANAGER_STORAGE)));
        mDevice =  mDeviceRepository.getCachedDevice();
        halo.manager().haloSocial(this);
    }

    /**
     * Creates the social api for authentications.
     *
     * @param halo The halo instance.
     * @return The social api instance.
     */
    public static Builder with(@NonNull Halo halo) {
        return new Builder(halo);
    }

    /**
     * Provide the device alias from device repository
     *
     * @return String The device alias.
     *
     */
    public String getCurrentAlias() {
        return mDevice.getAlias();
    }

    @Override
    public void recoverLogin() {
        if(mRecoveryPolicy == HaloSocialApi.RECOVERY_ALWAYS && mDevice!=null) {
            Account account = mAccountManagerHelper.recoverAccount(mAccountType);
            if (account != null) {
                if (mAccountManagerHelper.getAccountTokenType(account).equals(AuthTokenType.HALO_AUTH_TOKEN)) {
                    HaloAuthProfile haloAuthProfile = recoverHaloAuthProfile();
                    mProviders.get(SOCIAL_HALO).setAuthProfile(haloAuthProfile);
                    mProviders.get(SOCIAL_HALO).authenticate(halo(), mAccountType, new CallbackV2<HaloSocialProfile>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<HaloSocialProfile> result) {
                            if(result.status().isOk()) {
                                Halog.v(HaloSocialApi.class,result.data().displayName());
                            }
                        }
                    });
                } else if (mAccountManagerHelper.getAccountTokenType(account).equals(AuthTokenType.GOOGLE_AUTH_TOKEN)) {
                    mProviders.get(SOCIAL_GOOGLE_PLUS).authenticate(halo(), mAccountType, new CallbackV2<HaloSocialProfile>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<HaloSocialProfile> result) {
                            if(result.status().isOk()) {
                                Halog.v(HaloSocialApi.class,result.data().displayName());
                            }
                        }
                    });
                } else if (mAccountManagerHelper.getAccountTokenType(account).equals(AuthTokenType.FACEBOOK_AUTH_TOKEN)) {
                    mProviders.get(SOCIAL_FACEBOOK).authenticate(halo(), mAccountType, new CallbackV2<HaloSocialProfile>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<HaloSocialProfile> result) {
                            if(result.status().isOk()) {
                                Halog.v(HaloSocialApi.class,result.data().displayName());
                            }
                        }
                    });
                }
            }
        }
    }

    @Override
    public void login(int socialNetwork, @NonNull CallbackV2<HaloSocialProfile> callback) throws SocialNotAvailableException {
        AssertionUtils.notNull(callback, "callback");
        if (!isSocialNetworkAvailable(socialNetwork)) {
            throw new SocialNotAvailableException("The social network you are trying to log with is not available. Social network id: " + socialNetwork);
        }
        mProviders.get(socialNetwork).authenticate(halo(),mAccountType, callback);
    }

    @Override
    public void login(int socialNetwork, @NonNull HaloAuthProfile haloAuthProfile, @NonNull CallbackV2<HaloSocialProfile> callback) throws SocialNotAvailableException {
        AssertionUtils.notNull(haloAuthProfile, "haloAuthProfile");
        if (!isSocialNetworkAvailable(socialNetwork)) {
            throw new SocialNotAvailableException("The social network you are trying to log with is not available. Social network id: " + socialNetwork);
        }
        mProviders.get(socialNetwork).setAuthProfile(haloAuthProfile);
        mProviders.get(socialNetwork).authenticate(halo(), mAccountType, callback);
    }


    @Override
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<HaloSocialProfile> register(@NonNull HaloAuthProfile haloAuthProfile, @NonNull HaloUserProfile haloUserProfile) {
        AssertionUtils.notNull(haloAuthProfile, "haloAuthProfile");
        AssertionUtils.notNull(haloUserProfile, "haloUserProfile");
        return new HaloInteractorExecutor<>(halo(),
                "Sign in with halo",
                new RegisterInteractor(new RegisterRepository(new RegisterRemoteDatasource(halo().framework().network())),
                        haloAuthProfile, haloUserProfile)
        );
    }

    /**
     * Tries to login with halo based on the social network token, network type and device alias
     *
     * @param socialNetworkName The social network to login with.
     * @param socialToken The social token
     */
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<IdentifiedUser> loginWithANetwork(@NonNull String socialNetworkName, @NonNull String socialToken) {
        AssertionUtils.notNull(socialToken, "socialToken");
        return new HaloInteractorExecutor<>(halo(),
                "Login with a social provider",
                new SocialLoginInteractor(mAccountType,new LoginRepository(new LoginRemoteDatasource(halo().framework().network())),
                        socialNetworkName, socialToken, mDevice,mRecoveryPolicy)
        );

    }

    /**
     * Tries to login with halo
     *
     * @param username The social network to login with.
     * @param password The social token
     */
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<IdentifiedUser> loginWithHalo(@NonNull String username, @NonNull String password) {
        AssertionUtils.notNull(username, "username");
        AssertionUtils.notNull(password, "password");
        return new HaloInteractorExecutor<>(halo(),
                "Login with halo",
                new LoginInteractor(mAccountType, new LoginRepository(new LoginRemoteDatasource(halo().framework().network())),
                        username, password, mDevice,mRecoveryPolicy)
        );
    }

    /**
     * Tries to recover a halo auth profile for a given account.
     *
     *
     * @return HaloAuthProfile The HaloAuthProfile.
     */
    @Nullable
    private HaloAuthProfile recoverHaloAuthProfile(){
        if(mAccountType!=null) {
            return mAccountManagerHelper.getAuthProfile(mAccountManagerHelper.recoverAccount(mAccountType), mDevice.getAlias());
        }
        return null;
    }

    /**
     * Tries to recover a halo auth token for a given account.
     *
     *
     * @return HaloAuthProfile The HaloAuthProfile.
     */
    @Api(2.0)
    @Nullable
    public String recoverAuthToken(@NonNull String tokenType){
        AssertionUtils.notNull(tokenType, "tokenType");
        if(mAccountType!=null) {
            return mAccountManagerHelper.getAuthToken(mAccountManagerHelper.recoverAccount(mAccountType), tokenType);
        }
        return null;
    }

    /**
     * Checks if the social network with the given id is available.
     *
     * @param socialNetwork The social network.
     * @return True if it is available. False otherwise.
     */
    @Api(2.0)
    public boolean isSocialNetworkAvailable(int socialNetwork) {
        return hasProvider(socialNetwork) && libraryAvailable(socialNetwork) && linkedAppAvailable(socialNetwork);
    }

    /**
     * Releases all the reserved providers.
     */
    @Api(2.0)
    public void release() {
        for (int i = 0; i < mProviders.size(); i++) {
            SocialProvider provider = mProviders.get(i);
            if (provider != null) {
                provider.release();
            }
        }
        mProviders.clear();
    }

    /**
     * Set the account type to save account details on account mananger
     *
     * @param accountType The account type.
     *
     */
    private void setAccountType(@Nullable String accountType) {
        mAccountType = accountType;
    }

    /**
     * Set the recovery policy.
     *
     * @param recoverPolicy The recovery policy
     *
     */
    private void setRecoverPolicy(@Nullable int recoverPolicy) {
        mRecoveryPolicy = recoverPolicy;
    }

    /**
     * Get the recovery policy.
     *
     * @return The recovery policyß
     */
    @Api(2.0)
    public int getRecoveryPolicy() {
        return mRecoveryPolicy;
    }

    /**
     * Registers a new provider so it can be used for authentications.
     *
     * @param socialNetworkId The social network id.
     * @param provider        The provider registered.
     */
    private void registerProvider(int socialNetworkId, @Nullable SocialProvider provider) {
        if (hasProvider(socialNetworkId)) {
            Halog.w(getClass(), "You are overriding an existing social provider. Make sure you have a unique id for it.");
        }
        mProviders.put(socialNetworkId, provider);
    }

    /**
     * Checks if the application to which the network is linked in is available.
     *
     * @param socialNetwork The social network to check.
     * @return Trie if it is available. False otherwise.
     */
    private boolean linkedAppAvailable(int socialNetwork) {
        return mProviders.get(socialNetwork).linkedAppAvailable(context());
    }

    /**
     * Checks if the library is available in the platform.
     *
     * @param socialNetwork The social network to check.
     * @return True if the library is available. False otherwise.
     */
    private boolean libraryAvailable(int socialNetwork) {
        return mProviders.get(socialNetwork).isLibraryAvailable(context());
    }

    /**
     * Checks if the provider is already registered.
     *
     * @param socialNetwork The social network to login with.
     * @return True if the provider is there, false otherwise.
     */
    private boolean hasProvider(int socialNetwork) {
        return mProviders.get(socialNetwork) != null;
    }

    /**
     * The builder for the social api.
     */
    public static class Builder implements IBuilder<HaloSocialApi> {

        /**
         * The social api.
         */
        @NonNull
        private HaloSocialApi mSocialApi;

        /**
         * The social api builder.
         *
         * @param halo The halo builder.
         */
        private Builder(@NonNull final Halo halo) {
            mSocialApi = new HaloSocialApi(halo);
        }

        /**
         * Set the recovery policy.
         *
         * @param recoverPolicy The recovery policy
         *
         * @return The current builder
         */
        @Api(2.0)
        @NonNull
        public Builder recoveryPolicy(@Nullable  int recoverPolicy) {
            mSocialApi.setRecoverPolicy(recoverPolicy);
            return this;
        }

        /**
         * Adds the google provider to the social api login.
         *
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder withGoogle() {
            String clientId = mSocialApi.halo().context().getString(R.string.halo_social_google_client);
            if (TextUtils.isEmpty(clientId)) {
                throw new HaloConfigurationException("You must add in the plugin the id for the social network. halo {\n" +
                        "\t...\n" +
                        "\tsocial {\n" +
                        "\t\tgoogle \"oAuth2 Web client id\"\n" +
                        "\t\t...\n" +
                        "\t}\n" +
                        "\t...\n" +
                        "}");
            }
            GoogleSignInOptions options = new GoogleSignInOptions.Builder()
                    .requestId()
                    .requestIdToken(clientId)
                    .requestEmail()
                    .requestProfile()
                    .build();
            return withProvider(SOCIAL_GOOGLE_PLUS, new GoogleSocialProvider(options));
        }

        /**
         * Set the account type to save account details on account mananger. This is optional and if its null credentials will not be stored.
         * @param accountType The account type.
         *
         * @return The account type.
         */
        @Api(2.0)
        @NonNull
        public Builder storeCredentials(@Nullable  String accountType) {
            mSocialApi.setAccountType(accountType);
            return this;
        }
        /**
         * Adds the halo provider to the social api login.
         *
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder withHalo() {
            return withProvider(SOCIAL_HALO, new HaloSocialProvider());
        }

        /**
         * Adds the facebook provider to the social api login.
         *
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder withFacebook() {
            return withProvider(SOCIAL_FACEBOOK, new FacebookSocialProvider());
        }

        /**
         * Adds a custom provider.
         *
         * @param socialId The social id of the custom provider.
         * @param provider The provider instance.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder withProvider(int socialId, @Nullable SocialProvider provider) {
            mSocialApi.registerProvider(socialId, provider);
            return this;
        }

        @NonNull
        @Override
        public HaloSocialApi build() {
            return mSocialApi;
        }
    }
}
