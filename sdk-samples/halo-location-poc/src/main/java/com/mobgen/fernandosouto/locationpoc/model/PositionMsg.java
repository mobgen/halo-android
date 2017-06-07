package com.mobgen.fernandosouto.locationpoc.model;

import android.location.Location;

/**
 * Created by f.souto.gonzalez on 01/06/2017.
 */

public class PositionMsg {

    String detectedName;

    public String getDetectedName() {
        return detectedName;
    }

    public void setDetectedName(String detectedName) {
        this.detectedName = detectedName;
    }

    public boolean isChangeStatus() {
        return changeStatus;
    }

    public void setChangeStatus(boolean changeStatus) {
        this.changeStatus = changeStatus;
    }

    boolean changeStatus;
}
