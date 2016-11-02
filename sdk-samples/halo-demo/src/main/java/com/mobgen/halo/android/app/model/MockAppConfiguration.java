package com.mobgen.halo.android.app.model;


import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

@JsonObject
public class MockAppConfiguration {

    @JsonField(name = "ToolbarColor")
    String mToolbarColor;

    @JsonField(name = "MenuColor")
    String mMenuColor;

    public String getToolbarColor() {
        return mToolbarColor;
    }

    public String getMenuColor() {
        return mMenuColor;
    }
}
