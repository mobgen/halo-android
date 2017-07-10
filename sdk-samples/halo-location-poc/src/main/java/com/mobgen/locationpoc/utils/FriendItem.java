package com.mobgen.locationpoc.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.mobgen.locationpoc.model.Friend;

/**
 * Created by f.souto.gonzalez on 13/06/2017.
 */

public class FriendItem implements ClusterItem {
    private final LatLng mPosition;
    private Friend mFriend;

    public FriendItem(double lat, double lng, Friend friend) {
        mPosition = new LatLng(lat, lng);
        mFriend = friend;
    }

    public Friend getFriend(){
        return mFriend;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
