package com.mobgen.locationpoc.model;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;

/**
 * Created by f.souto.gonzalez on 29/05/2017.
 */
@JsonObject
public class GsmAps {
    @JsonField( name = "postal")
    int postal;
    @JsonField( name = "country")
    int country;
    @JsonField(name = "signal")
    double signalStrength;

    public GsmAps(int postal, int country, double signalStrength){
        this.postal = postal;
        this.country = country;
        this.signalStrength = signalStrength;
    }

    public GsmAps(){}

    public int getPostal() {
        return postal;
    }

    public void setPostal(int postal) {
        this.postal = postal;
    }

    public int getCountry() {
        return country;
    }

    public void setCountry(int country) {
        this.country = country;
    }
}
