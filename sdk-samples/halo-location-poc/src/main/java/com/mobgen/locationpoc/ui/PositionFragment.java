package com.mobgen.locationpoc.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobgen.locationpoc.R;
import com.mobgen.locationpoc.model.ObserverMsg;
import com.mobgen.locationpoc.model.PositionMsg;
import com.mobgen.locationpoc.receiver.BroadcastObserver;
import com.mobgen.locationpoc.utils.LocationUtils;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by f.souto.gonzalez on 01/06/2017.
 */

public class PositionFragment extends Fragment implements Observer, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private PositionFragment mPositionFragment;
    private SupportMapFragment mMapFragment;
    private GoogleApiClient mGoogleApiClient;
    private Marker mInsideMarker;
    private boolean updateUI;

    public static PositionFragment newInstance(BroadcastObserver broadcastObserver) {

        Bundle args = new Bundle();
        PositionFragment fragment = new PositionFragment();
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
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient
                    .Builder(getActivity())
                    .addApi(Places.GEO_DATA_API)
                    .addApi(Places.PLACE_DETECTION_API)
                    .enableAutoManage(getActivity(), this)
                    .build();
        }

        updateUI = true;

        return rootview;
    }

    /**
     * Setup the map with initial markers.
     *
     */
    public void setUpMapIfNeeded() {
        if (ActivityCompat.checkSelfPermission(mPositionFragment.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mPositionFragment.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(false);
        mInsideMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)));
        mInsideMarker.setVisible(false);
        Location location = LocationUtils.getLocation(getContext());;
        if (location != null) {
            LatLng myLatLng = new LatLng(location.getLatitude(),
                    location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 25.0f));

            mGoogleApiClient.connect();

            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                        mMap.addMarker(new MarkerOptions()
                                .position(placeLikelihood.getPlace().getLatLng()).title(placeLikelihood.getPlace().getName().toString())
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                                .snippet(placeLikelihood.getPlace().getPhoneNumber().toString()));
                    }
                    likelyPlaces.release();
                    mGoogleApiClient.disconnect();
                }
            });
        }
    }

    @Override
    public void update(Observable o, Object result) {
        //updates from receiver
        if (result instanceof ObserverMsg) {
            PositionMsg positionMsg = ((ObserverMsg) result).getPositionMsg();
            if (positionMsg.isChangeStatus() || updateUI) {
                updateUI = false;
                //remove all markers and add the new location
                Location location = LocationUtils.getLocation(getContext());
                if (location != null) {
                    //mMap.clear();
                    BitmapDescriptor colorMarker;
                    if (positionMsg.getDetectedName().contains(getString(R.string.scan_between_1))) {
                        colorMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                    } else {
                        colorMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    }
                    LatLng myLatLng = new LatLng(location.getLatitude(),
                            location.getLongitude());
                    mInsideMarker.setVisible(true);
                    mInsideMarker.setPosition(myLatLng);
                    mInsideMarker.setTitle(positionMsg.getDetectedName());
                    mInsideMarker.setIcon(colorMarker);
                    mInsideMarker.setSnippet(getString(R.string.welcome_msg));
                    mInsideMarker.showInfoWindow();

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 25.0f));
                    Toast.makeText(getContext(), getString(R.string.room_change), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
