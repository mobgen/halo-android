package com.mobgen.halo.android.cache;

import com.mobgen.halo.android.cache.adapters.sqlite.SQLiteCache;
import com.mobgen.halo.android.cache.adapters.sqlite.converter.LoganSquareConverterFactory;
import com.mobgen.halo.android.cache.mock.TestCacheItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class IndexerTest {

    private Indexer<?, ?> mIndexer;

    @Before
    public void create() throws CacheException {
        mIndexer = Indexer.of(SQLiteCache.builder(RuntimeEnvironment.application)
                .converterFactory(new LoganSquareConverterFactory())
                .build());
        mIndexer.init();
    }

    @Test
    public void insertItemTest() throws CacheException {
        TestCacheItem item = new TestCacheItem("My item");
        String group = "myGroup";
        mIndexer.withTransaction()
                .insert(item, group, item.getId())
                .commit();
        Query query = Query.builder(group).where(TestCacheItem.QUERY_NAME, Query.Op.EQ, "My item").build();
        TestCacheItem itemSearch = mIndexer.searchOne(new CacheType<TestCacheItem>() {
        }, query);
        assertNotNull(itemSearch);
        assertEquals(item, itemSearch);
    }

    @Test
    public void mutiple1000InsertionsSearch() throws CacheException {
        List<TestCacheItem> items = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            items.add(new TestCacheItem(Long.toHexString(Double.doubleToLongBits(Math.random()))));
        }
        String group = "myGroup";
        mIndexer.withTransaction()
                .insert(items, group, TestCacheItem.generator())
                .commit();
        Query query = Query.builder(group).where(TestCacheItem.QUERY_NAME, Query.Op.EQ, items.get(0).getInfo()).build();
        TestCacheItem itemSearch = mIndexer.searchOne(new CacheType<TestCacheItem>() {
        }, query);
        assertNotNull(itemSearch);
        assertEquals(items.get(0), itemSearch);
    }
}
