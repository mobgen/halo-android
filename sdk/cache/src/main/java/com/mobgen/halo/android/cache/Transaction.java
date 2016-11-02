package com.mobgen.halo.android.cache;

import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.mobgen.halo.android.cache.Assert.notNull;

public class Transaction<T> {

    private static final AtomicInteger TRANSACTION_ID_GENERATOR = new AtomicInteger();
    private final int mId;
    private boolean mCommited;
    private Cache<T> mDependentCache;
    private List<CacheOperation> mOperations;

    protected Transaction(@NonNull Cache<T> cache) {
        notNull(cache, "dependentCache");
        mId = TRANSACTION_ID_GENERATOR.getAndDecrement();
        mDependentCache = cache;
        mOperations = new ArrayList<>(3);
    }

    public final boolean isCommited() {
        return mCommited;
    }

    public final int getId() {
        return mId;
    }

    @NonNull
    public CacheResult commit() throws CacheException {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Log.w("Cache", "You are using the cache in the main thread and it can be dangerous");
        }
        if (mCommited) {
            throw new IllegalStateException("The transaction was already commited.");
        }
        CacheResult result = new CacheResult();
        try {
            mDependentCache.beginTransaction(this);
            //Execute the pending operations
            for (CacheOperation operation : mOperations) {
                operation.execute(result);
            }
            mCommited = true; // Set as success transaction
        } catch (Exception e) {
            result.clear();
            result.e = e;
            Log.e("Transaction", e.getMessage(), e);
        } finally {
            //Finish the transaction
            mDependentCache.endTransaction(this);
        }
        return result;
    }

    @NonNull
    public <T> Transaction insert(@NonNull final List<T> data, @NonNull final String groupId, @NonNull final Cache.IdGenerator<T> generator) throws CacheException {
        notNull(data, "data");
        notNull(groupId, "groupId");
        addOperation(new CacheOperation() {
            @Override
            public void execute(CacheResult result) throws CacheException {
                mDependentCache.insert(data, groupId, generator, Transaction.this);
                result.mInsertions += data.size();
            }
        });
        return this;
    }

    @NonNull
    public Transaction insert(@NonNull final Object data, @NonNull final String groupId, @NonNull final String itemId) throws CacheException {
        notNull(data, "data");
        notNull(groupId, "groupId");
        addOperation(new CacheOperation() {
            @Override
            public void execute(CacheResult result) throws CacheException {
                mDependentCache.insert(data, groupId, itemId, Transaction.this);
                result.mInsertions += 1;
            }
        });
        return this;
    }

    @NonNull
    public Transaction delete(@NonNull final String groupId, @NonNull final String id) throws CacheException {
        notNull(groupId, "groupId");
        notNull(id, "id");
        addOperation(new CacheOperation() {
            @Override
            public void execute(CacheResult result) throws CacheException {
                mDependentCache.delete(groupId, id, Transaction.this);
                result.mDeletions += 1;
            }
        });
        return this;
    }

    @NonNull
    public Transaction delete(@NonNull final String groupId) throws CacheException {
        notNull(groupId, "groupId");
        addOperation(new CacheOperation() {
            @Override
            public void execute(CacheResult result) throws CacheException {
                mDependentCache.delete(groupId, Transaction.this);
                result.mDeletions += 1;
            }
        });
        return this;
    }

    @NonNull
    public Transaction forceRollback() {
        addOperation(new CacheOperation() {
            @Override
            public void execute(CacheResult result) throws CacheException {
                throw new CacheException("Forced rollback");
            }
        });
        return this;
    }

    @NonNull
    protected final Cache<T> dependentCache() {
        return mDependentCache;
    }

    private void addOperation(@NonNull CacheOperation operation) {
        notNull(operation, "operation");
        mOperations.add(operation);
    }
}
