package com.mobgen.halo.android.framework.network.client.response;

import com.mobgen.halo.android.framework.common.annotations.Api;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Class that indirects the types allocated by generics. It wraps the types and
 * includes them for parameter parsing.
 */
public abstract class TypeReference<T> {

    /**
     * The type bounded.
     */
    private final Type mType;

    /**
     * Constructor to bind the actual type.
     */
    protected TypeReference() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        mType = ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

    /**
     * Gets the referenced type.
     *
     * @return The current type.
     */
    @Api(2.0)
    public Type getType() {
        return mType;
    }
}