package com.mobgen.halo.android.auth.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.typeconverters.TypeConverter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by f.souto.gonzalez on 19/06/2017.
 */
@JsonObject
public class ReferenceContainer implements Parcelable {
    //TODO PARCELABLE IMPL AND JAVADOC

    @JsonField(name = "referenceData")
    List<String> mReference;

    @JsonField(name = "referenceName")
    public String mName;

    public ReferenceContainer(String name, List<String> reference){
        this.mName = name;
        this.mReference = reference;
    }


    protected ReferenceContainer() {
        //Empty constructor
    }


    protected ReferenceContainer(Parcel in) {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
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
}
