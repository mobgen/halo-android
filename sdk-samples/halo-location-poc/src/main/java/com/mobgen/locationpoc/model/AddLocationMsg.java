package com.mobgen.locationpoc.model;

/**
 * Created by f.souto.gonzalez on 01/06/2017.
 */

public class AddLocationMsg {

    String roomSelection;
    String wifiStatus;

    public String getRoomSelection() {
        return roomSelection;
    }

    public void setRoomSelection(String roomSelection) {
        this.roomSelection = roomSelection;
    }

    public String getWifiStatus() {
        return wifiStatus;
    }

    public void setWifiStatus(String wifiStatus) {
        this.wifiStatus = wifiStatus;
    }
}
