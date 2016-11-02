package com.mobgen.halo.android.content.models;

import android.os.Parcel;
import android.support.annotation.Keep;

/**
 * Represents the parenthesis opened or closed.
 */
@Keep
public class Parenthesis implements SearchExpression {

    /**
     * Opened or closed.
     */
    private boolean mOpened;

    public static final Creator<Parenthesis> CREATOR = new Creator<Parenthesis>() {
        public Parenthesis createFromParcel(Parcel source) {
            return new Parenthesis(source);
        }

        public Parenthesis[] newArray(int size) {
            return new Parenthesis[size];
        }
    };

    /**
     * Constructor for the parenthesis.
     *
     * @param opened True if opened, false otherwise.
     */
    public Parenthesis(boolean opened) {
        mOpened = opened;
    }

    protected Parenthesis(Parcel in) {
        this.mOpened = in.readByte() != 0;
    }

    /**
     * Checks if the parenthesis is opened.
     *
     * @return True if the parenthesis are opened. False otherwise
     */
    public boolean isOpened() {
        return mOpened;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(mOpened ? (byte) 1 : (byte) 0);
    }
}
