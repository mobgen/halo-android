package com.mobgen.locationpoc.ui.friends;

/**
 * Created by f.souto.gonzalez on 13/06/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.mobgen.locationpoc.model.Friend;

import java.util.List;

public class InfoWindowSlidePagerAdapter extends FragmentStatePagerAdapter {

    List<Friend> mFriends;

    public InfoWindowSlidePagerAdapter(FragmentManager fm, List<Friend> friends) {
        super(fm);
        mFriends = friends;
    }

    @Override
    public Fragment getItem(int position) {
        return  InfoWindowSlidePageFragment.newInstance(mFriends.get(position));
    }

    @Override
    public int getCount() {
        return mFriends.size();
    }
}
