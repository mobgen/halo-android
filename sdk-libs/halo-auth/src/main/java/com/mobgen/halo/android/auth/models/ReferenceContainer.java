package com.mobgen.halo.android.auth.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by f.souto.gonzalez on 19/06/2017.
 */

/**
 * Reference Container to store on the identified user pocket.
 */
@JsonObject
@Keep
public class ReferenceContainer implements Parcelable {

    @JsonField(name = "referenceData")
    List<String> mReference;

    @JsonField(name = "referenceName")
    String mName;

    /**
     * Create a new instance.
     *
     * @param builder The reference container builder.
     */
    private ReferenceContainer(@NonNull ReferenceContainer.Builder builder) {
        this.mName = builder.mName;
        this.mReference = builder.mReference;
    }


    protected ReferenceContainer() {
        //Empty constructor
    }

    /**
     * Default constructor.
     *
     * @param name      The name of the reference.
     * @param reference The list of references.
     */
    public ReferenceContainer(String name, List<String> reference) {
        this.mReference = reference;
        this.mName = name;
    }


    protected ReferenceContainer(Parcel in) {
        this.mReference = in.createStringArrayList();
        this.mName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeStringList(this.mReference);
        dest.writeString(this.mName);
    }

    /**
     * Get the list of references.
     *
     * @return The list of references.
     */
    @Api(2.4)
    @Nullable
    public List<String> getReferences() {
        return mReference;
    }

    /**
     * Get the name of the references.
     *
     * @return The name of the references.
     */
    @Api(2.4)
    @NonNull
    public String getName() {
        return mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ReferenceContainer> CREATOR = new Creator<ReferenceContainer>() {
        @Override
        public ReferenceContainer createFromParcel(Parcel in) {
            return new ReferenceContainer(in);
        }

        @Override
        public ReferenceContainer[] newArray(int size) {
            return new ReferenceContainer[size];
        }
    };

    /**
     * The builder class.
     */
    @Keep
    public static class Builder implements IBuilder<ReferenceContainer> {

        /**
         * The list of references.
         */
        List<String> mReference;

        /**
         * The name of the reference.
         */
        String mName;

        /**
         * Creates the builder.
         */
        public Builder(String name) {
            mName = name;
        }

        /**
         * Set the list of references.
         *
         * @param reference The references.
         * @return
         */
        @NonNull
        @Api(2.4)
        public Builder references(String... reference) {
            mReference = addToList(mReference, reference);
            return this;
        }

        @NonNull
        @Override
        public ReferenceContainer build() {
            return new ReferenceContainer(this);
        }
    }

    /**
     * Adds something to the given list or creates it returning as a result.
     *
     * @param list  The list of items.
     * @param items The items.
     * @return The list returned or created.
     */
    @NonNull
    public static <T> List<T> addToList(@Nullable List<T> list, @Nullable T[] items) {
        List<T> finalList = list;
        if (items != null && items.length > 0) {
            if (finalList == null) {
                finalList = new ArrayList<>();
            }
            finalList.addAll(Arrays.asList(items));
        }
        return finalList;
    }
}
