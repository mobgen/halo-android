package com.mobgen.halo.android.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

public interface IndexingAlgorithm<T extends Index> {

    void prepareForTransaction(Transaction transaction);

    /**
     * Indexes the pending changes into the in-memory index.
     *
     * @return The new index.
     * @throws CacheException
     */
    @NonNull
    T completeTransaction(Transaction transaction) throws CacheException;

    /**
     * Drops the current in memory and cached index. Keep in mind that doing
     * so means all the data that is inside the cache will not be indexed again.
     */
    void dropIndex();

    /**
     * Rollbacks all the pending changes if they exists.
     */
    void rollbackIndexChanges(@NonNull Transaction transaction);

    void feedDelete(@NonNull String groupId, @NonNull String id, @NonNull Transaction transaction);

    void feedDelete(@NonNull String groupId, @NonNull Transaction transaction);

    void feed(@Nullable Map<String, String> indexedProps, @NonNull String groupId, @NonNull String id, long expirationDate, @NonNull Transaction transaction);

    void clearExpired(@NonNull String groupId);

    Index getIndex();
}
