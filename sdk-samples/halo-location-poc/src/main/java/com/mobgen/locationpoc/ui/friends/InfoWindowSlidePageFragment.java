package com.mobgen.locationpoc.ui.friends;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobgen.locationpoc.R;
import com.mobgen.locationpoc.model.Friend;
import com.mobgen.locationpoc.utils.DateUtils;
import com.squareup.picasso.Picasso;

import java.util.Date;

/**
 * Created by f.souto.gonzalez on 13/06/2017.
 */

public class InfoWindowSlidePageFragment extends Fragment {

    private static final String BUNDLE_FRIEND = "bundle_friend";

    private Friend mFriend;

    public static InfoWindowSlidePageFragment newInstance(Friend friend) {

        Bundle args = new Bundle();
        args.putParcelable(BUNDLE_FRIEND, friend);
        InfoWindowSlidePageFragment fragment = new InfoWindowSlidePageFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFriend = getArguments().getParcelable(BUNDLE_FRIEND);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.window_friend_layout, container, false);

        TextView room = (TextView) rootView.findViewById(R.id.tv_room);
        TextView userName = (TextView) rootView.findViewById(R.id.tv_name);
        TextView userMail = (TextView) rootView.findViewById(R.id.tv_mail);
        TextView userDate = (TextView) rootView.findViewById(R.id.tv_date);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.iv_photo);

        if (mFriend != null) {
            room.setText(getContext().getString(R.string.room_info) + " " + mFriend.getRoom());
            userName.setText(getContext().getString(R.string.room_username) + " " + mFriend.getUserName());
            userMail.setText(getContext().getString(R.string.room_email) + " " + mFriend.getUserMail());
            userDate.setText(DateUtils.timeBetween(getContext(), mFriend.getTime(), new Date()));
            Picasso.with(getContext()).load(mFriend.getUserPhoto()).into(imageView);
        }

        return rootView;
    }

}
