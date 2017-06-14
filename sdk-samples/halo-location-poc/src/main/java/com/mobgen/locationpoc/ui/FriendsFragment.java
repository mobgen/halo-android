package com.mobgen.locationpoc.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm;
import com.google.maps.android.clustering.algo.PreCachingAlgorithmDecorator;
import com.google.maps.android.ui.IconGenerator;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.search.SearchQueryBuilderFactory;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.locationpoc.R;
import com.mobgen.locationpoc.model.Friend;
import com.mobgen.locationpoc.model.ObserverMsg;
import com.mobgen.locationpoc.model.PositionMsg;
import com.mobgen.locationpoc.receiver.AccessPointReceiver;
import com.mobgen.locationpoc.receiver.BroadcastObserver;
import com.mobgen.locationpoc.utils.FriendItem;
import com.mobgen.locationpoc.utils.LocationUtils;
import com.mobgen.locationpoc.utils.InfoWindowSlidePagerAdapter;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * Created by f.souto.gonzalez on 01/06/2017.
 */

public class FriendsFragment extends Fragment implements Observer, GoogleApiClient.OnConnectionFailedListener {

    private static final int SIX_HOURS = 60 * 60 * 6000;

    private GoogleMap mMap;
    private FriendsFragment mPositionFragment;
    private SupportMapFragment mMapFragment;
    private ClusterManager<FriendItem> mClusterManager;
    private PreCachingAlgorithmDecorator<FriendItem> algorithm;
    private SlidingUpPanelLayout mLayout;
    private ViewPager mPager;
    private InfoWindowSlidePagerAdapter mPagerAdapter;
    private RelativeLayout mControlLeft, mControlRight;
    private TextView no_elements;
    private List<Friend> uniqueFriendList = new ArrayList<>();

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
                mClusterManager = new ClusterManager<FriendItem>(getContext(), mMap);
                algorithm = new PreCachingAlgorithmDecorator<FriendItem>(new NonHierarchicalDistanceBasedAlgorithm<FriendItem>());
                mClusterManager.setAlgorithm(algorithm);
                setUpMapIfNeeded();
            }
        });

        no_elements = (TextView) rootview.findViewById(R.id.no_elements);
        mPager = (ViewPager) rootview.findViewById(R.id.pager);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    mControlLeft.setVisibility(View.INVISIBLE);
                } else {
                    mControlLeft.setVisibility(View.VISIBLE);
                }
                if (mPagerAdapter.getCount() - 1 == position || mPagerAdapter.getCount() == 1) {
                    mControlRight.setVisibility(View.INVISIBLE);
                } else {
                    mControlRight.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mControlLeft = (RelativeLayout) rootview.findViewById(R.id.ib_control_left);
        mControlLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1, true);
            }
        });
        mControlRight = (RelativeLayout) rootview.findViewById(R.id.ib_control_right);
        mControlRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1, true);
            }
        });
        mLayout = (SlidingUpPanelLayout) rootview.findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

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
                List<Friend> friendClustered = new ArrayList<Friend>();
                for (Cluster<FriendItem> cluster : algorithm.getClusters(22.0f)) {
                    if (cluster.getPosition().equals(marker.getPosition())) {
                        for (FriendItem friendItem : cluster.getItems()) {
                            friendClustered.add(friendItem.getFriend());
                        }
                    }
                }
                friendClustered = orderUniqueFriendList(friendClustered);

                showClusteredElements(friendClustered);
                return null;
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
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 18.0f));
            mMap.setOnCameraIdleListener(mClusterManager);
            mMap.setOnMarkerClickListener(mClusterManager);
            mMap.setOnInfoWindowClickListener(mClusterManager);

        }
        addFriendPoints();
    }

    /**
     * Show elements on sliding panel
     *
     * @param friends
     */
    private void showClusteredElements(List<Friend> friends) {
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        if (friends.size() > 0) {
            mPagerAdapter = new InfoWindowSlidePagerAdapter(getChildFragmentManager(), friends);
            mPager.setAdapter(mPagerAdapter);
            //set button visibility
            no_elements.setVisibility(View.GONE);
            mControlLeft.setVisibility(View.INVISIBLE);
            if (friends.size() > 1) {
                mControlRight.setVisibility(View.VISIBLE);
            } else {
                mControlRight.setVisibility(View.INVISIBLE);
            }
        } else {
            no_elements.setVisibility(View.VISIBLE);
            mControlLeft.setVisibility(View.INVISIBLE);
            mControlRight.setVisibility(View.INVISIBLE);
        }
        mLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);

    }

    /**
     * Add all markers to map
     */
    private void addFriendPoints() {
        final List<String> emailsStored = new ArrayList<>();
        final List<PolylineOptions> polylineOptions = new ArrayList<>();
        final List<Integer> polylineColor = new ArrayList<>();
        final IconGenerator iconFactory = new IconGenerator(getContext());
        SearchQuery options = SearchQueryBuilderFactory.getPublishedItems(AccessPointReceiver.MODULE_NAME_FRIENDS, AccessPointReceiver.MODULE_NAME_FRIENDS)
                .populateAll()
                .onePage(true)
                .build();

        HaloContentApi.with(MobgenHaloApplication.halo())
                .search(Data.NETWORK_AND_STORAGE, options) //s@back.end qwe123qwe
                .asContent(Friend.class)
                .execute(new CallbackV2<List<Friend>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<Friend>> result) {
                        if (result.data() != null) {
                            final List<Friend> friendList = result.data();
                            int currentIndex = 0;
                            uniqueFriendList.clear();
                            uniqueFriendList.addAll(orderUniqueFriendList(friendList));
                            for (int i = friendList.size() - 1; i >= 0; i--) {
                                drawMarkersAndLines(friendList, i, emailsStored, polylineColor, polylineOptions, iconFactory);
                            }
                            //cluster items
                            mClusterManager.cluster();

                            //add all polylines
                            for (int j = 0; j < polylineOptions.size(); j++) {
                                mMap.addPolyline(polylineOptions.get(j));
                            }
                            //center map on current location
                            Location location = LocationUtils.getLocation(getContext());
                            if (location != null) {
                                LatLng myLatLng = new LatLng(location.getLatitude(),
                                        location.getLongitude());
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 22.0f));
                            }
                        }
                    }
                });
    }

    /**
     * Get last position of the user based on timestamp
     *
     * @param friendListUnordered
     * @return
     */
    private List<Friend> orderUniqueFriendList(List<Friend> friendListUnordered) {
        LinkedHashMap<String, List<Friend>> groupFriendList = new LinkedHashMap<String, List<Friend>>();
        //group lists
        for (Friend friend : friendListUnordered) {
            List<Friend> temp = groupFriendList.get(friend.getUserMail());
            if (temp == null) {
                temp = new ArrayList<Friend>();
                groupFriendList.put(friend.getUserMail(), temp);
            }
            temp.add(friend);
        }
        //order by timestamp
        List<Friend> orderedFriendList = new ArrayList<>();
        for (int i = 0; i < groupFriendList.size(); i++) {
            List<Friend> tempList = (new ArrayList<List<Friend>>(groupFriendList.values())).get(i);
            Comparator<Friend> comparator = new Comparator<Friend>() {
                @Override
                public int compare(Friend left, Friend right) {
                    return (left.getTime().getTime() > right.getTime().getTime() ? -1 : 1);
                }
            };
            Collections.sort(tempList, comparator);
            orderedFriendList.add(tempList.get(0));
            tempList.clear();
        }
        return orderedFriendList;
    }

    /**
     * Draw polylines and markers clustered on map
     *
     * @param friendList
     * @param i
     * @param emailsStored
     * @param polylineColor
     * @param polylineOptions
     * @param iconFactory
     */
    private void drawMarkersAndLines(List<Friend> friendList, int i, List<String> emailsStored, List<Integer> polylineColor, List<PolylineOptions> polylineOptions, IconGenerator iconFactory) {
        int currentIndex;
        if (!emailsStored.contains(friendList.get(i).getUserMail())) {
            emailsStored.add(friendList.get(i).getUserMail());
            polylineColor.add(getRandomColor());
            polylineOptions.add(new PolylineOptions());
        }
        //add polyline options
        currentIndex = emailsStored.indexOf(friendList.get(i).getUserMail());
        polylineOptions.get(currentIndex).color(polylineColor.get(currentIndex));
        polylineOptions.get(currentIndex).add(
                new LatLng(friendList.get(i).getLatitude(), friendList.get(i).getLongitude()));
        
        if(isMarkerVisible(friendList.get(i).getTime())) {
            FriendItem offsetItem = new FriendItem(friendList.get(i).getLatitude(), friendList.get(i).getLongitude(), friendList.get(i));
            mClusterManager.addItem(offsetItem);
            addIcon(iconFactory, friendList.get(i), polylineColor.get(currentIndex));
        }

        //hide old lines
        polylineOptions.get(currentIndex).visible(isMarkerVisible(friendList.get(i).getTime()));
    }

    /**
     * add markers to map
     *
     * @param iconFactory
     * @param friend
     * @param color
     */
    private void addIcon(IconGenerator iconFactory, Friend friend, int color) {
        Marker friendMarker = mMap.addMarker(new MarkerOptions().position(
                new LatLng(friend.getLatitude(), friend.getLongitude())));
        friendMarker.setTitle(friend.getUserName() + " " + friend.getRoom());
        friendMarker.setSnippet(friend.getUserMail());
        friendMarker.setTag(friend);
        BitmapDescriptor bitmapDescriptor;
        bitmapDescriptor = getMarkerIcon(color);
        friendMarker.setIcon(bitmapDescriptor);

        Picasso.with(getContext()).load(friend.getUserPhoto()).fetch();

        friendMarker.setAnchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
        //hide old markers
        friendMarker.setVisible(isMarkerVisible(friend.getTime()));

    }

    /**
     * Determine if marker or line will be visible based on timestamp.
     *
     * @param timestamp
     * @return True if we need visibility false otherwise
     */
    private boolean isMarkerVisible(Date timestamp) {
        long now = new Date().getTime();
        if (timestamp == null) {
            return false;
        }
        return (now - timestamp.getTime()) < (SIX_HOURS);
    }

    /**
     * Get a random color to draw lines and markers.
     *
     * @return The random color as int.
     */
    private int getRandomColor() {
        Random randomGenerator = new Random();
        int red = randomGenerator.nextInt(256);
        int green = randomGenerator.nextInt(256);
        int blue = randomGenerator.nextInt(256);
        return Color.argb(255, red, green, blue);
    }

    /**
     * Get descriptor from color.
     *
     * @param color
     * @return The bitmapDescriptor.
     */
    public BitmapDescriptor getMarkerIcon(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
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
                if (mMap != null && mClusterManager != null) {
                    mMap.clear();
                    mClusterManager.clearItems();
                    addFriendPoints();
                }
            }
        }
    }
}
