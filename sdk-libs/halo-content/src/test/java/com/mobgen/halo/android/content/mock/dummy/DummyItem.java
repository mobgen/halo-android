package com.mobgen.halo.android.content.mock.dummy;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.mobgen.halo.android.content.annotations.HaloConstructor;
import com.mobgen.halo.android.content.annotations.HaloField;
import com.mobgen.halo.android.content.annotations.HaloQueries;
import com.mobgen.halo.android.content.annotations.HaloQuery;
import com.mobgen.halo.android.content.annotations.HaloSearchable;

import java.util.Date;

@JsonObject
@HaloQueries(queries = {@HaloQuery(
        name = "getData",
        query = "SELECT * FROM DummyItem WHERE foo = @{foo:String}"),
        @HaloQuery(
        name = "insertData",
        query = "INSERT into DummyItem (foo,fooInt,fooBool,dateFoo) VALUES (@{foo:String},@{fooInt:Integer},@{fooBool:Boolean},@{dateFoo:Date},@{dummyObject:DummyObject})"
)})
@HaloSearchable(tableName = "DummyItem" , version = 2)
public class DummyItem {
    @HaloField(index = true, columnName = "foo")
    @JsonField(name = "foo")
    public String foo;

    @JsonField(name = "fooInt")
    public Integer fooInt;

    @JsonField(name = "fooBool")
    public Boolean fooBool;

    @JsonField(name = "dateFoo")
    public Date dateFoo;

    @JsonField(name = "customObject")
    public DummyObject customObject;

    @HaloConstructor(columnNames = {"foo","fooInt","fooBool","dateFoo","customObject"})
    public DummyItem(String fuu, Integer fuuInt, Boolean fuuBool,Date dateFuu,DummyObject dummyObject){
        foo = fuu;
        fooInt = fuuInt;
        fooBool = fuuBool;
        dateFoo = dateFuu;
        customObject = dummyObject;
    }

    public DummyItem(){

    }
}
