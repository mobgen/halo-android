package com.mobgen.halo.android.social;

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
import com.mobgen.halo.android.framework.sync.callbacks.Callback;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloPluginApi;
import com.mobgen.halo.android.social.facebook.FacebookSocialProvider;
import com.mobgen.halo.android.social.google.GoogleSocialProvider;

/**
 * Plugin for the social network handling and login with different profiles of app +.
 * It allows to use the halo login, facebook, google plus or another declared provider.
 */
public class HaloSocialApi extends HaloPluginApi {

    /**
     * Identifier for social login with google plus.
     */
    public static final int SOCIAL_GOOGLE_PLUS = 1;
    /**
     * Identifier for social login with facebook.
     */
    public static final int SOCIAL_FACEBOOK = 2;

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
        mProviders = new SparseArray<>(2);
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
     * Tries to login with a social network based on the id of this social network.
     *
     * @param socialNetwork The social network to login with.
     * @param callback      The callback.
     * @throws SocialNotAvailableException Server not available.
     */
    @Api(2.0)
    public void login(int socialNetwork, @NonNull Callback<HaloSocialProfile, Void> callback) throws SocialNotAvailableException {
        AssertionUtils.notNull(callback, "callback");
        if (!isSocialNetworkAvailable(socialNetwork)) {
            throw new SocialNotAvailableException("The social network you are trying to log with is not available. Social network id: " + socialNetwork);
        }
        mProviders.get(socialNetwork).authenticate(halo(), callback);
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
        private final HaloSocialApi mSocialApi;

        /**
         * The social api builder.
         *
         * @param halo The halo builder.
         */
        private Builder(@NonNull Halo halo) {
            mSocialApi = new HaloSocialApi(halo);
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
