package com.mobgen.halo.android.content.mock.dummy;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.content.annotations.HaloConstructor;
import com.mobgen.halo.android.content.annotations.HaloQueries;
import com.mobgen.halo.android.content.annotations.HaloQuery;
import com.mobgen.halo.android.content.annotations.HaloSearchable;

import java.util.Date;

/**
 * Created by mobgenimac on 14/2/17.
 */
@HaloSearchable(tableName = "DummyObject" , version = 1)
@JsonObject
public class DummyObject {
    @JsonField(name = "field")
    public String field;

    @HaloConstructor(
            columnNames = {"field"}
    )
    public DummyObject(String fild){
        field = fild;
    }

    public DummyObject(){

    }
}
