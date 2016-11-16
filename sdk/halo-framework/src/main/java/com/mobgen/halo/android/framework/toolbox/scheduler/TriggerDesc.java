package com.mobgen.halo.android.framework.toolbox.scheduler;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Transportable trigger description class
 */
final class TriggerDesc implements Parcelable {
    /**
     * The creator.
     */
    public static final Creator<TriggerDesc> CREATOR = new Creator<TriggerDesc>() {
        public TriggerDesc createFromParcel(Parcel source) {
            return new TriggerDesc(source);
        }

        public TriggerDesc[] newArray(int size) {
            return new TriggerDesc[size];
        }
    };
    /**
     * The identity.
     */
    final String mIdentity;
    /**
     * Trigger is satisfied or not.
     */
    boolean mSatisfy;

    /**
     * The trigger description.
     *
     * @param ident The identity.
     */
    public TriggerDesc(String ident) {
        this.mIdentity = ident;
    }

    /**
     * Parcelable constructor.
     *
     * @param in the parcel.
     */
    private TriggerDesc(Parcel in) {
        mIdentity = in.readString();
        mSatisfy = in.readByte() != 0;
    }

    /**
     * Sets the satisfy property.
     *
     * @param satisfy True if it was satisfied.
     */
    public void satisfy(boolean satisfy) {
        this.mSatisfy = satisfy;
    }

    @Override
    public String toString() {
        return "Condition{" +
                "ident='" + mIdentity + '\'' +
                ", satisfy=" + mSatisfy +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mIdentity);
        dest.writeByte(mSatisfy ? (byte) 1 : (byte) 0);
    }
}
