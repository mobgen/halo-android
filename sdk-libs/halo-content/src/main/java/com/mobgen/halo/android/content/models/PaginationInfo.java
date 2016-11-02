package com.mobgen.halo.android.content.models;

import android.support.annotation.Keep;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * The pagination info for those requests that should be paginated. Pages start in 1.
 */
@Keep
@JsonObject
public class PaginationInfo {

    /**
     * The page number.
     */
    @JsonField(name = "page")
    int mPage;
    /**
     * The limit of items.
     */
    @JsonField(name = "limit")
    int mLimit;
    /**
     * The offset.
     */
    @JsonField(name = "offset")
    int mOffset;
    /**
     * The total items.
     */
    @JsonField(name = "totalItems")
    int mTotalItems;
    /**
     * The total pages.
     */
    @JsonField(name = "totalPages")
    int mTotalPages;

    /**
     * Parsing empty constructor.
     */
    protected PaginationInfo() {
        //Empty constructor for parsing
    }

    /**
     * The pagination constructor.
     *
     * @param count Count of the items brought.
     */
    public PaginationInfo(int count) {
        mPage = 1;
        mOffset = 0;
        mLimit = count;
        mTotalItems = count;
        mTotalPages = 1;
    }

    /**
     * The pagination info constructor for the items.
     *
     * @param page  The page number.
     * @param limit The limit.
     * @param count The count.
     */
    public PaginationInfo(int page, int limit, int count) {
        mPage = page;
        mLimit = limit;
        mTotalItems = count;
        mOffset = (page - 1) * limit;
        mTotalPages = (int) Math.ceil((double) count / (double) limit);
    }

    /**
     * Provides the page.
     *
     * @return The page.
     */
    public int getPage() {
        return mPage;
    }

    /**
     * Provides the limit.
     *
     * @return The limit.
     */
    public int getLimit() {
        return mLimit;
    }

    /**
     * Provides the offset.
     *
     * @return The offset.
     */
    public int getOffset() {
        return mOffset;
    }

    /**
     * Provides the total number of items.
     *
     * @return The total number of items.
     */
    public int getTotalItems() {
        return mTotalItems;
    }

    /**
     * Provides the total number of pages.
     *
     * @return The total number of pages.
     */
    public int getTotalPages() {
        return mTotalPages;
    }
}
