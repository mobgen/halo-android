package com.mobgen.halo.android.cache.algorithm;

import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.cache.Cache;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@JsonObject
public class BTreeIndexContent implements Comparable<BTreeIndexContent> {

    @JsonField
    Map<String, Long> mIdExpirationDate;
    @JsonField
    String mValue;

    protected BTreeIndexContent() {
        mIdExpirationDate = new LinkedHashMap<>();
    }

    public BTreeIndexContent(String indexedValue, String itemId, long exepirationDate) {
        this();
        mValue = indexedValue;
        addId(itemId, exepirationDate);
    }

    public void addId(@NonNull String id, long expirationDate) {
        mIdExpirationDate.put(id, expirationDate);
    }

    public void clearExpired() {
        long expirationDate = new Date().getTime();
        for (Map.Entry<String, Long> entry : mIdExpirationDate.entrySet()) {
            if (entry.getValue() < expirationDate && entry.getValue() != Cache.UNLIMITED_TTL) {
                mIdExpirationDate.remove(entry.getKey());
            }
        }
    }

    @Override
    @SuppressWarnings("all")
    public int compareTo(@NonNull BTreeIndexContent another) {
        if (mValue != null && another.mValue != null) {
            return mValue.compareTo(another.mValue);
        }
        return mValue == another.mValue ? 0 : 1;
    }

    @Override
    public String toString() {
        return mValue;
    }
}