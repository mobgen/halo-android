package com.mobgen.halo.android.sdk.core.management.authentication;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;
import com.mobgen.halo.android.sdk.core.management.models.Token;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * Provides the token data.
 */
public class RequestTokenInteractor implements HaloInteractorExecutor.Interactor<Token> {

    /**
     * The token repository.
     */
    private TokenRepository mTokenRepository;

    /**
     * Request token credentials.
     */
    private Credentials mCredentials;

    /**
     * Constructor of the token provider.
     *
     * @param tokenRepository The token repository.
     */
    public RequestTokenInteractor(@NonNull TokenRepository tokenRepository, @NonNull Credentials credentials) {
        mTokenRepository = tokenRepository;
        mCredentials = credentials;
    }

    @NonNull
    @Override
    public HaloResultV2<Token> executeInteractor() throws Exception {
        return mTokenRepository.requestToken(mCredentials);
    }
}
