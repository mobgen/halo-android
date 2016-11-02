package com.mobgen.halo.android.cache.algorithm;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.cache.Cache;
import com.mobgen.halo.android.cache.CacheException;
import com.mobgen.halo.android.cache.CacheType;
import com.mobgen.halo.android.cache.Index;
import com.mobgen.halo.android.cache.Query;
import com.mobgen.halo.android.cache.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BTreeIndex implements Index {

    private Cache<?> mCacheDependent;

    // group->property->tree
    private Map<String, Map<String, BTree<BTreeIndexContent>>> mMapInformation;

    public BTreeIndex(@NonNull Cache<?> cache) {
        mMapInformation = new HashMap<>(3);
        mCacheDependent = cache;
    }

    //DONE
    public void persistForest(@NonNull String groupId) throws CacheException {
        Map<String, BTree<BTreeIndexContent>> mapGroup = mMapInformation.get(groupId);
        if (mapGroup != null) {
            mCacheDependent.withTransaction()
                    .insert(new ArrayList<>(mapGroup.values()), INDEX_GROUP_ID, groupId)
                    .commit();
        }
    }

    //DONE
    public void deleteForest(@NonNull String groupId, @NonNull Transaction transaction) throws CacheException {
        mMapInformation.put(groupId, null);
        mCacheDependent.withTransaction()
                .delete(INDEX_GROUP_ID, groupId)
                .commit();
    }

    //DONE
    public void clearExpired(@NonNull String groupId) throws CacheException {
        Map<String, BTree<BTreeIndexContent>> mapForGroup = getOrBuildGroup(groupId);
        List<BTree<BTreeIndexContent>> treesInGroup = new ArrayList<>(mapForGroup.values());
        for (BTree<BTreeIndexContent> tree : treesInGroup) {
            tree.inOrder(new BTree.TraverseFunction<BTreeIndexContent>() {
                @Override
                public boolean onTraverse(BTreeIndexContent item) {
                    item.clearExpired();
                    return false;
                }
            });
        }
    }

    public void chopTree(@NonNull String groupId, @NonNull String id) throws CacheException {
        Map<String, BTree<BTreeIndexContent>> mapForGroup = getOrBuildGroup(groupId);
        mapForGroup.remove(id);
    }

    public void addToIndex(@NonNull String indexedProperty, @Nullable String indexedValue, @NonNull String groupId, @NonNull String itemId, long expirationDate) throws CacheException {
        // Bring persisted tree to memory in case it is needed
        Map<String, BTree<BTreeIndexContent>> mapForGroup = getOrBuildGroup(groupId);
        //Insert the content into the tree
        BTree<BTreeIndexContent> tree = getOrBuildTree(mapForGroup, indexedProperty);
        BTreeIndexContent contentToInsert = new BTreeIndexContent(indexedValue, itemId, expirationDate);

        //Slow??
        BTreeIndexContent contentInserted = tree.member(contentToInsert);
        if (contentInserted != null) {
            contentInserted.addId(itemId, expirationDate);
        } else {
            tree.insert(contentToInsert);
        }
    }

    //DONE
    @NonNull
    private Map<String, BTree<BTreeIndexContent>> getOrBuildGroup(@NonNull String groupId) throws CacheException {
        //Bring from memory
        Map<String, BTree<BTreeIndexContent>> mapForGroup = mMapInformation.get(groupId);
        if (mapForGroup == null) {
            //Bring from cache
            List<BTree<BTreeIndexContent>> content = mCacheDependent.get(new CacheType<List<BTree<BTreeIndexContent>>>() {
            }, INDEX_GROUP_ID, groupId);

            //Rebuild index from cache
            mapForGroup = new HashMap<>(2);
            if (content != null) {
                for (BTree<BTreeIndexContent> index : content) {
                    mapForGroup.put(index.getId(), index);
                }
            }
            mMapInformation.put(groupId, mapForGroup);
        }
        return mapForGroup;
    }

    //DONE
    @NonNull
    private BTree<BTreeIndexContent> getOrBuildTree(@NonNull Map<String, BTree<BTreeIndexContent>> indexGroup, @NonNull String property) {
        //Get or create the tree
        BTree<BTreeIndexContent> tree = indexGroup.get(property);
        if (tree == null) {
            tree = new BTree<>(property);
            indexGroup.put(property, tree);
        }
        return tree;
    }


    @NonNull
    @Override
    public String[] search(@NonNull Query query) throws CacheException {
        Set<String> ids = new HashSet<>(10);
        for (Query.Where condition : query.conditions()) {
            BTree<BTreeIndexContent> index = getOrBuildTree(getOrBuildGroup(query.groupId()), condition.getFieldName());
            ids.addAll(searchOp(index, condition));
        }
        return ids.toArray(new String[ids.size()]);
    }

    private Set<String> searchOp(BTree<BTreeIndexContent> index, final Query.Where condition) {
        final Set<String> ids = new HashSet<>(5);
        if (condition.getOp() == Query.Op.EQ) {
            index.inOrder(new BTree.TraverseFunction<BTreeIndexContent>() {
                @Override
                public boolean onTraverse(BTreeIndexContent item) {
                    boolean found = item.mValue.equals(condition.getValue());
                    if (found) {
                        ids.addAll(item.mIdExpirationDate.keySet());
                    }
                    return found;
                }
            });
        }
        return ids;
    }
}