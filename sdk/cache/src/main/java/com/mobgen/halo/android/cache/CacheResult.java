package com.mobgen.halo.android.cache;

public class CacheResult {
    public int mInsertions;
    public int mDeletions;
    public Exception e;

    protected void clear() {
        mInsertions = 0;
        mDeletions = 0;
        e = null;
    }
}
