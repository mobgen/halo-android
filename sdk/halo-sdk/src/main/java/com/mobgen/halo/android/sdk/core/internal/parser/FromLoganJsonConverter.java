package com.mobgen.halo.android.sdk.core.internal.parser;

import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.HaloParserConverterUtils;
import com.bluelinelabs.logansquare.LoganSquare;
import com.mobgen.halo.android.framework.network.client.response.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Converts from an input stream to an object.
 */
public class FromLoganJsonConverter implements Parser<InputStream, Object> {

    /**
     * The type to make the conversion.
     */
    private Type mType;

    /**
     * The constructor for the converter.
     *
     * @param type The type to convert to.
     */
    public FromLoganJsonConverter(@NonNull Type type) {
        mType = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(InputStream value) throws IOException {
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
                return LoganSquare.parse(value, HaloParserConverterUtils.parameterizedTypeOf(mType));
            }
        }
        return null;
    }
}
