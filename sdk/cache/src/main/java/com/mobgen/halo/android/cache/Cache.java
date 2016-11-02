package com.mobgen.halo.android.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class Cache<S> implements ICache {

    public static final long UNLIMITED_TTL = -1;
    @NonNull
    private List<InsertionListener> mInsertionListeners;
    @NonNull
    private List<DeletionListener> mDeletionListeners;
    @NonNull
    private List<CacheDropListener> mDropListeners;
    @Nullable
    private CacheConverter.Factory<S> mFactory;

    public Cache(@Nullable CacheConverter.Factory<S> factory) {
        mInsertionListeners = new ArrayList<>(2);
        mDeletionListeners = new ArrayList<>(2);
        mDropListeners = new ArrayList<>(2);
        mFactory = factory;
    }

    @Override
    public void init() throws CacheException {
        // Intended to be overridden
    }

    @Override
    public void addDeletionListener(@NonNull DeletionListener deletionListener) {
        mDeletionListeners.add(deletionListener);
    }

    @Override
    public void addInsertionListener(@NonNull InsertionListener insertionListener) {
        mInsertionListeners.add(insertionListener);
    }

    @Override
    public void addDropListener(@NonNull CacheDropListener listener) {
        mDropListeners.add(listener);
    }

    @Override
    public void removeDeletionListener(@NonNull DeletionListener deletionListener) {
        mDeletionListeners.remove(deletionListener);
    }

    @Override
    public void removeInsertionListener(@NonNull InsertionListener insertionListener) {
        mInsertionListeners.remove(insertionListener);
    }

    @Override
    public void removeDropListener(@NonNull CacheDropListener listener) {
        mDropListeners.remove(listener);
    }

    protected final void notifyDeleted(String groupId, String itemId, Transaction transaction) {
        for (DeletionListener listener : mDeletionListeners) {
            listener.onDeleted(groupId, itemId, transaction);
        }
    }

    protected final void notifyDeleted(String groupId, Transaction transaction) {
        for (DeletionListener listener : mDeletionListeners) {
            listener.onDeleted(groupId, transaction);
        }
    }

    protected final void notifyInserted(@NonNull String groupId, @NonNull String id, @NonNull Object item, long expirationDate, Transaction transaction) {
        for (InsertionListener listener : mInsertionListeners) {
            listener.onInserted(groupId, id, item, expirationDate, transaction);
        }
    }

    protected final void notifyDropped() {
        for (CacheDropListener listener : mDropListeners) {
            listener.onCacheDropped();
        }
    }

    protected void beginTransaction(@NonNull Transaction<S> transaction){
        // Intended to be overridden
    }

    @Override
    @NonNull
    public Transaction<S> withTransaction() {
        return new Transaction<>(this);
    }

    @Override
    public final void endTransaction(@NonNull Transaction transaction) throws CacheException {
        if (transaction.isCommited()) {
            onTransactionSuccess(transaction);
        } else {
            onTransactionError(transaction);
        }
    }

    @Nullable
    public final CacheConverter.Factory<S> converter() {
        return mFactory;
    }

    protected void onTransactionSuccess(Transaction transaction) throws CacheException {
        // Intended to be overridden
    }

    protected void onTransactionError(Transaction transaction) throws CacheException {
        // Intended to be overridden
    }

    @NonNull
    public abstract <T> String[] insert(@NonNull List<T> data, @NonNull String groupId, @NonNull IdGenerator<T> idGenerator, @NonNull Transaction<S> transaction) throws CacheException;

    @NonNull
    public abstract String insert(@Nullable Object data, @NonNull String groupId, @NonNull String id, @NonNull Transaction<S> transaction) throws CacheException;

    public abstract void delete(@NonNull String groupId, @NonNull String id, @NonNull Transaction<S> transaction) throws CacheException;

    public abstract void delete(@NonNull String groupId, @NonNull Transaction<S> transaction) throws CacheException;

    @Nullable
    public abstract <T> T get(CacheType<T> clazz, @NonNull String groupId, @NonNull String id) throws CacheException;

    @NonNull
    public abstract <T> List<T> get(CacheType<T> clazz, @NonNull String groupId, @NonNull String... id) throws CacheException;

    @NonNull
    public abstract <T> List<T> get(CacheType<T> clazz, @NonNull String groupId) throws CacheException;

    protected long ttl() {
        return UNLIMITED_TTL;
    }

    protected void clearExpired(@NonNull String groupId) {
        // Clears the expired items. Support for ttls. Intended to override
    }

    public interface IdGenerator<T> {
        String idFromObject(@Nullable T data);
    }
}
