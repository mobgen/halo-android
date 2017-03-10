package com.mobgen.halo.android.app.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by fernando souto on 23/2/17.
 */
@JsonObject
public class TestLocalizedText {

    @JsonField(name = "Title")
    public LocalizedText mTtitle;

    public TestLocalizedText(LocalizedText title){
        mTtitle = title;
    }

    public TestLocalizedText(){}

    public void setmTtitle(LocalizedText mTtitle) {
        this.mTtitle = mTtitle;
    }

    public LocalizedText getmTtitle() {
        return mTtitle;
    }

}
