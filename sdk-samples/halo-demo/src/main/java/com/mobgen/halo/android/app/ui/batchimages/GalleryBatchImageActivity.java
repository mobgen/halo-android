package com.mobgen.halo.android.app.ui.batchimages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.BatchImage;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.chat.ChatRoomActivity;
import com.mobgen.halo.android.app.ui.chat.QRScanActivity;
import com.mobgen.halo.android.app.ui.generalcontent.GeneralContentModuleActivity;
import com.mobgen.halo.android.app.utils.StatusInterceptor;
import com.mobgen.halo.android.app.utils.ViewUtils;
import com.mobgen.halo.android.content.edition.HaloContentEditApi;
import com.mobgen.halo.android.content.models.BatchOperationResults;
import com.mobgen.halo.android.content.models.BatchOperations;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.search.SearchQueryBuilderFactory;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;

import net.bohush.geometricprogressview.GeometricProgressView;

import java.util.ArrayList;
import java.util.Date;

import icepick.Icepick;
import icepick.State;

public class GalleryBatchImageActivity extends MobgenHaloActivity implements SwipeRefreshLayout.OnRefreshListener, BatchImageAdapter.ImageSelectionListener {

    private static final String BUNDLE_MODULE_NAME = "bundle_module_name";
    private static final String BUNDLE_MODULE_ID = "bundle_module_id";
    public static final int CODE_ACTIVITY = 41;

    @State
    ArrayList<BatchImage> mGalleryImages;
    @State
    String mModuleName;
    @State
    String mModuleId;
    private SwipeRefreshLayout mSwipeToRefresh;
    private RecyclerView mRecyclerView;
    private BatchImageAdapter mAdapter;
    private GeometricProgressView mProgressView;

    public static void start(@NonNull Activity activity, @NonNull String moduleName, @NonNull String moduleId) {
        Bundle data = new Bundle();
        data.putString(BUNDLE_MODULE_NAME, moduleName);
        data.putString(BUNDLE_MODULE_ID, moduleId);
        Intent intent = new Intent(activity, GalleryBatchImageActivity.class);
        intent.putExtras(data);
        activity.startActivityForResult(intent, CODE_ACTIVITY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_batch);
        mModuleName = getIntent().getExtras().getString(BUNDLE_MODULE_NAME);
        mModuleId = getIntent().getExtras().getString(BUNDLE_MODULE_ID);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Icepick.restoreInstanceState(this, savedInstanceState);

        mSwipeToRefresh = (SwipeRefreshLayout) findViewById(R.id.srl_generic);
        mAdapter = new BatchImageAdapter(this, this);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_generic);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressView = (GeometricProgressView) findViewById(R.id.gm_progress);
    }

    @Override
    public void onPresenterInitialized() {
        super.onPresenterInitialized();
        mRecyclerView.setAdapter(mAdapter);
        if (mGalleryImages == null) {
            loadGallery();
        } else {
            updateGallery();
        }
    }

    private void loadGallery() {
        //take random photos from unsplash
        mGalleryImages = new ArrayList<>();
        for (int i = 10; i < 500; i++) {
            mGalleryImages.add(new BatchImage("https://unsplash.it/800?image=" + i, "unsplah.it"));
        }
        ViewUtils.refreshing(mSwipeToRefresh, false);
        updateGallery();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void updateGallery() {
        mAdapter.setImages(mGalleryImages);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.batch_gallery_select_title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onRefresh() {
        loadGallery();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_batch_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_save_batch:
                //save instances on a batch operation
                mProgressView.setVisibility(View.VISIBLE);
                BatchOperations batchOperations = getInstancesToBatch();
                if (batchOperations != null) {
                    HaloContentEditApi.with(MobgenHaloApplication.halo())
                            .batch(batchOperations, false)
                            .execute(new CallbackV2<BatchOperationResults>() {
                                @Override
                                public void onFinish(@NonNull HaloResultV2<BatchOperationResults> result) {
                                    mProgressView.setVisibility(View.GONE);
                                    if (result.status().isOk()) {
                                        //set result ok
                                        setResult(RESULT_OK);
                                        finish();
                                    } else {
                                        //set result cancel and do not reload view
                                        setResult(RESULT_CANCELED);
                                        finish();
                                    }
                                }
                            });
                } else {
                    mProgressView.setVisibility(View.GONE);
                    Toast.makeText(GalleryBatchImageActivity.this, "Sorry we cannot make the operation", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    @Nullable
    public BatchOperations getInstancesToBatch() {
        if (mGalleryImages != null) {
            BatchOperations.Builder operations = BatchOperations.builder();
            for (int i = 0; i < mGalleryImages.size(); i++) {
                //add one instance with every image which has been selected
                if (mGalleryImages.get(i).isSelected()) {
                    Date now = new Date();
                    HaloContentInstance instance = new HaloContentInstance.Builder(mModuleName)
                            .withAuthor("Android SDK app")
                            .withContentData(mGalleryImages.get(i))
                            .withCreationDate(now)
                            .withName("Create from batch request")
                            .withPublishDate(now)
                            .withModuleId(mModuleId)
                            .build();
                    operations.create(instance);
                }
            }
            return operations.build();
        } else {
            return null;
        }
    }

    public void onImageSelected(String originalUrl, String thumb) {
        Log.v(originalUrl, thumb);
    }
}
