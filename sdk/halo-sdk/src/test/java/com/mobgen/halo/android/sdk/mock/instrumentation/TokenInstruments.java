package com.mobgen.halo.android.sdk.mock.instrumentation;


import com.mobgen.halo.android.sdk.core.management.models.Token;

public class TokenInstruments {


    public static Token givenARefreshToken() {
        return new Token("access", "refresh", 10000L, "bearer");
    }

    public static Token givenAExpiredToken() {
        return new Token("expired", "expiredRefresh", -1L, "bearer");
    }

    public static Token givenAToken() {
        return new Token("access", "refresh", 0L, "type");
    }

    public static Token givenACustomTypeToken() {
        return new Token("token", "refresh", 10L, "myType");
    }
}
