package com.mobgen.halo.android.framework.network.client.response;

import com.mobgen.halo.android.framework.common.annotations.Api;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Parser interface for the framework. It mimics the retrofit parser
 * interface to create the info
 */
public interface Parser<F, T> {

    /**
     * Converts a value from one type to another.
     * @param value The value to convert.
     * @return The value converted.
     * @throws IOException Error while converting.
     */
    @Api(2.0)
    T convert(F value) throws IOException;

    /**
     * Factory to make the conversions.
     */
    abstract class Factory {

        /**
         * Deserialize.
         * @param type The type that will be deserialized.
         * @return The parser.
         */
        @Api(2.0)
        public Parser<InputStream, ?> deserialize(Type type) {
            return null;
        }

        /**w
         * Serialize converter.
         * @param type The type that will be serialized.
         * @return The parser.
         */
        @Api(2.0)
        public Parser<?, String> serialize(Type type) {
            return null;
        }
    }
}