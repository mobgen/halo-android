package com.mobgen.halo.android.sdk.core.management.authentication;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;
import com.mobgen.halo.android.sdk.core.management.models.Token;

/**
 * The token repository with all the operations related to tokens.
 */
public class TokenRepository {

    /**
     * The remote datasource for the tokens.
     */
    private TokenRemoteDatasource mTokenRemoteDatasource;

    /**
     * The token repository.
     * @param tokenRemoteDatasource The remote datasource.
     */
    public TokenRepository(TokenRemoteDatasource tokenRemoteDatasource) {
        mTokenRemoteDatasource = tokenRemoteDatasource;
    }

    /**
     * The token request.
     * @param credentials The credentials.
     * @return The result generated.
     */
    public HaloResultV2<Token> requestToken(@NonNull Credentials credentials) {
        HaloStatus.Builder status = HaloStatus.builder();
        Token token = null;
        try {
            token = mTokenRemoteDatasource.requestToken(credentials);
        }catch (Exception e){
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), token);
    }
}
