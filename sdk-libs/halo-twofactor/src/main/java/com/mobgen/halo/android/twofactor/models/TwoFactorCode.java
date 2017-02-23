package com.mobgen.halo.android.twofactor.models;

/**
 * Model with the information of a two factor authentication code.
 */
public class TwoFactorCode {

    String mCode;

    String mIssuer;

    public TwoFactorCode(String code, String issuer){
        mCode = code;
        mIssuer = issuer;
    }

    public String getCode(){
        return mCode;
    }

    public String getIssuer(){
        return mIssuer;
    }

}
