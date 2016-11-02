package com.mobgen.halo.android.sdk.core.internal.parser;

import android.support.annotation.Keep;

import com.bluelinelabs.logansquare.ConverterUtils;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.LongBasedTypeConverter;
import com.mobgen.halo.android.framework.network.client.response.Parser;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Date;

/**
 * Logan square factory used to convert.
 */
@Keep
public class LoganSquareParserFactory extends Parser.Factory {

    /**
     * Creates the logan square factory.
     *
     * @return The logan square factory.
     */
    public static LoganSquareParserFactory create() {
        LoganSquare.registerTypeConverter(Date.class, new LongBasedTypeConverter<Date>() {
            @Override
            public Date getFromLong(long date) {
                return new Date(date);
            }

            @Override
            public long convertToLong(Date object) {
                return object.getTime();
            }
        });
        return new LoganSquareParserFactory();
    }

    @Override
    public Parser<InputStream, ?> deserialize(Type type) {
        Parser<InputStream, ?> converter = null;
        if (ConverterUtils.isSupported(type)) {
            converter = new FromLoganJsonConverter(type);
        }
        return converter;
    }

    @Override
    public Parser<?, String> serialize(Type type) {
        Parser<?, String> converter = null;
        if (ConverterUtils.isSupported(type)) {
            converter = new ToLoganJsonConverter(type);
        }
        return converter;
    }
}
