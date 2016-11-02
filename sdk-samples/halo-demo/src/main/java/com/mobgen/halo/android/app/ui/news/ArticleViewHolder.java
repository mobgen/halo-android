package com.mobgen.halo.android.app.ui.news;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.DataStatusRecyclerAdapter;

public class ArticleViewHolder extends DataStatusRecyclerAdapter.StatusViewHolder {

    public TextView mTitle;
    public TextView mDate;
    public ImageView mThumbnail;
    public TextView mSummary;

    public ArticleViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        mTitle = (TextView) itemView.findViewById(R.id.tv_title);
        mDate = (TextView) itemView.findViewById(R.id.tv_date);
        mThumbnail = (ImageView) itemView.findViewById(R.id.iv_thumbnail);
        mSummary = (TextView) itemView.findViewById(R.id.tv_summary);
    }

    public TextView getTitle() {
        return mTitle;
    }

    public ImageView getImage() {
        return mThumbnail;
    }
}