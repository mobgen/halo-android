package com.mobgen.halo.android.cache.adapters.sqlite.converter;

import com.bluelinelabs.logansquare.ConverterUtils;
import com.mobgen.halo.android.cache.Cache;
import com.mobgen.halo.android.cache.CacheConverter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public class LoganSquareConverterFactory extends CacheConverter.Factory<String> {

    public static LoganSquareConverterFactory create() {
        return new LoganSquareConverterFactory();
    }

    @Override
    public CacheConverter<?, String> serializeCacheConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Cache cache) {
        CacheConverter<?, String> converter = null;
        if (ConverterUtils.isSupported(type)) {
            converter = new ToLoganJsonConverter(type);
        }
        return converter;
    }

    @Override
    public CacheConverter<String, ?> deserializeCacheConverter(Type type, Annotation[] annotations, Cache cache) {
        CacheConverter<String, ?> converter = null;
        if (ConverterUtils.isSupported(type)) {
            converter = new FromLoganJsonConverter(type);
        }
        return converter;
    }
}
