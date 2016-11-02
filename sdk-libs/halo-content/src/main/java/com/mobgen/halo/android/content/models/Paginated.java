package com.mobgen.halo.android.content.models;

import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;

import java.util.List;

/**
 * Represents the paginated information. This should be used for those paginated calls that are
 * paginated.
 */
@Keep
@JsonObject
public class Paginated<T> {

    /**
     * The collection data that will be stored.
     */
    @JsonField(name = "items")
    List<T> mCollectionData;

    /**
     * The pagination info.
     */
    @JsonField(name = "pagination")
    PaginationInfo mPaginationInfo;

    /**
     * Parsing empty constructor.
     */
    protected Paginated() {
        //Empty constructor for parsing
    }

    /**
     * The collection data to be stored. This is the not paginated answer with the full count and the
     * limit and page as default. Page 1 since there is only one page, limit is the same as count and the
     * count provides the size of the data.
     *
     * @param collectionData The collection data.
     */
    public Paginated(@NonNull List<T> collectionData) {
        this(collectionData, new PaginationInfo(collectionData.size()));
    }

    /**
     * The collection data to be stored. This is the not paginated answer with the full count and the
     * limit and page as default. Page 1 since there is only one page, limit is the same as count and the
     * count provides the size of the data.
     *
     * @param collectionData The collection data.
     * @param paginationInfo The pagination info.
     */
    public Paginated(@NonNull List<T> collectionData, @NonNull PaginationInfo paginationInfo) {
        mCollectionData = collectionData;
        mPaginationInfo = paginationInfo;
    }

    /**
     * Provides the data wrapped.
     *
     * @return The data.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public List<T> data() {
        return mCollectionData;
    }

    /**
     * Provides the page for the pagination.
     *
     * @return The page of the pagination.
     */
    @Keep
    @Api(2.0)
    public int getPage() {
        return mPaginationInfo.getPage();
    }

    /**
     * Provides the limit.
     *
     * @return The limit of the pagination.
     */
    @Keep
    @Api(2.0)
    public int getLimit() {
        return mPaginationInfo.getLimit();
    }

    /**
     * Provides the count.
     *
     * @return The count of the pagination.
     */
    @Keep
    @Api(2.0)
    public int getCount() {
        return mPaginationInfo.getTotalItems();
    }

    /**
     * Provides the offset.
     *
     * @return The offset of the pagination.
     */
    @Keep
    @Api(2.0)
    public int getOffset() {
        return mPaginationInfo.getOffset();
    }

    /**
     * Provides the total pages of the pagination.
     *
     * @return The total pages of the pagination.
     */
    @Keep
    @Api(2.0)
    public int getTotalPages() {
        return mPaginationInfo.getTotalPages();
    }

    /**
     * Provides if the page fir this pagination is the last one.
     *
     * @return True if it is the last page. False otherwise.
     */
    @Keep
    @Api(2.0)
    public boolean isLastPage() {
        return getPage() == getTotalPages();
    }

    /**
     * Provides if the paginated items is the first page.
     *
     * @return True if it is the first page, false otherwise.
     */
    @Keep
    @Api(2.0)
    public boolean isFirstPage() {
        return getPage() == 1;
    }

    /**
     * Determines if the pagination has only a single page.
     *
     * @return True if the pagination has only one page, false otherwise.
     */
    @Keep
    @Api(2.0)
    public boolean isUniquePage() {
        return getTotalPages() == 1;
    }

    /**
     * Provides the pagination info object.
     *
     * @return The pagination info object.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public PaginationInfo info() {
        return mPaginationInfo;
    }
}
