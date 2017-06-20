package com.mobgen.halo.android.auth.models;

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
@Keep
public class ReferenceFilter {

    private final String REFERENCE_FILTER = "filterReferences[]=";
    private final String CONCATENATE_FILTER = "&";

    List<String> mFilters;
    String mFilterToApply;

    protected ReferenceFilter(){
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


    public String getCurrentReferences(){
        mFilterToApply = "";
        if(mFilterToApply != null) {
            for (int i = 0; i < mFilters.size(); i++) {
                mFilterToApply = mFilterToApply + REFERENCE_FILTER + mFilters.get(i) + CONCATENATE_FILTER;
            }
        }
        return mFilterToApply;
    }

    public String getAll(){
        mFilterToApply = "";
        return  mFilterToApply;
    }

    public String noReferences(){
        mFilterToApply = REFERENCE_FILTER;
        return  mFilterToApply;
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

        @NonNull
        @Api(2.4)
        public ReferenceFilter.Builder filters(@NonNull String... filters) {
            AssertionUtils.notNull(filters, "filters");
            mFilters = addToList(mFilters,filters);
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
