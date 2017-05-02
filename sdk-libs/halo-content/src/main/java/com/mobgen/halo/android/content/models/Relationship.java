package com.mobgen.halo.android.content.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.content.utils.HaloContentHelper;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Options to search relationships
 */
@JsonObject
@Keep
public class Relationship implements Parcelable{
    /**
     * All related instances to field name
     */
    private static final String ALL_RELATED_INSTANCES = "*";
    /**
     * The field name with relationship
     */
    @NonNull
    @JsonField(name = "fieldName")
    String mFieldName;

    /**
     * The instance ids that will be brought.
     */
    @NonNull
    @JsonField(name = "instanceIds")
    List<String> mInstanceIds;

    protected Relationship(){
    }

    /**
     * private constructor to fromCursor relationship.
     *
     * @param builder The builder used to fromCursor the original object.
     */
    protected Relationship(@NonNull Relationship.Builder builder) {
        mFieldName = builder.mFieldName;
        mInstanceIds = builder.mInstanceIds;
    }

    private Relationship(@NonNull String fieldName, @NonNull String ... instanceIds){
        mFieldName = fieldName;
        mInstanceIds = HaloContentHelper.addToList(mInstanceIds,instanceIds);
    }

    /**
     * Create a relationship for all
     *
     * @param fieldName The field name.
     * @return
     */
    @Api(2.22)
    public static Relationship createForAll(@NonNull String fieldName){
        return new Relationship(fieldName, new String[]{ALL_RELATED_INSTANCES});
    }

    /**
     * Create a relationship
     *
     * @param fieldName The field name.
     * @param instanceIds The instance ids.
     * @return
     */
    @Api(2.22)
    public static Relationship create(@NonNull String fieldName, @NonNull String ... instanceIds) {
        return new Relationship(fieldName, instanceIds);
    }

    protected Relationship(Parcel in) {
        mFieldName = in.readString();
        mInstanceIds = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFieldName);
        dest.writeStringList(mInstanceIds);
    }

    public static final Creator<Relationship> CREATOR = new Creator<Relationship>() {
        @Override
        public Relationship createFromParcel(Parcel in) {
            return new Relationship(in);
        }

        @Override
        public Relationship[] newArray(int size) {
            return new Relationship[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Creates a new builder.
     *
     * @return The builder created.
     */
    @Keep
    @Api(2.22)
    @NonNull
    public static Relationship.Builder builder() {
        return new Relationship.Builder();
    }

    /**
     * Get the current fieldname.
     *
     * @return The field name
     */
    @Api(2.22)
    @NonNull
    public String getFieldName() {
        return mFieldName;
    }

    /**
     * Get the instances ids
     *
     * @return The instances ids
     */
    @Api(2.22)
    @NonNull
    public List<String> getInstanceIds() {
        return mInstanceIds;
    }


    /**
     * The builder for the options item.
     */
    @Keep
    public static class Builder implements IBuilder<Relationship> {

        /**
         * The field name with relationship
         */
        private String mFieldName;

        /**
         * The instance ids that will be brought.
         */
        private List<String> mInstanceIds;


        /**
         * Default builder creation.
         */
        protected Builder() {
        }

        /**
         * Search the field specified
         *
         * @param field The field to search.
         * @return The current builder.
         */
        @Keep
        @Api(2.22)
        @NonNull
        public Relationship.Builder fieldName(@Nullable String field) {
            mFieldName = field;
            return this;
        }

        /**
         * The instance ids to bring.
         *
         * @param ids The ids of the instances.
         * @return The current builder.
         */
        @Keep
        @Api(2.22)
        @NonNull
        public Relationship.Builder instanceIds(@Nullable String... ids) {
            mInstanceIds = HaloContentHelper.addToList(mInstanceIds, ids);
            return this;
        }

        /**
         * The instance ids to bring.
         *
         * @param id The ids of the instances.
         * @return The current builder.
         */
        @Keep
        @Api(2.22)
        @NonNull
        public Relationship.Builder addInstanceIds(@NonNull String id) {
            if(mInstanceIds!=null) {
                mInstanceIds.add(id);
            } else {
                mInstanceIds = new ArrayList<>();
                mInstanceIds.add(id);
            }
            return this;
        }


        @Keep
        @NonNull
        @Api(2.22)
        @Override
        public Relationship build() {
            return new Relationship(this);
        }
    }

}
