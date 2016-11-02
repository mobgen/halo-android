package com.mobgen.halo.android.content.models;

import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

/**
 * Query item father of the composite pattern to generate search queries in the Options.
 */
@Keep
public interface SearchExpression extends Parcelable {

    /**
     * Type converter to serialize the search expression in order to perform search
     * queries to the search api.
     */
    @Keep
    class SearchExpressionTypeConverter implements TypeConverter<SearchExpression> {

        /**
         * Trigger mapper.
         */
        private final JsonMapper<Condition> mConditionJsonMapper = LoganSquare.mapperFor(Condition.class);
        /**
         * Json mapper for the operator.
         */
        private final JsonMapper<Operator> mOperatorJsonMapper = LoganSquare.mapperFor(Operator.class);

        @Override
        public SearchExpression parse(JsonParser jsonParser) throws IOException {
            //This object will never be parsed
            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void serialize(SearchExpression object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) throws IOException {
            if (fieldName != null) {
                jsonGenerator.writeFieldName(fieldName);
            }
            JsonMapper mapper = mapperFor(object);
            if (mapper != null) {
                mapper.serialize(object, jsonGenerator, true);
            } else {
                jsonGenerator.writeObjectField(fieldName, object);
            }
        }

        /**
         * Obtains the mapper based on the type of operation.
         *
         * @param expression The expression to analyze.
         * @return The json mapper.
         */
        @Nullable
        private JsonMapper mapperFor(SearchExpression expression) {
            if (expression instanceof Condition) {
                return mConditionJsonMapper;
            } else if (expression instanceof Operator) {
                return mOperatorJsonMapper;
            } else {
                return null;
            }
        }
    }
}
