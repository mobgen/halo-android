package com.mobgen.halo.android.auth.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by f.souto.gonzalez on 19/06/2017.
 */

/**
 * The references filter to apply when fetching the identified user pocket.
 *
 */
@Keep
public class ReferenceFilter implements Parcelable {

    /**
     * Reference filter name.
     */
    private static final String REFERENCE_FILTER = "filterReferences[]=";

    /**
     * Reference concatenate char.
     */
    private static final String CONCATENATE_FILTER = "&";

    /**
     * List of filters to apply.
     */
    List<String> mFilters;

    /**
     * Current reference filter to apply.
     */
    String mFilterToApply;

    protected ReferenceFilter() {
        //empty constructor
    }

    /**
     * The Halo edit content instance.
     *
     * @param builder The builder.
     */
    private ReferenceFilter(@NonNull ReferenceFilter.Builder builder) {
        mFilters = builder.mFilters;
    }

    protected ReferenceFilter(Parcel in) {
        mFilters = in.createStringArrayList();
        mFilterToApply = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(mFilters);
        dest.writeString(mFilterToApply);
    }

    public static final Creator<ReferenceFilter> CREATOR = new Creator<ReferenceFilter>() {
        @Override
        public ReferenceFilter createFromParcel(Parcel in) {
            return new ReferenceFilter(in);
        }

        @Override
        public ReferenceFilter[] newArray(int size) {
            return new ReferenceFilter[size];
        }
    };

    /**
     * Set current filter references.
     *
     * @return The current filter references.
     */
    @NonNull
    @Api(2.4)
    public String getCurrentReferences() {
        mFilterToApply = "";
        if (mFilterToApply != null) {
            for (int i = 0; i < mFilters.size(); i++) {
                mFilterToApply = mFilterToApply + REFERENCE_FILTER + mFilters.get(i) + CONCATENATE_FILTER;
            }
        }
        return mFilterToApply;
    }

    /**
     * Set the get all elements filter.
     *
     * @return The get all fitler.
     */
    @NonNull
    @Api(2.4)
    public String getAll() {
        mFilterToApply = "";
        return mFilterToApply;
    }

    /**
     * Set no reference filter.
     *
     * @return The no reference filter.
     */
    @NonNull
    @Api(2.4)
    public String noReferences() {
        mFilterToApply = REFERENCE_FILTER;
        return mFilterToApply;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * The builder class.
     */
    @Keep
    public static class Builder implements IBuilder<ReferenceFilter> {

        List<String> mFilters;

        /**
         * Creates the builder.
         */
        public Builder() {

        }

        /**
         * Set the references filter.
         *
         * @param filters The references filters.
         * @return
         */
        @NonNull
        @Api(2.4)
        public ReferenceFilter.Builder filters(@NonNull String... filters) {
            AssertionUtils.notNull(filters, "filters");
            mFilters = addToList(mFilters, filters);
            return this;
        }

        /**
         * Adds something to the given list or creates it returning as a result.
         *
         * @param list  The list of items.
         * @param items The items.
         * @return The list returned or created.
         */
        @NonNull
        public static <T> List<T> addToList(@Nullable List<T> list, @Nullable T[] items) {
            List<T> finalList = list;
            if (items != null && items.length > 0) {
                if (finalList == null) {
                    finalList = new ArrayList<>();
                }
                finalList.addAll(Arrays.asList(items));
            }
            return finalList;
        }

        @NonNull
        @Override
        public ReferenceFilter build() {
            return new ReferenceFilter(this);
        }
    }
}
