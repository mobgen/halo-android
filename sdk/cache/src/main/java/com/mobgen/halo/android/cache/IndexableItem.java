package com.mobgen.halo.android.cache;

import android.support.annotation.NonNull;

import java.util.Map;

public interface IndexableItem {

    void getIndexedProps(@NonNull Map<String, String> map);
}
