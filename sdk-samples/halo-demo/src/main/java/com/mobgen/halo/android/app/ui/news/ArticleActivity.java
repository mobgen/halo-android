package com.mobgen.halo.android.app.ui.news;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.transition.TransitionInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.Article;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.modules.partial.ModulesActivity;
import com.mobgen.halo.android.app.utils.DateUtils;
import com.mobgen.halo.android.app.utils.StatusInterceptor;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.api.Halo;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleActivity extends MobgenHaloActivity {

    private static final String BUNDLE_ARTICLE = "argument_article";
    private static final String BUNDLE_ARTICLE_STATUS = "argument_article_status";

    private Article mArticle;
    private HaloStatus mStatus;
    private ArticleViewHolder mViewHolder;

    public static void start(Context context, Article article, HaloStatus status, ActivityOptionsCompat options) {
        Intent intent = new Intent(context, ArticleActivity.class);
        Bundle data = new Bundle();
        data.putParcelable(BUNDLE_ARTICLE, article);
        data.putParcelable(BUNDLE_ARTICLE_STATUS, status);
        intent.putExtras(data);
        if (options != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.startActivity(intent, options.toBundle());
        } else {
            context.startActivity(intent);
        }
    }

    /**
     * Provides the deeplink for this activity.
     *
     * @param context The context to start the activity.
     * @param extras  The extras.
     */
    public static PendingIntent getDeeplink(Context context, Bundle extras, String moduleId) {
        Intent intentModulesActivity = ModulesActivity.getIntent(context);
        Intent intentArticleList = ArticlesListActivity.getIntent(context, moduleId);
        Intent intentArticle = new Intent(context, ArticleActivity.class);
        intentArticle.putExtras(extras);
        return TaskStackBuilder.create(context)
                .addNextIntent(intentModulesActivity)
                .addNextIntent(intentArticleList)
                .addNextIntent(intentArticle)
                .getPendingIntent(0, Intent.FILL_IN_PACKAGE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_article);
        mViewHolder = new ArticleViewHolder(getWindow().getDecorView());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.article_transition));
        }

        if (getIntent().getExtras() != null) {
            //Comes from the list and it has an article
            if (getIntent().getExtras().containsKey(BUNDLE_ARTICLE)) {
                mArticle = getIntent().getExtras().getParcelable(BUNDLE_ARTICLE);
            }
            if (getIntent().getExtras().containsKey(BUNDLE_ARTICLE_STATUS)) {
                mStatus = getIntent().getExtras().getParcelable(BUNDLE_ARTICLE_STATUS);
            }
        } else {
            finish();
        }
    }

    @Override
    public void onPresenterInitialized() {
        super.onPresenterInitialized();
        if (mArticle != null) {
            if (!TextUtils.isEmpty(mArticle.getTitle())) {
                setToolbarTitle(mArticle.getTitle());
                mViewHolder.mTitle.setText(mArticle.getTitle());
            }
            if (!TextUtils.isEmpty(mArticle.getArticle())) {
                mViewHolder.mArticle.loadData(mArticle.getArticle(), "text/html; charset=utf-8", "UTF-8");
            } else {
                mViewHolder.mArticle.setVisibility(View.GONE);
            }

            if (mArticle.getDate() != null) {
                mViewHolder.mDate.setText(DateUtils.formatDate(mArticle.getDate()));
            } else {
                mViewHolder.mDate.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mArticle.getImage())) {
                Picasso.with(this).load(mArticle.getImage()).into(mViewHolder.mTitleImage);
            } else {
                mViewHolder.mTitleImage.setVisibility(View.GONE);
            }
        } else {
            if (getIntent().getExtras().containsKey("instanceId")) {
                loadNewsInstanceDeepLink(getIntent().getExtras().getString("instanceId"));
            }
        }
        StatusInterceptor.intercept(mStatus, mViewHolder.mStatusBar);
    }

    /**
     * Loads the news by the instance id.
     *
     * @param instanceId The instance id.
     */
    private void loadNewsInstanceDeepLink(String instanceId) {

        SearchQuery options = SearchQuery.builder()
                .onePage(true)
                .instanceIds(instanceId)
                .searchTag(instanceId)
                .segmentWithDevice()
                .build();
        HaloContentApi.with(MobgenHaloApplication.halo())
                .search(Data.NETWORK_AND_STORAGE, options)
                .asContent(Article.class)
                .execute(new CallbackV2<List<Article>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<Article>> result) {
                        if(result.status().isOk()){
                            List<Article> data = result.data();
                            if (data != null && !data.isEmpty()) {
                                mArticle = data.get(0);
                            }
                            mStatus = result.status();
                            onPresenterInitialized();
                        } else {
                            Toast.makeText(ArticleActivity.this, "This article is not available.", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mArticle != null) {
            outState.putParcelable(BUNDLE_ARTICLE, mArticle);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mArticle = savedInstanceState.getParcelable(BUNDLE_ARTICLE);
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }

    private class ArticleViewHolder {
        private ImageView mTitleImage;
        private TextView mTitle;
        private WebView mArticle;
        private TextView mDate;
        private View mStatusBar;

        public ArticleViewHolder(View container) {
            mTitleImage = (ImageView) container.findViewById(R.id.iv_article_image);
            mTitle = (TextView) container.findViewById(R.id.tv_title);
            mArticle = (WebView) container.findViewById(R.id.wb_article);
            mDate = (TextView) container.findViewById(R.id.tv_date);
            mStatusBar = container.findViewById(R.id.v_status);
        }
    }
}
