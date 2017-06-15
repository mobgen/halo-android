package com.mobgen.locationpoc.ui.heatmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.search.SearchQueryBuilderFactory;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.locationpoc.R;
import com.mobgen.locationpoc.model.Friend;
import com.mobgen.locationpoc.receiver.AccessPointReceiver;
import com.mobgen.locationpoc.ui.MobgenHaloApplication;
import com.mobgen.locationpoc.utils.DateUtils;
import com.mobgen.locationpoc.utils.LocationUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by f.souto.gonzalez on 01/06/2017.
 */

public class HeatRegionFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {


    private GoogleMap mMap;
    private HeatRegionFragment mPositionFragment;
    private SupportMapFragment mMapFragment;
    private List<Friend> orderedFriendList = new ArrayList<>();
    private List<LatLng> heatPoint = new ArrayList<>();
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;

    boolean fragmentStatus = false;

    public static HeatRegionFragment newInstance() {

        Bundle args = new Bundle();
        HeatRegionFragment fragment = new HeatRegionFragment();
        fragment.setArguments(args);

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
        Location location = LocationUtils.getLocation(getContext());
        if (location != null) {
            LatLng myLatLng = new LatLng(location.getLatitude(),
                    location.getLongitude());
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 20.0f));
        }
        addFriendPoints();
    }


    /**
     * Add all markers to map
     */
    private void addFriendPoints() {
        SearchQuery options = SearchQueryBuilderFactory.getPublishedItems(AccessPointReceiver.MODULE_NAME_FRIENDS, AccessPointReceiver.MODULE_NAME_FRIENDS)
                .populateAll()
                .onePage(true)
                .build();

        HaloContentApi.with(MobgenHaloApplication.halo())
                .search(Data.NETWORK_AND_STORAGE, options)
                .asContent(Friend.class)
                .execute(new CallbackV2<List<Friend>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<Friend>> result) {
                        if (result.data() != null) {
                            orderedFriendList = orderByTimestamp(result.data());
                            for (int i = 0; i < orderedFriendList.size(); i++) {
                                heatPoint.add(convertFriendToLatLng(orderedFriendList, i));
                            }
                            //draw heat map
                            mProvider = new HeatmapTileProvider.Builder()
                                    .data(heatPoint)
                                    .radius(50)
                                    .build();
                            // Add a tile overlay to the map, using the heat map tile provider.
                            mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));

                            //center map on current location
                            Location location = LocationUtils.getLocation(getContext());
                            if (location != null) {
                                LatLng myLatLng = new LatLng(location.getLatitude(),
                                        location.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 23.0f));
                            }
                        }
                    }
                });
    }


    private List<Friend> orderByTimestamp(List<Friend> friends) {
        Comparator<Friend> comparator = new Comparator<Friend>() {
            @Override
            public int compare(Friend left, Friend right) {
                return (left.getTime().getTime() > right.getTime().getTime() ? 1 : -1);
            }
        };
        Collections.sort(friends, comparator);
        return friends;
    }


    /**
     * Draw polylines and markers clustered on map
     *
     * @param friendList
     * @param i
     */
    private LatLng convertFriendToLatLng(List<Friend> friendList, int i) {
        LatLng tmpPoint = new LatLng(friendList.get(i).getLatitude(), friendList.get(i).getLongitude());
        return tmpPoint;
    }

    public void startHeatAnimation() {

        final LinkedHashMap<Integer, List<LatLng>> myList = new LinkedHashMap<>();
        int subLists = 10;
        int elements = heatPoint.size() / subLists;
        for (int j = 0; j < subLists; j++) {
            if (j == 0) {
                myList.put(j, heatPoint.subList((elements * j), elements * (j + 1)));
            } else {
                myList.put(j, heatPoint.subList((elements * j) + 1, elements * (j + 1)));
            }
        }
        final List<LatLng> itemsToPut = new ArrayList<>();
        itemsToPut.add(new LatLng(0,0));
        final int[] indexMap = {0};

        mProvider.setData(itemsToPut);
        mOverlay.clearTileCache();
        mOverlay.setFadeIn(true);

        CountDownTimer timer = new CountDownTimer(4000, 3000 / subLists) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(myList.get(indexMap[0])!= null) {
                    itemsToPut.addAll(myList.get(indexMap[0]));
                    mProvider.setData(itemsToPut);
                    mOverlay.clearTileCache();
                    indexMap[0] = indexMap[0] + 1;
                }
            }

            @Override
            public void onFinish() {
            }
        };
        timer.start();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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


}
