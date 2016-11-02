package com.mobgen.halo.android.cache.adapters.sqlite.converter;

import com.bluelinelabs.logansquare.ConverterUtils;
import com.bluelinelabs.logansquare.LoganSquare;
import com.mobgen.halo.android.cache.CacheConverter;
import com.mobgen.halo.android.cache.CacheException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class FromLoganJsonConverter implements CacheConverter<String, Object> {

    private Type mType;

    public FromLoganJsonConverter(Type type) {
        mType = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(String value) throws CacheException {
        try {
            if (mType instanceof Class) {
                // Plain object conversion
                return LoganSquare.parse(value, (Class<?>) mType);
            } else if (mType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) mType;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                Type firstType = typeArguments[0];

                Type rawType = parameterizedType.getRawType();
                if (rawType == Map.class) {
                    return LoganSquare.parseMap(value, (Class<?>) typeArguments[1]);
                } else if (rawType == List.class) {
                    return LoganSquare.parseList(value, (Class<?>) firstType);
                } else {
                    // Generics
                    return LoganSquare.parse(value, ConverterUtils.parameterizedTypeOf(mType));
                }
            }
            return null;
        } catch (IOException e) {
            throw new CacheException(e, "Error while parsing one entity to the provided class.");
        }
    }
}
