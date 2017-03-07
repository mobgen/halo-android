package com.mobgen.halo.android.twofactor.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.twofactor.HaloTwoFactorApi;

/**
 * Model with the information of a two factor authentication code.
 */
@Keep
public class TwoFactorCode implements Parcelable {
    /**
     * The two factor authentication code.
     */
    private String mCode;

    /**
     * The provider name of the servide.
     */
    private String mIssuer;


    /**
     * Constructor for the two factor code.
     * @param code
     * @param issuer
     */
    @Keep
    @Api(2.3)
    public TwoFactorCode(@NonNull String code,@NonNull String issuer){
        mCode = code;
        mIssuer = issuer;
    }

    /**
     * Get the code of the two factor.
     *
     * @return The code of the two factor authentication.
     */
    @Keep
    @NonNull
    @Api(2.3)
    public String getCode(){
        return mCode;
    }

    /**
     * Get the name of the provider of the service.
     * @return The name of the provider.
     */
    @Keep
    @NonNull
    @Api(2.3)
    public @HaloTwoFactorApi.IssuerType String getIssuer(){
        return mIssuer;
    }

    @Keep
    public static final Creator<TwoFactorCode> CREATOR = new Creator<TwoFactorCode>() {
        @Override
        public TwoFactorCode createFromParcel(Parcel in) {
            return new TwoFactorCode(in);
        }

        @Override
        public TwoFactorCode[] newArray(int size) {
            return new TwoFactorCode[size];
        }
    };

    protected TwoFactorCode(Parcel in) {
        this.mCode = in.readString();
        this.mIssuer = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mCode);
        dest.writeString(this.mIssuer);
    }

    @Override
    public String toString() {
        return "TwoFactorCode{" +
                "mCode='" + mCode + '\'' +
                ", mIssuer='" + mIssuer + '\'' +
                '}';
    }
}
