package com.mobgen.halo.android.content.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * The pagination object used by the HALO backend.
 */
@Keep
@JsonObject
public class PaginationCriteria implements Parcelable {

    /**
     * The page to retrieve as the pagination item.
     */
    @JsonField(name = "page")
    int mPage;

    /**
     * Limits the items to that amount.
     */
    @JsonField(name = "limit")
    int mLimit;

    /**
     * Skips the current pagination.
     */
    @JsonField(name = "skip")
    boolean mSkip;

    public static final Creator<PaginationCriteria> CREATOR = new Creator<PaginationCriteria>() {
        public PaginationCriteria createFromParcel(Parcel source) {
            return new PaginationCriteria(source);
        }

        public PaginationCriteria[] newArray(int size) {
            return new PaginationCriteria[size];
        }
    };

    /**
     * Copy constructor for the pagination object.
     *
     * @param pagination The pagination object.
     */
    public PaginationCriteria(@NonNull PaginationCriteria pagination) {
        mPage = pagination.mPage;
        mLimit = pagination.mLimit;
        mSkip = pagination.mSkip;
    }

    /**
     * Constructor for the pagination with default parameters.
     */
    public PaginationCriteria() {
        mPage = 1;
        mLimit = 10;
    }

    protected PaginationCriteria(Parcel in) {
        this.mPage = in.readInt();
        this.mLimit = in.readInt();
        this.mSkip = in.readByte() != 0;
    }

    /**
     * Pagination object with
     *
     * @param page  The page to use.
     * @param limit The limit that will be used for the items.
     */
    public PaginationCriteria(int page, int limit) {
        mPage = page;
        mLimit = limit;
    }

    /**
     * Constructor to change the skip property.
     *
     * @param skip The skip property.
     */
    public PaginationCriteria(boolean skip) {
        this();
        mSkip = skip;
    }

    /**
     * Provides the page configured.
     *
     * @return The page.
     */
    @Keep
    @Api(2.0)
    public int getPage() {
        return mPage;
    }

    /**
     * Provides the limit configured.
     *
     * @return The limit.
     */
    @Keep
    @Api(2.0)
    public int getLimit() {
        return mLimit;
    }

    /**
     * Provides the value of the skip property.
     *
     * @return The skip property.
     */
    @Keep
    @Api(2.0)
    public boolean isSkipped() {
        return mSkip;
    }

    /**
     * Sets the skip property.
     *
     * @param skip The skip property.
     */
    @Keep
    @Api(2.0)
    public void setSkip(boolean skip) {
        mSkip = skip;
    }

    /**
     * Sets the page property.
     *
     * @param page The page property.
     */
    @Keep
    @Api(2.0)
    public void setPage(int page) {
        mPage = page;
    }

    /**
     * Sets the limit value.
     *
     * @param limit The limit value.
     */
    @Keep
    @Api(2.0)
    public void setLimit(int limit) {
        mLimit = limit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPage);
        dest.writeInt(this.mLimit);
        dest.writeByte(mSkip ? (byte) 1 : (byte) 0);
    }
}
