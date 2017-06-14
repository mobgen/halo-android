package com.mobgen.locationpoc.model;

import java.util.List;

/**
 * Created by f.souto.gonzalez on 01/06/2017.
 */

public class ObserverMsg {

    private AddLocationMsg addLocationMsg;
    private PositionMsg positionMsg;
    private List<ScanAPResult> scanAPResults;

    public List<ScanAPResult> getScanAPResults() {
        return scanAPResults;
    }

    public void setScanAPResults(List<ScanAPResult> scanAPResults) {
        this.scanAPResults = scanAPResults;
    }

    public AddLocationMsg getAddLocationMsg() {
        return addLocationMsg;
    }

    public void setAddLocationMsg(AddLocationMsg addLocationMsg) {
        this.addLocationMsg = addLocationMsg;
    }

    public PositionMsg getPositionMsg() {
        return positionMsg;
    }

    public void setPositionMsg(PositionMsg positionMsg) {
        this.positionMsg = positionMsg;
    }
}
