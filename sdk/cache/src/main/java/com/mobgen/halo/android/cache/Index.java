package com.mobgen.halo.android.cache;

import android.support.annotation.NonNull;

public interface Index {

    String INDEX_GROUP_ID = "___reserved_group___";

    @NonNull
    String[] search(@NonNull Query query) throws CacheException;
}
