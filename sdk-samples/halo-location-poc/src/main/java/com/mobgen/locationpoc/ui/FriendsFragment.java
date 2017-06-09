package com.mobgen.locationpoc.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.search.SearchQueryBuilderFactory;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.locationpoc.R;
import com.mobgen.locationpoc.model.Friend;
import com.mobgen.locationpoc.model.ObserverMsg;
import com.mobgen.locationpoc.model.PositionMsg;
import com.mobgen.locationpoc.receiver.AccessPointReceiver;
import com.mobgen.locationpoc.receiver.BroadcastObserver;
import com.mobgen.locationpoc.utils.LocationUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by f.souto.gonzalez on 01/06/2017.
 */

public class FriendsFragment extends Fragment implements Observer, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private FriendsFragment mPositionFragment;
    private SupportMapFragment mMapFragment;

    public static FriendsFragment newInstance(BroadcastObserver broadcastObserver) {

        Bundle args = new Bundle();
        FriendsFragment fragment = new FriendsFragment();
        fragment.setArguments(args);

        broadcastObserver.addObserver(fragment);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.content_position, container, false);

        mPositionFragment = this;

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit();

        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                setUpMapIfNeeded();
            }
        });

        return rootview;
    }

    /**
     * Setup the map with initial markers.
     */
    public void setUpMapIfNeeded() {
        if (ActivityCompat.checkSelfPermission(mPositionFragment.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mPositionFragment.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(false);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(final Marker marker) {

                View v = getActivity().getLayoutInflater().inflate(R.layout.window_layout, null);

                TextView room = (TextView) v.findViewById(R.id.tv_room);
                TextView userName = (TextView) v.findViewById(R.id.tv_name);
                TextView userMail = (TextView) v.findViewById(R.id.tv_mail);
                final ImageView imageView = (ImageView) v.findViewById(R.id.iv_photo);
                if (marker.getTag() != null) {
                    Friend friend = (Friend) marker.getTag();
                    room.setText(getContext().getString(R.string.room_info) + " " + friend.getRoom());
                    userName.setText(friend.getUserName());
                    userMail.setText(friend.getUserMail());
                    Picasso.with(getContext()).load(friend.getUserPhoto()).into(imageView);
                }

                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
        Location location = LocationUtils.getLocation(getContext());
        if (location != null) {
            LatLng myLatLng = new LatLng(location.getLatitude(),
                    location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 25.0f));
        }
        addFriendPoints();
    }

    private void addFriendPoints() {
        final List<String> emailsStored = new ArrayList<>();
        SearchQuery options = SearchQueryBuilderFactory.getPublishedItems(AccessPointReceiver.MODULE_NAME_FRIENDS, AccessPointReceiver.MODULE_NAME_FRIENDS)
                .populateAll()
                .build();

        HaloContentApi.with(MobgenHaloApplication.halo())
                .search(Data.NETWORK_AND_STORAGE, options) //s@back.end qwe123qwe
                .asContent(Friend.class)
                .execute(new CallbackV2<List<Friend>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<Friend>> result) {
                        if (result.data() != null) {
                            final List<Friend> friendList = result.data();
                            for (int i = 0; i < friendList.size(); i++) {
                                if (!emailsStored.contains(friendList.get(i).getUserMail())) {
                                    final Marker friendMarker = mMap.addMarker(new MarkerOptions().position(
                                            new LatLng(friendList.get(i).getLatitude(), friendList.get(i).getLongitude())));
                                    friendMarker.setTitle(friendList.get(i).getUserName() + " " + friendList.get(i).getRoom());
                                    friendMarker.setSnippet(friendList.get(i).getUserMail());
                                    friendMarker.setTag(friendList.get(i));
                                    BitmapDescriptor bitmapDescriptor;
                                    bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                                    friendMarker.setIcon(bitmapDescriptor);
                                    //fetch images for caching
                                    Picasso.with(getContext()).load(friendList.get(i).getUserPhoto()).fetch();
                                    emailsStored.add(friendList.get(i).getUserMail());
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void update(Observable o, Object result) {
        //only with a status change we reload all friends status
        if (result instanceof ObserverMsg) {
            PositionMsg positionMsg = ((ObserverMsg) result).getPositionMsg();
            if (positionMsg.isChangeStatus()) {
                mMap.clear();
                addFriendPoints();
            }
        }
    }
}
