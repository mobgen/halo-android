package com.mobgen.halo.android.content.mock.dummy;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by mobgenimac on 22/3/17.
 */
@JsonObject
public class DummyObjectModel {

    @JsonField(name = "foo")
    public String mFoo;

    public DummyObjectModel(String foo){
        mFoo = foo;
    }

    public DummyObjectModel(){

    }
}
