package com.mobgen.halo.android.social.dummy;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class DummyItem {

    @JsonField(name = "foo")
    public String foo;
}
