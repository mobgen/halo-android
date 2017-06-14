package com.mobgen.locationpoc.ui.friends;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobgen.locationpoc.R;
import com.mobgen.locationpoc.model.Friend;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by f.souto.gonzalez on 14/06/2017.
 */

public class AllFriendAdapter extends RecyclerView.Adapter<AllFriendAdapter.ViewHolder> {

    List<Friend> mFriendList;

    public AllFriendAdapter(List<Friend> friends) {
        mFriendList = friends;
    }


    @Override
    public AllFriendAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.window_layout, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(AllFriendAdapter.ViewHolder holder, int position) {
        holder.bind(mFriendList.get(position));
    }

    @Override
    public int getItemCount() {
        return mFriendList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private TextView userMail;
        private TextView userDate;
        private TextView room;
        private ImageView userImage;
        private Context mContext;

        public ViewHolder(View v) {
            super(v);
            room = (TextView) v.findViewById(R.id.tv_room);
            userName = (TextView) v.findViewById(R.id.tv_name);
            userMail = (TextView) v.findViewById(R.id.tv_mail);
            userDate = (TextView) v.findViewById(R.id.tv_date);
            userImage = (ImageView) v.findViewById(R.id.iv_photo);
            mContext = itemView.getContext();

        }

        private void bind(Friend friend) {
            if (friend != null) {
                room.setText(mContext.getString(R.string.room_info) + " " + friend.getRoom());
                userName.setText(mContext.getString(R.string.room_username) + " "+ friend.getUserName());
                userMail.setText(mContext.getString(R.string.room_email) + " " +friend.getUserMail());
                if (friend.getTime() != null) {
                    userDate.setText(friend.getTime().toString());
                }
                Picasso.with(mContext).load(friend.getUserPhoto()).into(userImage);
            }
        }
    }
}
