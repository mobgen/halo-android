package com.mobgen.halo.android.cache.mock;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.cache.Cache;
import com.mobgen.halo.android.cache.IndexableItem;

import java.util.Map;

@JsonObject
public class TestCacheItem implements IndexableItem {

    private static int mId;
    public static String QUERY_NAME = "name";

    @JsonField(name = "info")
    String mInfo;
    @JsonField(name = "id")
    int mCurrentId;

    public TestCacheItem(@Nullable String info) {
        mInfo = info;
        mCurrentId = mId++;
    }

    public TestCacheItem() {
    }

    @Nullable
    public String getInfo() {
        return mInfo;
    }

    @NonNull
    public String getId() {
        return String.valueOf(mCurrentId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestCacheItem that = (TestCacheItem) o;

        if (mCurrentId != that.mCurrentId) return false;
        return mInfo != null ? mInfo.equals(that.mInfo) : that.mInfo == null;

    }

    @Override
    public int hashCode() {
        int result = mInfo != null ? mInfo.hashCode() : 0;
        result = 31 * result + mCurrentId;
        return result;
    }

    public static Cache.IdGenerator<TestCacheItem> generator() {
        return new Cache.IdGenerator<TestCacheItem>() {
            @Override
            public String idFromObject(@Nullable TestCacheItem data) {
                if (data != null) {
                    return data.getId();
                }
                return null;
            }
        };
    }

    @Override
    public void getIndexedProps(@NonNull Map<String, String> map) {
        map.put(QUERY_NAME, mInfo);
    }
}
