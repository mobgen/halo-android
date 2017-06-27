package com.mobgen.halo.android.content.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

/**
 * Created by f.souto.gonzalez on 27/06/2017.
 */
/**
 * Search sort query to order search results.
 */
@Keep
public class SearchSort implements Parcelable {

    /**
     * The sort field.
     */
    private String sortField;

    /**
     * The sort order to apply.
     */
    private String sortType;

    /**
     * The sort query.
     */
    private String sortQuery;

    /**
     * Constructor for a search sort query.
     *
     * @param field     The field.
     * @param sortOrder The sort order type.
     */
    public SearchSort(@NonNull @SortField.SortOperator String field, @NonNull @SortOrder.SortType String sortOrder) {
        AssertionUtils.notNull(field, "operator");
        AssertionUtils.notNull(sortOrder, "type");
        this.sortField = field;
        this.sortType = sortOrder;
        this.sortQuery = sortField + " " + sortType;
    }

    protected SearchSort(Parcel in) {
        sortField = in.readString();
        sortType = in.readString();
        sortQuery = in.readString();
    }

    public static final Creator<SearchSort> CREATOR = new Creator<SearchSort>() {
        @Override
        public SearchSort createFromParcel(Parcel in) {
            return new SearchSort(in);
        }

        @Override
        public SearchSort[] newArray(int size) {
            return new SearchSort[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sortField);
        dest.writeString(sortType);
        dest.writeString(sortQuery);
    }

    /**
     * Get the search sort as string.
     *
     * @return The string to perfom the search sort.
     */
    @Keep
    @NonNull
    @Api(2.4)
    public String getSortQuery() {
        return sortQuery;
    }
}
