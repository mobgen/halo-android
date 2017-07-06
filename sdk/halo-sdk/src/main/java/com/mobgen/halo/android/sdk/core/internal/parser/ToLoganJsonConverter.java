package com.mobgen.halo.android.sdk.core.internal.parser;

import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.HaloParserConverterUtils;
import com.bluelinelabs.logansquare.LoganSquare;
import com.mobgen.halo.android.framework.network.client.response.Parser;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Converts objects into string based on the parser of LoganSquare.
 * Implements the factory to do so.
 */
public class ToLoganJsonConverter implements Parser<Object, String> {

    /**
     * The data type for this factory.
     */
    private Type mType;

    /**
     * Converts to logan json.
     *
     * @param type The type to convert.
     */
    public ToLoganJsonConverter(@NonNull Type type) {
        mType = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String convert(@NonNull Object value) throws IOException {
        if (mType instanceof java.lang.reflect.ParameterizedType) {
            java.lang.reflect.ParameterizedType pt = (java.lang.reflect.ParameterizedType) mType;
            Type rawType = pt.getRawType();
            if (rawType != List.class && rawType != Map.class) {
                return LoganSquare.serialize(value, HaloParserConverterUtils.parameterizedTypeOf(mType));
            }
        }
        return LoganSquare.serialize(value);
    }
}
