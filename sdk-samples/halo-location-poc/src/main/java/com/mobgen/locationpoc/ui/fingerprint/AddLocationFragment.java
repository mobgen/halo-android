package com.mobgen.locationpoc.ui.fingerprint;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobgen.locationpoc.R;
import com.mobgen.locationpoc.model.AddLocationMsg;
import com.mobgen.locationpoc.model.ObserverMsg;
import com.mobgen.locationpoc.receiver.BroadcastObserver;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by f.souto.gonzalez on 01/06/2017.
 */

public class AddLocationFragment extends Fragment implements Observer {

    TextView distance, room;
    String distanceText, roomText;
    boolean fragmentStatus = false;

    public static AddLocationFragment newInstance(BroadcastObserver brodcascastObserver) {

        Bundle args = new Bundle();

        AddLocationFragment fragment = new AddLocationFragment();
        fragment.setArguments(args);

        brodcascastObserver.addObserver(fragment);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.content_add_location, container, false);

        distance = (TextView) rootview.findViewById(R.id.distance);
        room = (TextView) rootview.findViewById(R.id.room);

        if(roomText != null && distanceText != null) {
            room.setText(roomText);
            distance.setText(distanceText);
        }

        return rootview;
    }


    @Override
    public void onResume() {
        super.onResume();
        fragmentStatus = true;
    }

    @Override
    public void onPause() {
        fragmentStatus = false;
        super.onPause();
    }


    @Override
    public void update(Observable o, Object result) {
        //updates from receiver
        if(fragmentStatus) {
            if (result instanceof ObserverMsg) {
                AddLocationMsg addLocationMsg = ((ObserverMsg) result).getAddLocationMsg();
                roomText = addLocationMsg.getRoomSelection();
                distanceText = addLocationMsg.getWifiStatus();
                room.setText(roomText);
                distance.setText(distanceText);
            }
        }
    }
}
