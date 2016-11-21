package com.mobgen.halo.android.framework.mock.parser;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.network.client.response.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Converts from an input stream to an object.
 */
public class ConverterIntegerInstrument implements Parser<InputStream, Object> {

    /**
     * The type to make the conversion.
     */
    private Type mType;

    /**
     * The constructor for the converter.
     *
     * @param type The type to convert to.
     */
    public ConverterIntegerInstrument(@NonNull Type type) {
        mType = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object convert(InputStream value) throws IOException {
        throw new IOException();
    }
}
