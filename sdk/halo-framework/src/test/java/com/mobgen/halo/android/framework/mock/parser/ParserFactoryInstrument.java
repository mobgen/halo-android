package com.mobgen.halo.android.framework.mock.parser;

import android.support.annotation.Keep;

import com.mobgen.halo.android.framework.network.client.response.Parser;

import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Test factory used to convert.
 */
@Keep
public class ParserFactoryInstrument extends Parser.Factory {

    /**
     * Creates the test instrument factory.
     *
     * @return The test instrument  factory.
     */
    public static ParserFactoryInstrument create() {
        return new ParserFactoryInstrument();
    }

    @Override
    public Parser<InputStream, ?> deserialize(Type type) {
        Parser<InputStream, ?> converter = null;
        if(type.equals(Integer.class)) {
            converter = new ConverterIntegerInstrument(type);
        }
        else{
            converter = new ConverterStringInstrument(type);
        }
        return converter;
    }
}
