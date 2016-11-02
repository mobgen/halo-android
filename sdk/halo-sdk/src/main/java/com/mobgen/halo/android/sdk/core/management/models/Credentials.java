package com.mobgen.halo.android.sdk.core.management.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * The credentials object that stores the current credentials of the device. It allows the SDK to connect
 * to the API and retrieve the authentication token.
 */
@Keep
public class Credentials implements Parcelable {

    /**
     * Client based login with the client id and client secret.
     */
    @Api(1.0)
    public static final int CLIENT_BASED_LOGIN = 0;

    /**
     * Device based login with the username and password.
     */
    @Api(1.0)
    public static final int USER_BASED_LOGIN = 1;

    /**
     * The client id of the device.
     */
    private String mUsername;

    /**
     * The client secret of the device.
     */
    private String mPassword;

    /**
     * The type of login to use with the sdk.
     */
    @LoginType
    private int mLoginType;

    /**
     * The creator for the parcelable item.
     */
    public static final Parcelable.Creator<Credentials> CREATOR = new Parcelable.Creator<Credentials>() {
        public Credentials createFromParcel(Parcel source) {
            return new Credentials(source);
        }

        public Credentials[] newArray(int size) {
            return new Credentials[size];
        }
    };

    /**
     * Constructor for the credentials.
     *
     * @param username The client id for the credentials.
     * @param password The client secret for the credentials.
     */
    private Credentials(@NonNull String username, @NonNull String password, @LoginType int loginType) {
        mUsername = username;
        mPassword = password;
        mLoginType = loginType;
    }

    /**
     * Parcelable constructor.
     *
     * @param in The parcel item.
     */
    @SuppressWarnings("ResourceType")
    protected Credentials(Parcel in) {
        this.mUsername = in.readString();
        this.mPassword = in.readString();
        this.mLoginType = in.readInt();
    }

    /**
     * Creates the device credentials.
     *
     * @param username The username for the login.
     * @param password The password for the login.
     * @return The credentials created.
     */
    @Api(1.0)
    public static Credentials createUser(@NonNull String username, String password) {
        return new Credentials(username, password, USER_BASED_LOGIN);
    }

    /**
     * Creates the client credentials.
     *
     * @param clientId     The client id.
     * @param clientSecret The client secret.
     * @return The credentials created.
     */
    @Api(1.0)
    public static Credentials createClient(@NonNull String clientId, String clientSecret) {
        return new Credentials(clientId, clientSecret, CLIENT_BASED_LOGIN);
    }

    /**
     * Provides the client secret of the device.
     *
     * @return The client secret.
     */
    @Api(1.0)
    @NonNull
    public String getPassword() {
        return mPassword;
    }

    /**
     * Provides the client id of the device.
     *
     * @return The client id of the device.
     */
    @Api(1.0)
    @NonNull
    public String getUsername() {
        return mUsername;
    }

    /**
     * Provides the login type.
     *
     * @return The login type.
     */
    @Api(1.0)
    @LoginType
    public int getLoginType() {
        return mLoginType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUsername);
        dest.writeString(this.mPassword);
        dest.writeInt(this.mLoginType);
    }

    /**
     * The login type annotation so you can setup a login based on device and password or
     * a login based on app authentication.
     */
    @IntDef({CLIENT_BASED_LOGIN, USER_BASED_LOGIN})
    @Retention(RetentionPolicy.SOURCE)
    public @interface LoginType {
    }
}
