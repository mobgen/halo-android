package com.mobgen.halo.android.sdk.mock.dummy;

import android.support.annotation.Nullable;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

import java.util.Date;

@JsonObject
public class DummyDateItem {

    @JsonField(name = "date")
    Date mDate;

    public DummyDateItem(){}

    public DummyDateItem(@Nullable Date date) {
        mDate = date;
    }

    public Date getDate() {
        return mDate;
    }
}
