package com.mobgen.locationpoc.receiver;

import java.util.Observable;

/**
 * Created by f.souto.gonzalez on 01/06/2017.
 */
public class BroadcastObserver extends Observable {
    private void triggerObservers(Object data) {
        setChanged();
        notifyObservers(data);
    }

    public void change(Object data) {
        triggerObservers(data);
    }
}