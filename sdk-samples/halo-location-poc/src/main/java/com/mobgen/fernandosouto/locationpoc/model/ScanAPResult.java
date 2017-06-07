package com.mobgen.fernandosouto.locationpoc.model;


import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import java.util.List;

/**
 * Created by f.souto.gonzalez on 29/05/2017.
 */
@JsonObject
public class ScanAPResult {
    @JsonField(name = "wifiaps")
    List<WifiAps> wifiAps;
    @JsonField(name = "gsmaps")
    List<GsmAps> gsmAPs;
    @JsonField(name = "roomname")
    String roomName;
    @JsonField(name = "location")
    String location;

    public ScanAPResult(){}

    public ScanAPResult(List<WifiAps> wifiAps, List<GsmAps> gsmAps, String roomName, String location) {
        this.wifiAps = wifiAps;
        this.gsmAPs = gsmAps;
        this.roomName = roomName;
        this.location = location;
    }

    public String getRoomName() {
        return roomName;
    }

    public List<WifiAps> getWifiAps(){
        return wifiAps;
    }
}
