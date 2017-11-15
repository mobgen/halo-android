package com.mobgen.halo.android.framework.toolbox.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloIntegrationException;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloAuthenticationException;
import com.mobgen.halo.android.framework.network.exceptions.HaloConnectionException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetParseException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNotFoundException;
import com.mobgen.halo.android.framework.network.exceptions.HaloServerException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageParseException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Data status that is used to determine which is the status of the data provided to the user.
 * It tells if this data is dataLocal, online and if a given request produced some sort of error.
 */
public class HaloStatus implements Parcelable {

    /**
     * ErrorStatus annotation for the state of the result provided.
     */
    @IntDef({
            STATUS_UNKNOWN_ERROR,
            STATUS_OK,
            STATUS_CANCELED,
            STATUS_CONNECTION_ERROR,
            STATUS_INTERNAL_SERVER_ERROR,
            STATUS_SECURITY_ERROR,
            STATUS_NOT_FOUND_ERROR,
            STATUS_GENERAL_STORAGE_ERROR,
            STATUS_NETWORK_PARSE_ERROR,
            STATUS_STORAGE_PARSE_ERROR,
            STATUS_INTEGRATION_ERROR,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ErrorStatus {

    }

    /**
     * Data statuses.
     */
    @IntDef({
        STATUS_FRESH,
        STATUS_LOCAL,
        STATUS_INCONSISTENT
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface DataStatus {

    }

    /**
     * The status of the request is fresh.
     */
    public static final int STATUS_FRESH = 0;
    /**
     * The status of the data is dataLocal.
     */
    public static final int STATUS_LOCAL = 1;
    /**
     * The status of the data is not consistent. This means an error
     * was produced when we tried to reach the dataLocal storage.
     */
    public static final int STATUS_INCONSISTENT = 2;

    /**
     * Unknown error. This relates to every exception not considered as a halo controlled exception.
     */
    public static final int STATUS_UNKNOWN_ERROR = -1;
    /**
     * The operation was ok.
     */
    public static final int STATUS_OK = 0;
    /**
     * ErrorStatus for a request that has been canceled.
     */
    public static final int STATUS_CANCELED = 1;
    /**
     * Connection error status. The device cannot be connected to the server.
     */
    public static final int STATUS_CONNECTION_ERROR = 2;
    /**
     * There is an internal server error on this request.
     */
    public static final int STATUS_INTERNAL_SERVER_ERROR = 3;
    /**
     * There us a security error on this request.
     */
    public static final int STATUS_SECURITY_ERROR = 4;
    /**
     * The requests is reaching an endpoint not available.
     */
    public static final int STATUS_NOT_FOUND_ERROR = 5;
    /**
     * There is a storage error.
     */
    public static final int STATUS_GENERAL_STORAGE_ERROR = 6;
    /**
     * There is a problem parsing the data from the network.
     */
    public static final int STATUS_NETWORK_PARSE_ERROR = 7;
    /**
     * There is an error while parsing the data from the database.
     */
    public static final int STATUS_STORAGE_PARSE_ERROR = 8;
    /**
     * Error integrating some service.
     */
    public static final int STATUS_INTEGRATION_ERROR = 9;

    /**
     * Creator for the integration error.
     */
    public static final Parcelable.Creator<HaloStatus> CREATOR = new Parcelable.Creator<HaloStatus>() {
        public HaloStatus createFromParcel(Parcel source) {
            return new HaloStatus(source);
        }

        public HaloStatus[] newArray(int size) {
            return new HaloStatus[size];
        }
    };
    /**
     * The current builder related to the status.
     */
    private final Builder mBuilder;

    /**
     * Data status constructor to build the data status.
     *
     * @param builder The builder to be used.
     */
    private HaloStatus(@NonNull Builder builder) {
        mBuilder = builder;
    }

    /**
     * Constructor with a parcel.
     * @param in The parcel.
     */
    protected HaloStatus(Parcel in) {
        this.mBuilder = in.readParcelable(Builder.class.getClassLoader());
    }

    /**
     * Creates a new data status builder.
     *
     * @return The data status builder.
     */
    @Api(1.0)
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a builder from another builder.
     * @param builder The builder.
     * @return The new builder.
     */
    @Api(2.0)
    public static Builder builder(@NonNull Builder builder){
        AssertionUtils.notNull(builder, "builder");
        return new Builder(builder);
    }

    /**
     * Creates the builder from another status.
     * @param status The parent status.
     * @return The builder created.
     */
    @Api(2.0)
    public static Builder builder(@NonNull HaloStatus status){
        AssertionUtils.notNull(status, "status");
        return new Builder(status);
    }

    /**
     * Creates another builder so we can modify the current status creating another
     * inmutable object.
     * @return The builder.
     */
    @Api(2.0)
    @NonNull
    public Builder modify(){
        return new Builder(mBuilder);
    }

    /**
     * Checks if the the request data is fresh.
     *
     * @return True if it is the result of a network request thus this data is fresh. False otherwise.
     */
    @Api(1.0)
    public boolean isFresh() {
        return mBuilder.mDataStatus == STATUS_FRESH;
    }

    /**
     * Provides if the status is considered as dataLocal data.
     *
     * @return True if it is dataLocal, false otherwise.
     */
    @Api(1.0)
    public boolean isLocal() {
        return mBuilder.mDataStatus == STATUS_LOCAL;
    }

    /**
     * Adds the inconsistent state to relate you cannot rely in the data information provided.
     * This status means there is some error in the sdk, so you may look to report it.
     * @return True if inconsistent, false otherwise.
     */
    @Api(2.0)
    public boolean isInconsistent(){
        return mBuilder.mDataStatus == STATUS_INCONSISTENT;
    }

    /**
     * Checks if the operation produced an exception.
     *
     * @return True if there was no exception.
     */
    @Api(1.0)
    public boolean isOk() {
        boolean isFresh = mBuilder.mErrorStatus == STATUS_OK && mBuilder.mDataStatus == STATUS_FRESH;
        boolean isLocalRequest = mBuilder.mErrorStatus == STATUS_OK && mBuilder.mDataStatus == STATUS_LOCAL;
        boolean isLocalWithBadConnection = mBuilder.mErrorStatus == STATUS_CONNECTION_ERROR && mBuilder.mDataStatus == STATUS_LOCAL;
        return isFresh || isLocalRequest || isLocalWithBadConnection;
    }

    /**
     * Cancel status support.
     * @return The result is a cancelled request.
     */
    @Api(2.0)
    public boolean isCanceled(){
        return mBuilder.mErrorStatus == STATUS_CANCELED;
    }

    /**
     * Indicates if the error produced is a storage error.
     *
     * @return True if storage error, false otherwise.
     */
    @Api(1.0)
    public boolean isGeneralStorageError() {
        return mBuilder.mErrorStatus == STATUS_GENERAL_STORAGE_ERROR;
    }

    /**
     * Indicates that there is a storage error while parsing the data.
     *
     * @return True if there is an error, false otherwise.
     */
    @Api(1.0)
    public boolean isStorageParseError() {
        return mBuilder.mErrorStatus == STATUS_STORAGE_PARSE_ERROR;
    }

    /**
     * Indicates if an internal server was produced, false otherwise.
     *
     * @return True if the error is a server exception, false otherwise.
     */
    @Api(1.0)
    public boolean isInternalServerError() {
        return mBuilder.mErrorStatus == STATUS_INTERNAL_SERVER_ERROR;
    }

    /**
     * Indicates if the request has reached an endpoint that does not exist.
     *
     * @return True if the endpoint has returned a 404, false otherwise.
     */
    @Api(1.0)
    public boolean isNotFoundException() {
        return mBuilder.mErrorStatus == STATUS_NOT_FOUND_ERROR;
    }

    /**
     * Indicates that the error produced is a security error.
     *
     * @return True if security error, false otherwise.
     */
    @Api(1.0)
    public boolean isSecurityError() {
        return mBuilder.mErrorStatus == STATUS_SECURITY_ERROR;
    }

    /**
     * Indicates that there is an error while parsing the data.
     *
     * @return True if there was a parsing error, false otherwise.
     */
    @Api(1.0)
    public boolean isNetworkParseError() {
        return mBuilder.mErrorStatus == STATUS_NETWORK_PARSE_ERROR;
    }

    /**
     * Checks if the error is related to a network error.
     *
     * @return True if it a network error, false otherwise.
     */
    @Api(1.0)
    public boolean isNetworkError() {
        return isError() && mBuilder.mException instanceof HaloNetException;
    }

    /**
     * Indicates if the exception produced is a storage exception whether it is a parse error
     * or any other kind.
     *
     * @return True if it is a storage error. False otherwise.
     */
    @Api(1.0)
    public boolean isStorageError() {
        return isError() && mBuilder.mException instanceof HaloStorageException;
    }

    /**
     * Integration error with some plugin. This error can be sent from plugins to show something
     * is wrong.
     * @return True with the integration. False otherwise.
     */
    @Api(2.0)
    public boolean isIntegrationError(){
        return isError() && mBuilder.mException instanceof HaloIntegrationException;
    }

    /**
     * Indicates if the status is an error resulting from an operation.
     *
     * @return True if it is an error, false otherwise.
     */
    @Api(1.0)
    public boolean isError() {
        return mBuilder.mException != null;
    }

    /**
     * Provides the exception message.
     *
     * @return The exception message produced.
     */
    @Nullable
    @Api(1.0)
    public String getExceptionMessage() {
        if (isError()) {
            return mBuilder.mException.getMessage();
        }
        return null;
    }

    /**
     * Provides the exception.
     * @return The exception.
     */
    @Api(2.0)
    @Nullable
    public Exception exception(){
        return mBuilder.mException;
    }

    /**
     * Provides the current status of the data.
     * @return The status of the data.
     */
    @Api(2.0)
    @DataStatus
    public int dataStatus() {
        return mBuilder.mDataStatus;
    }

    /**
     * Provides the error status.
     * @return The error status.
     */
    @Api(2.0)
    @ErrorStatus
    public int errorStatus(){
        return mBuilder.mErrorStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mBuilder, flags);
    }

    /**
     * The builder for the data status.
     */
    public static class Builder implements IBuilder<HaloStatus>, Parcelable {
        public static final Creator<Builder> CREATOR = new Creator<Builder>() {
            public Builder createFromParcel(Parcel source) {
                return new Builder(source);
            }

            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        };
        /**
         * The status of the data.
         */
        @DataStatus
        private int mDataStatus;
        /**
         * Tells if the data is fresh or not.
         */
        @ErrorStatus
        private int mErrorStatus;
        /**
         * Exception produced in this request.
         */
        private Exception mException;

        /**
         * The builder constructor.
         */
        private Builder() {
            mDataStatus = STATUS_FRESH;
            mErrorStatus = STATUS_OK;
        }

        /**
         * Creates another builder from the previous one.
         * @param builder The previous builder.
         */
        private Builder(@NonNull Builder builder){
            mDataStatus = builder.mDataStatus;
            mErrorStatus = builder.mErrorStatus;
            mException = builder.mException;
        }

        @SuppressWarnings("ResourceType")
        protected Builder(Parcel in) {
            this.mDataStatus = in.readInt();
            this.mErrorStatus = in.readInt();
            this.mException = (Exception) in.readSerializable();
        }

        /**
         * Creates the new builder based on the data from the status.
         * @param status The status.
         */
        protected Builder(HaloStatus status) {
            this.mDataStatus = status.dataStatus();
            this.mErrorStatus = status.errorStatus();
            this.mException = status.exception();
        }

        /**
         * Sets this item as an dataLocal one without an exception.
         *
         * @return The current builder.
         */
        @Api(1.0)
        @NonNull
        public Builder dataLocal() {
            mDataStatus = STATUS_LOCAL;
            return this;
        }

        /**
         * Sets the status of the data as inconsistent. This means
         * that an error that cannot be recovered happened.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder dataInconsitent(){
            mDataStatus = STATUS_INCONSISTENT;
            return this;
        }

        /**
         * Sets the status as a canceled event.
         * @return The status to cancel.
         */
        @Api(2.0)
        @NonNull
        public Builder cancel(){
            mDataStatus = STATUS_INCONSISTENT;
            mErrorStatus = STATUS_CANCELED;
            return this;
        }

        /**
         * Sets the error data in the builder. Also marks the data status
         * as inconsistent.
         *
         * @param e The error to ser.
         * @return The current builder.
         */
        @Api(1.0)
        public Builder error(@Nullable Exception e) {
            if (e != null) {
                mException = e;
                mErrorStatus = getStatusFromException(e);
                dataInconsitent();
                Halog.e(getClass(), "Erroneous status reached. ErrorStatus: " + mErrorStatus + ", message: " + e.getMessage(), e);
            }
            return this;
        }

        /**
         * Provides the status given an exception.
         *
         * @param e The exception to check.
         * @return The status.
         */
        @ErrorStatus
        private int getStatusFromException(@NonNull Exception e) {
            int status = STATUS_UNKNOWN_ERROR;
            if (e instanceof HaloServerException) {
                status = STATUS_INTERNAL_SERVER_ERROR;
            } else if (e instanceof HaloAuthenticationException) {
                status = STATUS_SECURITY_ERROR;
            } else if (e instanceof HaloNotFoundException) {
                status = STATUS_NOT_FOUND_ERROR;
            } else if (e instanceof HaloConnectionException) {
                status = STATUS_CONNECTION_ERROR;
            } else if (e instanceof HaloStorageGeneralException) {
                status = STATUS_GENERAL_STORAGE_ERROR;
            } else if (e instanceof HaloNetParseException) {
                status = STATUS_NETWORK_PARSE_ERROR;
            } else if (e instanceof HaloStorageParseException) {
                status = STATUS_STORAGE_PARSE_ERROR;
            } else if(e instanceof HaloIntegrationException){
                status = STATUS_INTEGRATION_ERROR;
            }
            return status;
        }

        /**
         * Provides if the request has run successfully.
         *
         * @return True if it went ok or false otherwise.
         */
        public boolean isOk() {
            return mErrorStatus == STATUS_OK;
        }

        /**
         * Checks if it is a connection error.
         *
         * @return True if it is a connection error, false otherwise.
         */
        public boolean isConnectionError() {
            return mErrorStatus == STATUS_CONNECTION_ERROR;
        }

        /**
         * Builds the data status.
         *
         * @return The data status.
         */
        @NonNull
        @Override
        @Api(1.0)
        public HaloStatus build() {
            //Ensure the status is valid
            if(mDataStatus == STATUS_FRESH && mErrorStatus != STATUS_OK){
                throw new IllegalStateException("This status is not valid. Fresh is incompatible with error");
            }
            if(mDataStatus == STATUS_INCONSISTENT && mErrorStatus == STATUS_OK){
                throw new IllegalStateException("This status is not valid. Inconsistent is not compatible with ok");
            }
            return new HaloStatus(this);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.mDataStatus);
            dest.writeInt(this.mErrorStatus);
            dest.writeSerializable(this.mException);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        //Data status
        switch (mBuilder.mDataStatus){
            case STATUS_FRESH:
                builder.append("fresh ");
                break;
            case STATUS_LOCAL:
                builder.append("local ");
                break;
            case STATUS_INCONSISTENT:
                builder.append("inconsistent ");
                break;
        }
        //Error status
        switch (mBuilder.mErrorStatus) {
            case STATUS_OK:
                builder.append("ok");
                break;
            case STATUS_CANCELED:
                builder.append("canceled");
                break;
            case STATUS_CONNECTION_ERROR:
                builder.append("connection error");
                break;
            case STATUS_INTERNAL_SERVER_ERROR:
                builder.append("internal server error");
                break;
            case STATUS_SECURITY_ERROR:
                builder.append("security error");
                break;
            case STATUS_NOT_FOUND_ERROR:
                builder.append("not found error");
                break;
            case STATUS_GENERAL_STORAGE_ERROR:
                builder.append("storage error");
                break;
            case STATUS_NETWORK_PARSE_ERROR:
                builder.append("network parsing error");
                break;
            case STATUS_STORAGE_PARSE_ERROR:
                builder.append("storage parsing error");
                break;
            case STATUS_INTEGRATION_ERROR:
                builder.append("integration error");
                break;
            case STATUS_UNKNOWN_ERROR:
            default:
                builder.append("unknown error");
        }
        return builder.toString();
    }
}
