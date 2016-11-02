package com.mobgen.halo.android.cache;

public final class Assert {

    private Assert() {
        //Do not allow instances
    }

    public static <T> T notNull(T object, String name) {
        if (object == null) {
            throw new NullPointerException(name + " == null");
        }
        return object;
    }
}
