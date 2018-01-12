package com.mobgen.halo.android.sdk.mock.instrumentation;

import com.mobgen.halo.android.sdk.core.management.authentication.TokenRemoteDatasource;

import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by mobgenimac on 11/10/16.
 */

public class HaloAuthenticatorIntrument {

    public static Response givenA200Response() {
        return new Response.Builder()
                .request(new Request.Builder().url("http://google.com").build())
                .protocol(Protocol.HTTP_1_1)
                .message("status_OK")
                .code(200).build();
    }

    public static Response givenA401Response() {
        return new Response.Builder()
                .request(new Request.Builder().url("http://google.com").build())
                .protocol(Protocol.HTTP_1_1)
                .message("status_FAIL")
                .code(401).build();
    }

    public static Response givenA401UserTokenResponse() {
        return new Response.Builder()
                .request(new Request.Builder().url("http://mockurl/" + TokenRemoteDatasource.URL_GET_USER_TOKEN).build())
                .protocol(Protocol.HTTP_1_1)
                .message("status_FAIL")
                .code(401).build();
    }


    public static Response givenA401ClientTokenResponse() {
        return new Response.Builder()
                .request(new Request.Builder().url("http://mockurl/" + TokenRemoteDatasource.URL_GET_CLIENT_TOKEN).build())
                .protocol(Protocol.HTTP_1_1)
                .message("status_FAIL")
                .code(401).build();
    }
}
