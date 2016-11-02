package com.mobgen.halo.android.content.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.NoSuchTypeConverterException;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * The operator for the search.
 */
@Keep
@JsonObject
public class Operator implements SearchExpression {

    /**
     * Equal operator.
     */
    private static final String EQUAL = "=";
    /**
     * Not equal operator.
     */
    private static final String NOT_EQUAL = "!=";
    /**
     * Major operator.
     */
    private static final String MAJOR = ">";
    /**
     * Major or equal operator.
     */
    private static final String MAJOR_EQUAL = ">=";
    /**
     * Minor operator.
     */
    private static final String MINOR = "<";
    /**
     * Minor or equal operator.
     */
    private static final String MINOR_EQUAL = "<=";
    /**
     * In operator.
     */
    private static final String IN = "in";
    /**
     * Not in operator
     */
    private static final String NOT_IN = "!in";

    /**
     * Type null.
     */
    private static final String TYPE_NULL = "null";
    /**
     * Type string.
     */
    private static final String TYPE_STRING = "string";
    /**
     * Type number.
     */
    private static final String TYPE_NUMBER = "number";
    /**
     * Type date.
     */
    private static final String TYPE_DATE = "date";

    /**
     * The operator name of this operator item.
     */
    @JsonField(name = "operation")
    String mOperator;

    /**
     * The property on which this operator acts.
     */
    @JsonField(name = "property")
    String mProperty;

    /**
     * The value for this operator.
     */
    @JsonField(name = "value", typeConverter = OperatorTypeConverter.class)
    Object mValue;

    /**
     * The type of the operator.
     */
    @JsonField(name = "type")
    String mType;

    /**
     * The creator for the operator.
     */
    public static final Parcelable.Creator<Operator> CREATOR = new Parcelable.Creator<Operator>() {
        public Operator createFromParcel(Parcel source) {
            return new Operator(source);
        }

        public Operator[] newArray(int size) {
            return new Operator[size];
        }
    };

    /**
     * The operator constructor for the different types.
     *
     * @param property The property of the type.
     * @param operator The operator name.
     * @param value    The value.
     */
    private Operator(@NonNull String property, @NonNull String operator, @Nullable Object value) {
        mProperty = property;
        mOperator = operator;
        mValue = value;
        if (value == null) {
            mType = TYPE_NULL;
        } else if (value instanceof Date) {
            mType = TYPE_DATE;
        } else if (Number.class.isAssignableFrom(value.getClass())) {
            mType = TYPE_NUMBER;
        } else {
            mType = TYPE_STRING;
        }
    }

    /**
     * Parsing empty constructor.
     */
    protected Operator() {
        //Empty constructor for parsing
    }

    protected Operator(Parcel in) {
        this.mOperator = in.readString();
        this.mProperty = in.readString();
        this.mValue = in.readSerializable();
        this.mType = in.readString();
    }

    /**
     * Equal operation definition.
     *
     * @param property The property to act on.
     * @param data     The data for the operation of this property.
     * @return The operator generated.
     */
    @NonNull
    public static Operator eq(@NonNull String property, @Nullable Object data) {
        return new Operator(property, EQUAL, data);
    }

    /**
     * Not equal operation definition.
     *
     * @param property The property to act on.
     * @param data     The data for the operation of this property.
     * @return The operator generated.
     */
    @NonNull
    public static Operator neq(@NonNull String property, @Nullable Object data) {
        return new Operator(property, NOT_EQUAL, data);
    }

    /**
     * Major operation definition.
     *
     * @param property The property to act on.
     * @param data     The data for the operation of this property.
     * @return The operator generated.
     */
    @NonNull
    public static Operator major(@NonNull String property, @NonNull Object data) {
        return new Operator(property, MAJOR, data);
    }

    /**
     * Major equal operation definition.
     *
     * @param property The property to act on.
     * @param data     The data for the operation of this property.
     * @return The operator generated.
     */
    @NonNull
    public static Operator majorEq(@NonNull String property, @NonNull Object data) {
        return new Operator(property, MAJOR_EQUAL, data);
    }

    /**
     * Minor operation definition.
     *
     * @param property The property to act on.
     * @param data     The data for the operation of this property.
     * @return The operator generated.
     */
    @NonNull
    public static Operator minor(@NonNull String property, @NonNull Object data) {
        return new Operator(property, MINOR, data);
    }

    /**
     * Minor equal operation definition.
     *
     * @param property The property to act on.
     * @param data     The data for the operation of this property.
     * @return The operator generated.
     */
    @NonNull
    public static Operator minorEq(@NonNull String property, @NonNull Object data) {
        return new Operator(property, MINOR_EQUAL, data);
    }

    /**
     * Not in operation definition.
     *
     * @param property The property to act on.
     * @param data     The data for the operation of this property.
     * @return The operator generated.
     */
    @NonNull
    public static Operator nin(@NonNull String property, @NonNull List data) {
        return new Operator(property, NOT_IN, data);
    }

    /**
     * In operation definition.
     *
     * @param property The property to act on.
     * @param data     The data for the operation of this property.
     * @return The operator generated.
     */
    @NonNull
    public static Operator in(@NonNull String property, @NonNull List data) {
        return new Operator(property, IN, data);
    }

    /**
     * Provides the operator symbol for the operator.
     *
     * @return The symbol
     */
    @NonNull
    public String getOperator() {
        return mOperator;
    }

    /**
     * Provides the property name for the operator.
     *
     * @return The property name.
     */
    @NonNull
    public String getProperty() {
        return mProperty;
    }

    /**
     * Provides the value of this operator.
     *
     * @return The value.
     */
    @Nullable
    public Object getValue() {
        return mValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mOperator);
        dest.writeString(this.mProperty);
        dest.writeSerializable((Serializable) this.mValue);
        dest.writeString(this.mType);
    }

    /**
     * Type converter to run over Logan square and make the json conversion
     * properly.
     */
    @Keep
    public static class OperatorTypeConverter implements TypeConverter<Object> {

        @Override
        public Object parse(JsonParser jsonParser) throws IOException {
            //Does not reparse
            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void serialize(Object object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) throws IOException {
            if (object != null) {
                Class<?> clazz = object.getClass();
                try {
                    TypeConverter mapper = LoganSquare.typeConverterFor(clazz);
                    mapper.serialize(object, fieldName, writeFieldNameForObject, jsonGenerator);
                } catch (NoSuchTypeConverterException e) {
                    try {
                        JsonMapper mapper = LoganSquare.mapperFor(object.getClass().getInterfaces()[0]);
                        if (writeFieldNameForObject) {
                            jsonGenerator.writeFieldName(fieldName);
                        }
                        mapper.serialize(object, jsonGenerator, true);
                    } catch (Exception e1) {
                        jsonGenerator.writeObjectField(fieldName, object);
                    }
                }
            } else {
                jsonGenerator.writeObjectField(fieldName, null);
            }
        }
    }
}
