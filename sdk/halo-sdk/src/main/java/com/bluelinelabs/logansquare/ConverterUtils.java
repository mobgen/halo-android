package com.bluelinelabs.logansquare;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @hide Converter helper that access some protected methods of the LoganSquare library so
 * we can check if the given type is available to work with it.
 */
public final class ConverterUtils {

    /**
     * Private constructor for the converter.
     */
    private ConverterUtils() {
        throw new AssertionError();
    }

    /**
     * Checks if a given type is supported by logan square.
     *
     * @param type The type to check.
     * @return True if it is supported. False otherwise.
     */
    public static boolean isSupported(Type type) {
        // Check ordinary Class
        if (type instanceof Class && !LoganSquare.supports((Class) type)) {
            return false;
        }

        // Check LoganSquare's CacheType
        if (type instanceof ParameterizedType && !LoganSquare.supports((ParameterizedType) type)) {
            return false;
        }

        // Check target types of java.lang.reflect.CacheType
        if (type instanceof java.lang.reflect.ParameterizedType) {
            java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) type;
            Type[] typeArguments = pt.getActualTypeArguments();
            Type firstType = typeArguments[0];

            Type rawType = pt.getRawType();
            if (rawType == Map.class) {
                // LoganSquare only handles Map objects with String keys and supported types
                Type secondType = typeArguments[1];
                if (firstType != String.class || !isSupported(secondType)) {
                    return false;
                }

            } else if (rawType == List.class) {
                // LoganSquare only handles List objects of supported types
                if (!isSupported(firstType)) {
                    return false;
                }

            } else {
                // Check for generics
                return LoganSquare.supports(parameterizedTypeOf(type));
            }
        }

        return true;
    }

    /**
     * Creates a parameterized type of a given type.
     *
     * @param type the type to parameterize.
     * @return The parameterized type created.
     */
    public static ParameterizedType parameterizedTypeOf(Type type) {
        return new ParameterizedType.ConcreteParameterizedType(type);
    }
}