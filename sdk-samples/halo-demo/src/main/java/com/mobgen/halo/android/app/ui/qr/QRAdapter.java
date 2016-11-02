package com.mobgen.halo.android.app.ui.qr;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.QROffer;
import com.mobgen.halo.android.app.ui.DataStatusRecyclerAdapter;
import com.mobgen.halo.android.app.utils.DateUtils;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.squareup.picasso.Picasso;

import java.util.List;

public class QRAdapter extends DataStatusRecyclerAdapter<QRViewHolder> {

    public interface QRListener {
        void onQRClicked(QROffer article);
    }

    private List<QROffer> mList;
    private QRListener mCallback;

    public QRAdapter(Context context, QRListener listener) {
        super(context);
        mCallback = listener;
    }

    public void setQRList(HaloResultV2<List<QROffer>> articles) {
        mList = articles.data();
        setStatus(articles.status());
    }

    @Override
    public QRViewHolder onCreateOverViewholder(ViewGroup parent, int viewType) {
        return new QRViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.adapter_article, parent, false), viewType);
    }

    @Override
    public void onBindOverViewHolder(QRViewHolder holder, int position) {
        final QROffer article = mList.get(position);
        holder.mTitle.setText(article.getTitle());
        if (article.getDate() != null) {
            holder.mDate.setText(DateUtils.formatDate(article.getDate()));
        }
        if (!TextUtils.isEmpty(article.getThumbnail())) {
            Picasso.with(getContext()).load(article.getThumbnail()).into(holder.mThumbnail);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onQRClicked(article);
                }
            }
        });
    }

    @Override
    public int getOverItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }
}
