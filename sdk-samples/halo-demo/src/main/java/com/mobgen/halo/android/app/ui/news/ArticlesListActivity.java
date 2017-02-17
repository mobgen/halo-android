package com.mobgen.halo.android.app.ui.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.Article;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.views.DividerItemDecoration;
import com.mobgen.halo.android.app.utils.ViewUtils;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.search.SearchQueryBuilderFactory;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.content.HaloContentApi;

import java.util.ArrayList;
import java.util.List;

public class ArticlesListActivity extends MobgenHaloActivity implements SwipeRefreshLayout.OnRefreshListener, ArticlesAdapter.ArticleCallback {

    private static final String BUNDLE_STATE_ARTICLES = "state_articles";
    private static final String BUNDLE_STATE_ARTICLES_STATUS = "state_articles_status";
    private static final String BUNDLE_MODULE_NAME = "module_name_bundle";

    private SwipeRefreshLayout mRefreshLayout;

    private String mNewsModuleName;
    private HaloResultV2<List<Article>> mArticlesResult;

    private ArticlesAdapter mAdapter;

    public static void start(Context context, String internalId) {
        context.startActivity(getIntent(context, internalId));
    }

    public static Intent getIntent(Context context, @NonNull String moduleName) {
        Intent intent = new Intent(context, ArticlesListActivity.class);
        intent.putExtra(BUNDLE_MODULE_NAME, moduleName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(BUNDLE_MODULE_NAME)) {
            mNewsModuleName = getIntent().getExtras().getString(BUNDLE_MODULE_NAME);
        } else {
            finish();
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(BUNDLE_STATE_ARTICLES)) {
            List<Article> articles = savedInstanceState.getParcelableArrayList(BUNDLE_STATE_ARTICLES);
            HaloStatus status = savedInstanceState.getParcelable(BUNDLE_STATE_ARTICLES_STATUS);
            mArticlesResult = new HaloResultV2<>(status, articles);
        }

        setContentView(R.layout.generic_recycler_refresh);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_generic);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_generic);
        mAdapter = new ArticlesAdapter(this, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this));
        recyclerView.setAdapter(mAdapter);
        mRefreshLayout.setOnRefreshListener(this);
        ViewUtils.refreshing(mRefreshLayout, true);
    }

    @Override
    public void onPresenterInitialized() {
        super.onPresenterInitialized();
        ViewUtils.refreshing(mRefreshLayout, false);
        if (mArticlesResult == null) {
            requestArticles();
        } else {
            updateAdapter();
        }
    }

    private void updateAdapter() {
        mAdapter.setArticles(mArticlesResult);
        mAdapter.notifyDataSetChanged();
    }

    private void requestArticles() {
        ViewUtils.refreshing(mRefreshLayout, true);

        SearchQuery options = SearchQueryBuilderFactory.getPublishedItems(mNewsModuleName, mNewsModuleName)
                .onePage(true)
                .segmentWithDevice()
                .build();

        HaloContentApi.with(MobgenHaloApplication.halo())
                .search(Data.NETWORK_AND_STORAGE, options)
                .asContent(Article.class)
                .execute(new CallbackV2<List<Article>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<Article>> result) {
                        ViewUtils.refreshing(mRefreshLayout, false);
                        if (result.status().isOk()) {
                            List<Article> articles = result.data();
                            if (articles != null) {
                                mArticlesResult = new HaloResultV2<>(result.status(), articles);
                                mAdapter.setArticles(mArticlesResult);
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(ArticlesListActivity.this, "This news articles could not be loaded.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.news_title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mArticlesResult != null) {
            outState.putParcelableArrayList(BUNDLE_STATE_ARTICLES, (ArrayList<? extends Parcelable>) mArticlesResult.data());
            outState.putParcelable(BUNDLE_STATE_ARTICLES_STATUS, mArticlesResult.status());
        }
    }

    @Override
    public void onRefresh() {
        requestArticles();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onArticleSelected(Article article, ArticleViewHolder holder) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, holder.getImage(), "articleImage");
        ArticleActivity.start(this, article, mAdapter.getStatus(), options);
    }
}
