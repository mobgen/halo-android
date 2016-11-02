package com.mobgen.halo.android.app.ui.news;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.Article;
import com.mobgen.halo.android.app.ui.DataStatusRecyclerAdapter;
import com.mobgen.halo.android.app.utils.DateUtils;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticlesAdapter extends DataStatusRecyclerAdapter<ArticleViewHolder> {

    private Context mContext;
    private ArticleCallback mCallback;
    private List<Article> mArticles;

    public interface ArticleCallback {
        void onArticleSelected(Article article, ArticleViewHolder viewHolder);
    }

    public ArticlesAdapter(Context context, ArticleCallback callback) {
        super(context);
        mContext = context;
        mCallback = callback;
    }

    public void setArticles(HaloResultV2<List<Article>> articles) {
        mArticles = articles.data();
        setStatus(articles.status());
    }

    @Override
    public ArticleViewHolder onCreateOverViewholder(ViewGroup parent, int viewType) {
        return new ArticleViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_article, parent, false), viewType);
    }

    @Override
    public void onBindOverViewHolder(final ArticleViewHolder holder, int position) {
        final Article article = mArticles.get(position);
        holder.mTitle.setText(article.getTitle());
        if (article.getDate() != null) {
            holder.mDate.setText(DateUtils.formatDate(article.getDate()));
        }
        if (!TextUtils.isEmpty(article.getImage())) {
            Picasso.with(mContext).load(article.getImage()).into(holder.mThumbnail);
        }
        if (!TextUtils.isEmpty(article.getSummary())) {
            holder.mSummary.setText(article.getSummary());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onArticleSelected(article, holder);
                }
            }
        });
    }

    @Override
    public int getOverItemCount() {
        if (mArticles == null) {
            return 0;
        }
        return mArticles.size();
    }


}
