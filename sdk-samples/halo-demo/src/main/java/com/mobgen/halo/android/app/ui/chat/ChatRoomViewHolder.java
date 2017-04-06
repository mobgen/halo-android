package com.mobgen.halo.android.app.ui.chat;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.DataStatusRecyclerAdapter;

public class ChatRoomViewHolder extends DataStatusRecyclerAdapter.StatusViewHolder {

    public TextView mUserName;
    public ImageView mThumbnail;

    public ChatRoomViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        mUserName = (TextView) itemView.findViewById(R.id.tv_title);
        mThumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
    }

    public TextView getTitle() {
        return mUserName;
    }

    public ImageView getImage() {
        return mThumbnail;
    }
}