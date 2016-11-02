package com.mobgen.halo.android.cache;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

public interface CacheConverter<F, T> {
    T convert(F value) throws CacheException;

    abstract class Factory<C> {

        public CacheConverter<C, ?> deserializeCacheConverter(Type type, Annotation[] annotations, Cache<C> cache) {
            return null;
        }

        public CacheConverter<?, C> serializeCacheConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Cache<C> cache) {
            return null;
        }
    }
}