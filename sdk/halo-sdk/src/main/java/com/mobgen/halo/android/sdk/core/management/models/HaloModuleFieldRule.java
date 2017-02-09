package com.mobgen.halo.android.sdk.core.management.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.framework.common.annotations.Api;

import java.util.List;

/**
 * The halo module field rules valitadions to apply on each moduel field type.
 */
@Keep
@JsonObject
public class HaloModuleFieldRule implements Parcelable{

    /**

     *  The rule to apply.
     */
    @JsonField(name="rule")
    String mRule;

    /**
     * The params of the rule.
     */
    @JsonField(name="params")
    List<String> mParams;

    /**
     * The error type.
     */
    @JsonField(name="error")
    String mError;

    /**
     * Parsing empty constructor.
     */
    public HaloModuleFieldRule(){
        //Empty constructor for parsing
    }

    public HaloModuleFieldRule(String mRule, List<String> mParams, String mError) {
        this.mRule = mRule;
        this.mParams = mParams;
        this.mError = mError;
    }

    /**
     * Provides the validation rule of the field.
     * @return The validation rule.
     */
    @NonNull
    @Api(2.3)
    public String getRule() {
        return mRule;
    }


    protected HaloModuleFieldRule(Parcel in) {
        mRule = in.readString();
        mError = in.readString();
        mParams = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRule);
        dest.writeString(mError);
        dest.writeStringList(mParams);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HaloModuleFieldRule> CREATOR = new Creator<HaloModuleFieldRule>() {
        @Override
        public HaloModuleFieldRule createFromParcel(Parcel in) {
            return new HaloModuleFieldRule(in);
        }

        @Override
        public HaloModuleFieldRule[] newArray(int size) {
            return new HaloModuleFieldRule[size];
        }
    };
}
