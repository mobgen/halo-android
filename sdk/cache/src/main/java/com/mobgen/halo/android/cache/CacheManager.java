package com.mobgen.halo.android.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

public class CacheManager {

    private HashMap<String, Cache> mCacheMap;

    public CacheManager() {
        mCacheMap = new HashMap<>(2);
    }

    public void register(@NonNull String id, @Nullable Cache cache) {
        mCacheMap.put(id, cache);
    }

    public Cache cache(@NonNull String id) {
        return mCacheMap.get(id);
    }

    public Indexer indexer(@NonNull String id) {
        Indexer indexer = null;
        Cache cache = cache(id);
        if (cache != null && cache instanceof Indexer) {
            indexer = (Indexer) cache;
        }
        return indexer;
    }
}
