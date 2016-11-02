package com.mobgen.halo.android.sdk.core.management.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;

import java.util.Date;

/**
 * Token used for the OAuth protocol in HALO.
 */
@Keep
@JsonObject
public class Token implements Parcelable {

    /**
     * The creator.
     */
    public static final Creator<Token> CREATOR = new Creator<Token>() {
        public Token createFromParcel(Parcel source) {
            return new Token(source);
        }

        public Token[] newArray(int size) {
            return new Token[size];
        }
    };
    /**
     * The access token.
     */
    @JsonField(name = "access_token")
    String mAccessToken;
    /**
     * The refresh token.
     */
    @JsonField(name = "refresh_token")
    String mRefreshToken;
    /**
     * Expires in time in milliseconds.
     */
    @JsonField(name = "expires_in")
    Long mExpiresIn;
    /**
     * The token type.
     */
    @JsonField(name = "token_type")
    String mTokenType;
    /**
     * The date on which the token was received.
     */
    Date mTokenReceivedDate;

    /**
     * Constructor of the OAuth token. This constructor should be only used by Gson while parsing.
     */
    public Token() {
        mTokenReceivedDate = new Date();
    }

    /**
     * Constructor with all the parameters to build a token.
     *
     * @param accessToken  The access token.
     * @param refreshToken The refresh token that should be used to refresh this token.
     * @param expiresIn    Time expressing when the token will be considered as expired.
     * @param tokenType    The token type, typically bearer.
     */
    @Api(1.0)
    public Token(@NonNull String accessToken, @NonNull String refreshToken, @NonNull Long expiresIn, @NonNull String tokenType) {
        this();
        mAccessToken = accessToken;
        mRefreshToken = refreshToken;
        mExpiresIn = expiresIn;
        mTokenType = tokenType;
    }

    /**
     * Parcel for the token.
     *
     * @param in The parcel where we will write the token.
     */
    protected Token(Parcel in) {
        this.mAccessToken = in.readString();
        this.mRefreshToken = in.readString();
        this.mExpiresIn = (Long) in.readValue(Long.class.getClassLoader());
        this.mTokenType = in.readString();
        long tmpMTokenReceivedDate = in.readLong();
        this.mTokenReceivedDate = new Date(tmpMTokenReceivedDate);
    }

    /**
     * Provides the access token.
     *
     * @return The access token.
     */
    @Api(1.0)
    @NonNull
    public String getAccessToken() {
        return mAccessToken;
    }

    /**
     * Provides the refresh token.
     *
     * @return The refresh token.
     */
    @Api(1.0)
    @NonNull
    public String getRefreshToken() {
        return mRefreshToken;
    }

    /**
     * Expires in time.
     *
     * @return The time that remains to expire.
     */
    @Api(1.0)
    @NonNull
    public Long getExpiresIn() {
        return mExpiresIn;
    }

    /**
     * The token type.
     *
     * @return The token type.
     */
    @Api(1.0)
    @NonNull
    public String getTokenType() {
        return mTokenType;
    }

    /**
     * Provides the received date.
     *
     * @return The date when the token was received.
     */
    @Api(1.0)
    @NonNull
    public Date getReceivedDate() {
        return mTokenReceivedDate;
    }

    /**
     * Provides the value of the authorization header.
     *
     * @return The value of the authorization header.
     */
    @Api(1.0)
    @NonNull
    public String getAuthorization() {
        return mTokenType + " " + mAccessToken;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mAccessToken);
        dest.writeString(this.mRefreshToken);
        dest.writeValue(this.mExpiresIn);
        dest.writeString(this.mTokenType);
        dest.writeLong(mTokenReceivedDate.getTime());
    }
}
