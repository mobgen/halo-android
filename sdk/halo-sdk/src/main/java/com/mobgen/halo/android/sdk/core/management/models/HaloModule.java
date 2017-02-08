package com.mobgen.halo.android.sdk.core.management.models;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.sdk.core.internal.storage.HaloManagerContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Modules are every component built in halo. Those components can be of different types such as
 * general content, push notifications or loyalty. This class represents those modules.
 */
@Keep
@JsonObject
public class HaloModule implements Parcelable {


    /**
     * Json converter to transform the json that comes to a json object.
     */
    @Keep
    public static class JSONArrayObjectConverter implements TypeConverter<JSONArray> {

        /**
         * The mapper for objects.
         */
        private static final JsonMapper<Object> mapper = LoganSquare.mapperFor(Object.class);

        @Override
        public JSONArray parse(JsonParser jsonParser) throws IOException {
            List<HaloModuleField> haloModuleFields = (List<HaloModuleField>) mapper.parse(jsonParser);
            return haloModuleFields != null ? new JSONArray(haloModuleFields) : null;
        }

        @Override
        public void serialize(JSONArray object, String fieldName, boolean writeFieldNameForObject, JsonGenerator jsonGenerator) throws IOException {
        }
    }

    public static final Creator<HaloModule> CREATOR = new Creator<HaloModule>() {
        public HaloModule createFromParcel(Parcel source) {
            return new HaloModule(source);
        }

        public HaloModule[] newArray(int size) {
            return new HaloModule[size];
        }
    };
    /**
     * The id of the customer that created this middleware.
     */
    @JsonField(name = "customer")
    Integer mCustomerId;
    /**
     * The unique id of this middleware.
     */
    @JsonField(name = "id")
    String mId;
    /**
     * The name of the middleware.
     */
    @JsonField(name = "name")
    String mName;
    /**
     * Checks if this middleware is enabled.
     */
    @JsonField(name = "enabled")
    Boolean mEnabled;
    /**
     * Date with the last moment this middleware was updated.
     */
    @JsonField(name = "updatedAt")
    Date mLastUpdate;
    /**
     * Date that tells you when this middleware was created.
     */
    @JsonField(name = "createdAt")
    Date mCreationDate;
    /**
     * Internal id usually used to bring the data that is stored in this middleware.
     */
    @JsonField(name = "internalId")
    String mInternalId;
    /**
     * Checks if this middleware is single or not. This field is intended to be used only in general
     * content modules.
     */
    @JsonField(name = "isSingle")
    Boolean mIsSingle;
    /**
     *
     */
    @JsonField(name = "fields", typeConverter = JSONArrayObjectConverter.class)
    JSONArray mFields;

    /**
     * Parsing empty constructor.
     */
    public HaloModule() {
        //Empty constructor for parsing
    }

    /**
     * Remote module constructor.
     *
     * @param customerId   The customer id.
     * @param id           The id.
     * @param name         The name of the module.
     * @param enabled      True if enabled, false otherwise.
     * @param lastUpdate   Last updated date.
     * @param creationDate Creation date.
     * @param internalId   The internal id.
     * @param isSingle     True if the module is a single module type.
     */
    @Api(1.2)
    public HaloModule(@NonNull Integer customerId, @NonNull String id, @NonNull String name, boolean enabled, @NonNull Date creationDate, @Nullable Date lastUpdate, @NonNull String internalId, boolean isSingle, JSONArray fields) {
        mCustomerId = customerId;
        mId = id;
        mName = name;
        mEnabled = enabled;
        mLastUpdate = lastUpdate;
        mCreationDate = creationDate;
        mInternalId = internalId;
        mIsSingle = isSingle;
        mFields = fields;
    }

    /**
     * Constructor with the parcel.
     *
     * @param in The parcel where we can write.
     */
    protected HaloModule(Parcel in) {
        this.mCustomerId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.mId = in.readString();
        this.mName = in.readString();
        this.mEnabled = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpMLastUpdate = in.readLong();
        this.mLastUpdate = tmpMLastUpdate == -1 ? null : new Date(tmpMLastUpdate);
        long tmpMCreationDate = in.readLong();
        this.mCreationDate = tmpMCreationDate == -1 ? null : new Date(tmpMCreationDate);
        this.mInternalId = in.readString();
        this.mIsSingle = (Boolean) in.readValue(Boolean.class.getClassLoader());
        try {
            String fields = in.readString();
            this.mFields = fields != null ? new JSONArray(fields): null;
        } catch (JSONException e) {
            Halog.e(getClass(), "The fields of the module with id " + mId + "could not be parsed on the parceling op.");
        }
    }

    /**
     * Creates a single item from a raw.
     *
     * @param cursor               The raw.
     * @return The remote middleware created.
     */
    @Api(1.0)
    @NonNull
    public static HaloModule create(@NonNull Cursor cursor) {
        HaloModule remoteModule = new HaloModule();
        remoteModule.mCustomerId = cursor.getInt(cursor.getColumnIndex(HaloManagerContract.RemoteModules.CUSTOMER));
        remoteModule.mId = cursor.getString(cursor.getColumnIndex(HaloManagerContract.RemoteModules.ID));
        remoteModule.mName = cursor.getString(cursor.getColumnIndex(HaloManagerContract.RemoteModules.NAME));
        remoteModule.mEnabled = cursor.getLong(cursor.getColumnIndex(HaloManagerContract.RemoteModules.ENABLED)) == 1;
        remoteModule.mIsSingle = cursor.getLong(cursor.getColumnIndex(HaloManagerContract.RemoteModules.SINGLE)) == 1;
        if (!cursor.isNull(cursor.getColumnIndex(HaloManagerContract.RemoteModules.UPDATED_AT))) {
            remoteModule.mLastUpdate = new Date(cursor.getLong(cursor.getColumnIndex(HaloManagerContract.RemoteModules.UPDATED_AT)));
        }
        if (!cursor.isNull(cursor.getColumnIndex(HaloManagerContract.RemoteModules.CREATED_AT))) {
            remoteModule.mCreationDate = new Date(cursor.getLong(cursor.getColumnIndex(HaloManagerContract.RemoteModules.CREATED_AT)));
        }
        remoteModule.mInternalId = cursor.getString(cursor.getColumnIndex(HaloManagerContract.RemoteModules.INTERNAL_ID));
        return remoteModule;
    }

    /**
     * Creates a list from a raw.
     *
     * @param cursor The raw to read from.
     * @param close  True if the raw should be closed.
     * @return The list from a raw.
     */
    @Api(1.0)
    @NonNull
    public static List<HaloModule> fromCursor(@Nullable Cursor cursor, boolean close) {
        List<HaloModule> instances = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    instances.add(create(cursor));
                } while (cursor.moveToNext());
            }
            if (close) {
                cursor.close();
            }
        }
        return instances;
    }

    /**
     * Provides the content values related to this model to store it in the database.
     *
     * @return The content values.
     */
    @Api(1.0)
    @NonNull
    public ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(HaloManagerContract.RemoteModules.CUSTOMER, mCustomerId);
        values.put(HaloManagerContract.RemoteModules.ID, mId);
        values.put(HaloManagerContract.RemoteModules.INTERNAL_ID, mInternalId);
        values.put(HaloManagerContract.RemoteModules.NAME, mName);
        values.put(HaloManagerContract.RemoteModules.ENABLED, mEnabled);
        values.put(HaloManagerContract.RemoteModules.SINGLE, mIsSingle);
        values.put(HaloManagerContract.RemoteModules.UPDATED_AT, mLastUpdate != null ? mLastUpdate.getTime() : null);
        values.put(HaloManagerContract.RemoteModules.CREATED_AT, mCreationDate != null ? mCreationDate.getTime() : null);
        return values;
    }

    /**
     * Provides the customer that created this middleware.
     *
     * @return The customer id.
     */
    @Api(1.0)
    @NonNull
    public Integer getCustomerId() {
        return mCustomerId;
    }

    /**
     * Provides the unique id of the middleware.
     *
     * @return The id of the middleware.
     */
    @Api(1.0)
    @NonNull
    public String getId() {
        return mId;
    }

    /**
     * Provides the name of the middleware.
     *
     * @return The name of the middleware.
     */
    @Api(1.0)
    @NonNull
    public String getName() {
        return mName;
    }

    /**
     * Checks if this middleware is enabled.
     *
     * @return True if the middleware is enabled, false otherwise.
     */
    @Api(1.0)
    @NonNull
    public Boolean isEnabled() {
        return mEnabled;
    }

    /**
     * The date when this middleware was last updated.
     *
     * @return The date of the last update.
     */
    @Api(1.0)
    @Nullable
    public Date getLastUpdate() {
        return mLastUpdate;
    }

    /**
     * Provides the creation date for this middleware.
     *
     * @return The creation date of this middleware.
     */
    @Api(1.0)
    @NonNull
    public Date getCreationDate() {
        return mCreationDate;
    }

    /**
     * Provides the internal id of the middleware.
     *
     * @return The internal id of the middleware.
     */
    @Api(1.0)
    public String getInternalId() {
        return mInternalId;
    }

    /**
     * Provides the fields meta-data of the module
     *
     * @return The fields meta-data of the module
     */
    @Api(2.3)
    public JSONArray getFields() {
        return mFields;
    }

    /**
     * Provides the information of this middleware being a single instance.
     *
     * @return True if it is a single instance, false otherwise. It can be null if this middleware
     * is not a general content middleware.
     */
    @Api(1.0)
    public boolean isSingleItemInstance() {
        return mIsSingle != null && mIsSingle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.mCustomerId);
        dest.writeString(this.mId);
        dest.writeString(this.mName);
        dest.writeValue(this.mEnabled);
        dest.writeLong(mLastUpdate != null ? mLastUpdate.getTime() : -1);
        dest.writeLong(mCreationDate != null ? mCreationDate.getTime() : -1);
        dest.writeString(this.mInternalId);
        dest.writeValue(this.mIsSingle);
        dest.writeString(mFields != null ? this.mFields.toString() : null);
    }
}
