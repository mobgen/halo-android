package com.mobgen.halo.android.cache;

import com.mobgen.halo.android.cache.adapters.sqlite.SQLiteCache;
import com.mobgen.halo.android.cache.adapters.sqlite.converter.LoganSquareConverterFactory;
import com.mobgen.halo.android.cache.mock.NotSerializableTestItem;
import com.mobgen.halo.android.cache.mock.TestCacheItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SQLiteCacheTest {

    private SQLiteCache mCache;

    @Before
    public void create() throws CacheException {
        mCache = SQLiteCache.builder(RuntimeEnvironment.application)
                .converterFactory(new LoganSquareConverterFactory())
                .build();
        mCache.init();
    }

    @Test
    public void addAndGetItemFromCacheTest() throws CacheException {
        String groupId = "group";
        TestCacheItem item = new TestCacheItem("Inserted item");
        mCache.insert(item, groupId, item.getId(), mCache.withTransaction());
        TestCacheItem insertedItem = mCache.get(new CacheType<TestCacheItem>() {
        }, groupId, item.getId());
        assertEquals(item, insertedItem);
    }

    @Test
    public void multipleInsertionTest() throws CacheException {
        String groupId = "group";
        TestCacheItem item1 = new TestCacheItem("Inserted item 1");
        TestCacheItem item2 = new TestCacheItem("Inserted item 2");
        mCache.insert(Arrays.asList(item1, item2, null), groupId, TestCacheItem.generator(), null);
        assertEquals(2, mCache.get(new CacheType<TestCacheItem>() {
        }, groupId).size());
        assertEquals(0, mCache.get(new CacheType<TestCacheItem>() {
        }, "anotherGroup").size());
    }

    @Test
    public void cacheTTLTest() throws CacheException, InterruptedException {
        long cacheTime = 100;
        Cache<String> cache = SQLiteCache.builder(RuntimeEnvironment.application)
                .converterFactory(new LoganSquareConverterFactory())
                .name("ttl_cache")
                .ttl(100, TimeUnit.MILLISECONDS)
                .build();
        cache.init();
        String groupId = "group";
        TestCacheItem item = new TestCacheItem("Inserted item 1");
        assertEquals(cacheTime, cache.ttl());
        cache.insert(item, groupId, item.getId(), null);
        Thread.sleep(cacheTime + 10);
        TestCacheItem insertedItem = cache.get(new CacheType<TestCacheItem>() {
        }, groupId, item.getId());
        assertNull(insertedItem);
    }

    @Test
    public void findWithInvalidIdTest() throws CacheException {
        assertNull(mCache.get(new CacheType<TestCacheItem>() {
        }, "group", "1"));
    }

    @Test
    public void insertNotSerializableItemTest() throws CacheException {
        NotSerializableTestItem item = new NotSerializableTestItem();
        mCache.insert(item, "group", item.getId(), null);
        assertNull(mCache.get(new CacheType<NotSerializableTestItem>() {
        }, "group", item.getId()));
    }

    @Test
    public void dropCacheTest() throws CacheException {
        String groupId = "group";
        TestCacheItem item1 = new TestCacheItem("Inserted item 1");
        TestCacheItem item2 = new TestCacheItem("Inserted item 2");
        mCache.insert(Arrays.asList(item1, item2), groupId, TestCacheItem.generator(), null);
        assertEquals(2, mCache.get(new CacheType<TestCacheItem>() {
        }, groupId).size());
        mCache.dropCache();
        assertEquals(0, mCache.get(new CacheType<TestCacheItem>() {
        }, groupId).size());
    }

    @Test
    public void getItemsByIdsTest() throws CacheException {
        String groupId = "group";
        TestCacheItem item1 = new TestCacheItem("Inserted item 1");
        TestCacheItem item2 = new TestCacheItem("Inserted item 2");
        mCache.insert(Arrays.asList(item1, item2), groupId, TestCacheItem.generator(), null);
        assertEquals(2, mCache.get(new CacheType<TestCacheItem>() {
        }, groupId, item1.getId(), item2.getId(), "fakeId").size());
        assertEquals(1, mCache.get(new CacheType<TestCacheItem>() {
        }, groupId, new String[]{item1.getId()}).size());
    }

    @Test
    public void deleteItemTest() throws CacheException {
        String groupId = "group";
        TestCacheItem item = new TestCacheItem("Inserted item 1");
        mCache.insert(item, groupId, item.getId(), null);
        assertEquals(item, mCache.get(new CacheType<TestCacheItem>() {
        }, groupId, item.getId()));
        mCache.delete(groupId, item.getId(), null);
        assertNull(mCache.get(new CacheType<TestCacheItem>() {
        }, groupId, item.getId()));
    }

    @Test
    public void deleteGroupTest() throws CacheException {
        String groupId1 = "group1";
        String groupId2 = "group2";
        TestCacheItem item = new TestCacheItem("Inserted item 1");
        mCache.insert(item, groupId1, item.getId(), null);
        mCache.insert(item, groupId2, item.getId(), null);
        assertEquals(item, mCache.get(new CacheType<TestCacheItem>() {
        }, groupId1, item.getId()));
        assertEquals(item, mCache.get(new CacheType<TestCacheItem>() {
        }, groupId2, item.getId()));
        mCache.delete(groupId1, null);
        assertNull(mCache.get(new CacheType<TestCacheItem>() {
        }, groupId1, item.getId()));
        assertNotNull(mCache.get(new CacheType<TestCacheItem>() {
        }, groupId2, item.getId()));
    }

    @Test
    public void executeTransaction() throws CacheException {
        String groupId = "group";
        TestCacheItem item1 = new TestCacheItem("Inserted item 1");
        TestCacheItem item2 = new TestCacheItem("Inserted item 2");
        Transaction transaction = mCache.withTransaction()
                .insert(item1, groupId, item1.getId())
                .insert(item2, groupId, item2.getId())
                .delete(groupId)
                .insert(item2, groupId, item2.getId());
        assertNotNull(transaction);
        assertFalse(transaction.isCommited());
        transaction.commit();
        assertTrue(transaction.isCommited());
        assertEquals(item2, mCache.get(new CacheType<TestCacheItem>() {
        }, groupId, item2.getId()));
    }

    @Test
    public void executeRollbackTransactionTest() throws CacheException {
        String groupId = "group";
        TestCacheItem item1 = new TestCacheItem("Inserted item 1");
        TestCacheItem item2 = new TestCacheItem("Inserted item 2");
        mCache.withTransaction()
                .insert(item1, groupId, item1.getId())
                .insert(item2, groupId, item2.getId())
                .delete(groupId)
                .insert(item2, groupId, item2.getId())
                .forceRollback()
                .commit();
        assertNull(mCache.get(new CacheType<TestCacheItem>() {
        }, groupId, item2.getId()));
    }

    @Test
    public void multipleInitTest() throws CacheException {
        String groupId = "group";
        TestCacheItem item1 = new TestCacheItem("Inserted item 1");
        TestCacheItem item2 = new TestCacheItem("Inserted item 2");
        mCache.withTransaction()
                .insert(item1, groupId, item1.getId())
                .insert(item2, groupId, item2.getId())
                .commit();
        assertEquals(2, mCache.get(new CacheType<TestCacheItem>() {
        }, groupId).size());
        //Reinit
        mCache.init();
        assertEquals(2, mCache.get(new CacheType<TestCacheItem>() {
        }, groupId).size());
    }

    @Test(expected = NullPointerException.class)
    public void invalidFactoryCreation() {
        SQLiteCache.builder(null).build();
    }

    @Test(expected = IllegalStateException.class)
    public void multipleCommitTransaction() throws CacheException {
        Transaction transaction = mCache.withTransaction();
        transaction.commit();
        transaction.commit();
    }
}
