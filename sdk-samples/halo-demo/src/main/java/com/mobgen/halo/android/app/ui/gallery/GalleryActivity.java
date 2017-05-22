package com.mobgen.halo.android.app.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.GalleryImage;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.common.FullScreenImageFragment;
import com.mobgen.halo.android.app.utils.StatusInterceptor;
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

import icepick.Icepick;
import icepick.State;

public class GalleryActivity extends MobgenHaloActivity implements SwipeRefreshLayout.OnRefreshListener, GalleryAdapter.ImageSelectionListener {

    private static final String BUNDLE_MODULE_NAME = "bundle_module_name";

    @State
    ArrayList<GalleryImage> mGalleryImages;
    @State
    HaloStatus mStatus;
    @State
    String mModuleName;
    private View mStatusView;
    private SwipeRefreshLayout mSwipeToRefresh;
    private RecyclerView mRecyclerView;
    private GalleryAdapter mAdapter;
   // private Action mLoadingAction;

    public static void start(@NonNull Context context, @NonNull String moduleName) {
        Bundle data = new Bundle();
        data.putString(BUNDLE_MODULE_NAME, moduleName);
        Intent intent = new Intent(context, GalleryActivity.class);
        intent.putExtras(data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mModuleName = getIntent().getExtras().getString(BUNDLE_MODULE_NAME);
        Icepick.restoreInstanceState(this, savedInstanceState);

        if (mModuleName == null) {
            throw new IllegalStateException("You have to provide the module name to start the activity.");
        }
        mAdapter = new GalleryAdapter(this, this);
        mSwipeToRefresh = (SwipeRefreshLayout) findViewById(R.id.srl_generic);
        mStatusView = findViewById(R.id.v_status);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_generic);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onPresenterInitialized() {
        super.onPresenterInitialized();
        mRecyclerView.setAdapter(mAdapter);
        mSwipeToRefresh.setOnRefreshListener(this);
        if (mGalleryImages == null) {
            loadGallery();
        } else {
            updateGallery();
        }
    }

    private void loadGallery() {
        ViewUtils.refreshing(mSwipeToRefresh, true);

        //TODO change moduleId to moduleName

        SearchQuery options = SearchQueryBuilderFactory.getPublishedItems(mModuleName,mModuleName)
                .onePage(true)
                .segmentWithDevice()
                .build();
        HaloContentApi.with(MobgenHaloApplication.halo())
                .search(Data.NETWORK_AND_STORAGE, options)
                .asContent(GalleryImage.class)
                .execute(new CallbackV2<List<GalleryImage>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<GalleryImage>> result) {
                        ViewUtils.refreshing(mSwipeToRefresh, false);
                       // mLoadingAction = null;
                        mStatus = result.status();
                        if(mStatus.isOk()){
                            List<GalleryImage> data = result.data();
                            if (data != null) {
                                mGalleryImages = (ArrayList<GalleryImage>) data;
                                updateGallery();
                            }
                        }
                    }
                });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // if (mLoadingAction != null) {
        //    mLoadingAction.cancel();
       // }
    }

    private void updateGallery() {
        if (mStatus != null) {
            StatusInterceptor.intercept(mStatus, mStatusView);
        }
        mAdapter.setImages(mGalleryImages);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.gallery_title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onRefresh() {
        //if (mLoadingAction == null) {
            loadGallery();
       // }
    }

    public void onImageSelected(String originalUrl, String thumb) {
        FullScreenImageFragment fragment = FullScreenImageFragment.create(originalUrl, thumb);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fragment.setEnterTransition(new Slide(Gravity.TOP));
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_fragment_container, fragment)
                .addToBackStack(null).commit();
    }
}
