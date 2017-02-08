package com.mobgen.halo.android.sdk.core.management.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.List;

/**
 * The halo module field rules to apply
 */
@JsonObject
public class HaloModuleFieldRule implements Parcelable{

    public HaloModuleFieldRule(){

    }

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


    protected HaloModuleFieldRule(Parcel in) {
        mRule = in.readString();
        mError = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mRule);
        dest.writeString(mError);
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
