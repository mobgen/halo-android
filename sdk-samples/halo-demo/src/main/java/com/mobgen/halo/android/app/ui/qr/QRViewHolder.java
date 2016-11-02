package com.mobgen.halo.android.app.ui.qr;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.DataStatusRecyclerAdapter;

public class QRViewHolder extends DataStatusRecyclerAdapter.StatusViewHolder {

    public TextView mTitle;
    public TextView mDate;
    public ImageView mThumbnail;

    public QRViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        mTitle = (TextView) itemView.findViewById(R.id.tv_title);
        mDate = (TextView) itemView.findViewById(R.id.tv_date);
        mThumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
    }

    public TextView getTitle() {
        return mTitle;
    }

    public ImageView getImage() {
        return mThumbnail;
    }
}