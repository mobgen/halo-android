package com.mobgen.halo.android.cache;

import android.support.annotation.NonNull;

public interface ICache {

    void endTransaction(Transaction transaction) throws CacheException;

    Transaction withTransaction();

    void init() throws CacheException;

    void addInsertionListener(@NonNull InsertionListener insertionListener);

    void removeInsertionListener(@NonNull InsertionListener insertionListener);

    void addDeletionListener(@NonNull DeletionListener deletionListener);

    void removeDeletionListener(@NonNull DeletionListener deletionListener);

    void addDropListener(@NonNull CacheDropListener listener);

    void removeDropListener(@NonNull CacheDropListener listener);

    interface InsertionListener {
        void onInserted(@NonNull String groupId, @NonNull String id, @NonNull Object item, long expirationDate, @NonNull Transaction transaction);
    }

    void dropCache();

    interface DeletionListener {
        void onDeleted(@NonNull String groupId, @NonNull String id, @NonNull Transaction transaction);

        void onDeleted(@NonNull String groupId, @NonNull Transaction transaction);
    }

    interface CacheDropListener {
        void onCacheDropped();
    }
}
