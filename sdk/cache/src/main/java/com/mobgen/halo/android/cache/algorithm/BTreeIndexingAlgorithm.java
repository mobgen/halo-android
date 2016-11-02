package com.mobgen.halo.android.cache.algorithm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;

import com.mobgen.halo.android.cache.Cache;
import com.mobgen.halo.android.cache.CacheException;
import com.mobgen.halo.android.cache.IndexingAlgorithm;
import com.mobgen.halo.android.cache.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mobgen.halo.android.cache.Assert.notNull;

public class BTreeIndexingAlgorithm implements IndexingAlgorithm<BTreeIndex> {

    private Cache<?> mCacheDependent;
    private BTreeIndex mDefaultIndex;
    private Set<String> mGroupsToCheckExpire;
    private SparseArray<TransactionChanges> mTransactionChanges;

    public BTreeIndexingAlgorithm(Cache<?> cache) {
        notNull(cache, "cache");
        mCacheDependent = cache;
        mTransactionChanges = new SparseArray<>(3);
        mGroupsToCheckExpire = new HashSet<>(3);
    }

    @Override
    public void prepareForTransaction(Transaction transaction) {
        mTransactionChanges.put(transaction.getId(), new TransactionChanges());
    }

    @NonNull
    @Override
    public BTreeIndex completeTransaction(@NonNull Transaction transaction) throws CacheException {
        BTreeIndex index = getIndex();
        TransactionChanges changes = getChanges(transaction);
        //Execute operations
        for (IndexOperation operation : changes.mPendingOperations) {
            operation.onExecute(index);
        }
        //Persist pending trees
        for (String group : changes.mPendingGroupsForPersist) {
            index.persistForest(group);
        }
        //Check expirations
        for (String group : mGroupsToCheckExpire) {
            index.clearExpired(group);
        }
        cleanPendingChanges(transaction);
        return index;
    }

    @Override
    public void dropIndex() {
        //We assume the full cache has dropped, so the index did it too
        mDefaultIndex = null;
        mTransactionChanges.clear();
    }

    private void cleanPendingChanges(Transaction transaction) {
        mTransactionChanges.remove(transaction.getId());
        mGroupsToCheckExpire.clear();
    }

    @NonNull
    private TransactionChanges getChanges(@NonNull Transaction transaction) {
        notNull(transaction, "transaction");
        TransactionChanges changes = mTransactionChanges.get(transaction.getId());
        if (changes == null) {
            throw new IllegalStateException("There is no open transaction for the transaction commited. This may be a bug in the cache index.");
        }
        return changes;
    }

    private void markAsPendingPersist(@NonNull Transaction transaction, @NonNull String groupId) {
        getChanges(transaction).mPendingGroupsForPersist.add(groupId);
    }

    @Override
    public synchronized void feedDelete(@NonNull final String groupId, @NonNull final Transaction transaction) {
        getChanges(transaction).mPendingOperations.add(new IndexOperation() {
            @Override
            public void onExecute(@NonNull BTreeIndex index) throws CacheException {
                index.deleteForest(groupId, transaction);
            }
        });
    }

    @Override
    public synchronized void feedDelete(@NonNull final String groupId, @NonNull final String property, @NonNull final Transaction transaction) {
        getChanges(transaction).mPendingOperations.add(new IndexOperation() {
            @Override
            public void onExecute(@NonNull BTreeIndex index) throws CacheException {
                index.chopTree(groupId, property);
                markAsPendingPersist(transaction, groupId);
            }
        });
    }

    @Override
    public synchronized void feed(@Nullable final Map<String, String> indexedProps, @NonNull final String groupId, @NonNull final String itemId, final long expirationDate, @NonNull final Transaction transaction) {
        if (indexedProps != null) {
            getChanges(transaction).mPendingOperations.add(new IndexOperation() {
                @Override
                public void onExecute(@NonNull BTreeIndex index) throws CacheException {
                    for (Map.Entry<String, String> entry : indexedProps.entrySet()) {
                        index.addToIndex(entry.getKey(), entry.getValue(), groupId, itemId, expirationDate);
                        //Add item to pending items
                        markAsPendingPersist(transaction, groupId);
                    }
                }
            });
        }
    }

    @Override
    public void clearExpired(@NonNull final String groupId) {
        mGroupsToCheckExpire.add(groupId);
    }

    @Override
    public void rollbackIndexChanges(@NonNull Transaction transaction) {
        cleanPendingChanges(transaction);
    }

    @Override
    public BTreeIndex getIndex() {
        if (mDefaultIndex == null) {
            mDefaultIndex = new BTreeIndex(mCacheDependent);
        }
        return mDefaultIndex;
    }

    private interface IndexOperation {
        /**
         * Executes an index operation on the index and provides the group ids that where modiifed.
         *
         * @param index The index.
         */
        void onExecute(@NonNull BTreeIndex index) throws CacheException;
    }

    private class TransactionChanges {
        private List<IndexOperation> mPendingOperations;
        private Set<String> mPendingGroupsForPersist;

        public TransactionChanges() {
            mPendingOperations = new ArrayList<>(2);
            mPendingGroupsForPersist = new HashSet<>(2);
        }
    }
}