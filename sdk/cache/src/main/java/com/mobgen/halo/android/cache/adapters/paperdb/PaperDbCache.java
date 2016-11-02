package com.mobgen.halo.android.cache.adapters.paperdb;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.cache.Cache;
import com.mobgen.halo.android.cache.CacheException;
import com.mobgen.halo.android.cache.CacheType;
import com.mobgen.halo.android.cache.Transaction;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Book;
import io.paperdb.Paper;

import static com.mobgen.halo.android.cache.Assert.notNull;

@SuppressWarnings("unchecked")
public class PaperDbCache extends Cache<Object> {

    private static final String INDEX_GROUPS = "__groups_available__";

    private Context mContext;

    public PaperDbCache(@NonNull Context context) {
        super(null);
        notNull(context, "context");
        mContext = context;
    }

    @Override
    public void init() throws CacheException {
        Paper.init(mContext);
    }

    @NonNull
    @Override
    public String insert(@Nullable Object data, @NonNull String groupId, @NonNull String id, @NonNull Transaction transaction) throws CacheException {
        if (data != null) {
            Paper.book(groupId).write(id, data);
            saveGroup(groupId);
            notifyInserted(groupId, id, data, -1, transaction);
        }
        return id;
    }

    @NonNull
    @Override
    public <T> String[] insert(@NonNull List<T> data, @NonNull String groupId, @NonNull IdGenerator<T> idGenerator, @NonNull Transaction<Object> transaction) throws CacheException {
        Book book = Paper.book(groupId);
        String[] ids = new String[data.size()];
        int i = 0;
        for (T item : data) {
            ids[i] = idGenerator.idFromObject(item);
            book.write(ids[i], item);
            notifyInserted(groupId, ids[i], item, -1, transaction);
            i++;
        }
        saveGroup(groupId);
        return ids;
    }

    private void saveGroup(@NonNull String groupId) {
        Paper.book(INDEX_GROUPS).write(groupId, "");
    }

    @Override
    public void delete(@NonNull String groupId, @NonNull String id, @NonNull Transaction transaction) throws CacheException {
        Paper.book(groupId).delete(id);
        notifyDeleted(groupId, id, transaction);
    }

    @Override
    public void delete(@NonNull String groupId, @NonNull Transaction transaction) throws CacheException {
        Paper.book(groupId).destroy();
        Paper.book(INDEX_GROUPS).delete(groupId);
        notifyDeleted(groupId, transaction);
    }

    @NonNull
    @Override
    public <T> List<T> get(CacheType<T> clazz, @NonNull String groupId) throws CacheException {
        Book book = Paper.book(groupId);
        List<String> keys = book.getAllKeys();
        List<T> result = new ArrayList<>();
        for (String key : keys) {
            result.add((T) book.read(key));
        }
        return result;
    }

    @NonNull
    @Override
    public <T> List<T> get(CacheType<T> clazz, @NonNull String groupId, @NonNull String... ids) throws CacheException {
        Book book = Paper.book(groupId);
        List<T> result = new ArrayList<>();
        for (String id : ids) {
            result.add((T) book.read(id));
        }
        return result;
    }

    @Nullable
    @Override
    public <T> T get(CacheType<T> clazz, @NonNull String groupId, @NonNull String id) throws CacheException {
        return Paper.book(groupId).read(id);
    }

    @Override
    public void dropCache() {
        Book bookGroups = Paper.book(INDEX_GROUPS);
        List<String> keys = bookGroups.getAllKeys();
        for (String key : keys) {
            Paper.book(key).destroy();
        }
        bookGroups.destroy();
        notifyDropped();
    }
}
