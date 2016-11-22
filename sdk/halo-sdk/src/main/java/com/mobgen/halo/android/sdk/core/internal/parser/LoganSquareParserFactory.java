package com.mobgen.halo.android.sdk.core.internal.parser;

import android.support.annotation.Keep;

import com.bluelinelabs.logansquare.ConverterUtils;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.mobgen.halo.android.framework.network.client.response.Parser;

import java.io.IOException;
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
        LoganSquare.registerTypeConverter(Date.class, new DateConverterSerializer());
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

    /**
     * Date converter that makes sure the conversion between dates is done properly.
     */
    private static class DateConverterSerializer implements TypeConverter<Date> {

        @Override
        public Date parse(JsonParser jsonParser) throws IOException {
            String nullableDate = jsonParser.getValueAsString();
            return nullableDate == null ? null : new Date(Long.parseLong(nullableDate));
        }

        @Override
        public void serialize(Date date, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) throws IOException {
            if (fieldName != null) {
                jsonGenerator.writeNumberField(fieldName, date.getTime());
            } else {
                jsonGenerator.writeNumber(date.getTime());
            }
        }
    }
}
