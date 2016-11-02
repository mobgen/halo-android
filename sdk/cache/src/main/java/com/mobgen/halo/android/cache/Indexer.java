package com.mobgen.halo.android.cache;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.cache.algorithm.BTreeIndexingAlgorithm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mobgen.halo.android.cache.Assert.notNull;


public class Indexer<A extends Index, C> extends Cache<C> {

    private Cache<C> mDelegatedCache;
    private IndexerFeeder<A> mIndexFeeder;

    public static <C> Indexer of(@NonNull Cache<C> cache) {
        return new Indexer<>(cache, new BTreeIndexingAlgorithm(cache));
    }

    public static <T extends Index, C> Indexer of(@NonNull Cache<C> cache, @NonNull IndexingAlgorithm<T> algorithm) {
        return new Indexer<>(cache, algorithm);
    }

    private Indexer(@NonNull Cache<C> cache, @NonNull IndexingAlgorithm<A> indexAlgorithm) {
        super(cache.converter());
        notNull(cache, "cache");
        notNull(indexAlgorithm, "indexingAlgorithm");
        mDelegatedCache = cache;
        mIndexFeeder = new IndexerFeeder<>(indexAlgorithm);
    }

    @Override
    public void init() throws CacheException {
        mDelegatedCache.init();
        mDelegatedCache.addInsertionListener(mIndexFeeder);
        mDelegatedCache.addDeletionListener(mIndexFeeder);
        mDelegatedCache.addDropListener(mIndexFeeder);
    }

    @Override
    public void dropCache() {
        mDelegatedCache.dropCache();
    }

    @Override
    protected void beginTransaction(@NonNull Transaction transaction) {
        mDelegatedCache.beginTransaction(transaction);
        mIndexFeeder.mIndexAlgorithm.prepareForTransaction(transaction);
    }

    @Nullable
    public <T> List<T> search(CacheType<T> type, @NonNull Query query) throws CacheException {
        notNull(query, "query");
        return get(type, query.groupId(), getIndex().search(query));
    }

    @Nullable
    public <T> T searchOne(CacheType<T> type, @NonNull Query query) throws CacheException {
        notNull(query, "query");
        String[] items = getIndex().search(query);
        if (items.length > 0) {
            return get(type, query.groupId(), items[0]);
        }
        return null;
    }


    @NonNull
    @Override
    public <T> String[] insert(@NonNull List<T> data, @NonNull String groupId, @NonNull IdGenerator<T> idGenerator, @NonNull Transaction<C> transaction) throws CacheException {
        return mDelegatedCache.insert(data, groupId, idGenerator, transaction);
    }

    @NonNull
    @Override
    public String insert(@Nullable Object data, @NonNull String groupId, @NonNull String itemId, @NonNull Transaction<C> transaction) throws CacheException {
        return mDelegatedCache.insert(data, groupId, itemId, transaction);
    }

    @Override
    public void delete(@NonNull String groupId, @NonNull String id, @NonNull Transaction<C> transaction) throws CacheException {
        mDelegatedCache.delete(groupId, id, transaction);
    }

    @Override
    public void delete(@NonNull String groupId, @NonNull Transaction<C> transaction) throws CacheException {
        mDelegatedCache.delete(groupId, transaction);
    }

    @Nullable
    @Override
    public <T> T get(CacheType<T> clazz, @NonNull String groupId, @NonNull String id) throws CacheException {
        clearExpired(groupId);
        return mDelegatedCache.get(clazz, groupId, id);
    }

    @NonNull
    @Override
    public <T> List<T> get(CacheType<T> clazz, @NonNull String groupId, @NonNull String... ids) throws CacheException {
        clearExpired(groupId);
        return mDelegatedCache.get(clazz, groupId, ids);
    }

    @NonNull
    @Override
    public <T> List<T> get(CacheType<T> clazz, @NonNull String groupId) throws CacheException {
        clearExpired(groupId);
        return mDelegatedCache.get(clazz, groupId);
    }

    @Override
    protected void onTransactionSuccess(Transaction transaction) throws CacheException {
        mDelegatedCache.onTransactionSuccess(transaction);
        mIndexFeeder.mIndexAlgorithm.completeTransaction(transaction);
    }

    @Override
    protected void onTransactionError(Transaction transaction) throws CacheException {
        mDelegatedCache.onTransactionError(transaction);
        mIndexFeeder.mIndexAlgorithm.rollbackIndexChanges(transaction);
    }

    @Override
    protected void clearExpired(@NonNull String groupId) {
        if (mDelegatedCache.ttl() != UNLIMITED_TTL) {
            mIndexFeeder.mIndexAlgorithm.clearExpired(groupId);
        }
        mDelegatedCache.clearExpired(groupId);
    }

    @NonNull
    public Index getIndex() {
        return mIndexFeeder.mIndexAlgorithm.getIndex();
    }

    private class IndexerFeeder<T extends Index> implements InsertionListener, DeletionListener, CacheDropListener {

        private final Map<String, String> INDEXED_PROPS = new HashMap<>(1);
        private IndexingAlgorithm<T> mIndexAlgorithm;

        private IndexerFeeder(IndexingAlgorithm<T> algorithm) {
            mIndexAlgorithm = algorithm;
        }

        @Override
        public void onInserted(@NonNull String groupId, @NonNull String id, @NonNull Object item, long expirationDate, @NonNull Transaction transaction) {
            if (item instanceof IndexableItem && !groupId.equals(Index.INDEX_GROUP_ID)) {
                ((IndexableItem) item).getIndexedProps(INDEXED_PROPS);
                mIndexAlgorithm.feed(new HashMap<>(INDEXED_PROPS), groupId, id, expirationDate, transaction);
                INDEXED_PROPS.clear();
            }
            notifyInserted(groupId, id, item, expirationDate, transaction);
        }

        @Override
        public void onDeleted(@NonNull String groupId, @NonNull String id, @NonNull Transaction transaction) {
            if (!groupId.equals(Index.INDEX_GROUP_ID)) {
                mIndexAlgorithm.feedDelete(groupId, id, transaction);
            }
            notifyDeleted(groupId, id, transaction);
        }

        @Override
        public void onDeleted(@NonNull String groupId, @NonNull Transaction transaction) {
            if (!groupId.equals(Index.INDEX_GROUP_ID)) {
                mIndexAlgorithm.feedDelete(groupId, transaction);
            }
            notifyDeleted(groupId, transaction);
        }

        @Override
        public void onCacheDropped() {
            mIndexAlgorithm.dropIndex();
            notifyDropped();
        }
    }
}
