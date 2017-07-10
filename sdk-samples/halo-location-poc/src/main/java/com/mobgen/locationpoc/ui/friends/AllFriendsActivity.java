package com.mobgen.locationpoc.ui.friends;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;

import com.mobgen.locationpoc.R;
import com.mobgen.locationpoc.model.Friend;

import java.util.ArrayList;
import java.util.List;

public class AllFriendsActivity extends AppCompatActivity {

    private static final String BUNDLE_FRIEND_LIST = "bundle_friend_list";

    private ArrayList<Friend> mFriendList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Context mContext;

    public static void start(@NonNull Context context, List<Friend> friendList) {
        Intent intent = new Intent(context, AllFriendsActivity.class);
        intent.putParcelableArrayListExtra(BUNDLE_FRIEND_LIST, (ArrayList<Friend>) friendList);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mFriendList = getIntent().getExtras().getParcelableArrayList(BUNDLE_FRIEND_LIST);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allfriends);

        mContext = this;

        if (mFriendList != null) {
            mRecyclerView = (RecyclerView) findViewById(R.id.rv_friends);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(this);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), LinearLayout.VERTICAL);
            mRecyclerView.addItemDecoration(dividerItemDecoration);
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new AllFriendAdapter(mFriendList);
            mRecyclerView.setAdapter(mAdapter);
        }

    }

}
