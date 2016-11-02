package com.mobgen.halo.android.cache.adapters.sqlite.converter;

import com.bluelinelabs.logansquare.ConverterUtils;
import com.bluelinelabs.logansquare.LoganSquare;
import com.mobgen.halo.android.cache.CacheConverter;
import com.mobgen.halo.android.cache.CacheException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class ToLoganJsonConverter implements CacheConverter<Object, String> {

    private Type mType;

    public ToLoganJsonConverter(Type type) {
        mType = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String convert(Object value) throws CacheException {
        try {
            if (mType instanceof java.lang.reflect.ParameterizedType) {
                java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) mType;
                Type rawType = pt.getRawType();
                if (rawType != List.class && rawType != Map.class) {
                    return LoganSquare.serialize(value, ConverterUtils.parameterizedTypeOf(mType));
                }
            }
            return LoganSquare.serialize(value);
        } catch (IOException e) {
            throw new CacheException(e, "Error while serializing the entity");
        }
    }
}
