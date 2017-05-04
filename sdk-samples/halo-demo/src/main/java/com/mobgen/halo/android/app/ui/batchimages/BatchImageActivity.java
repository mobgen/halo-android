package com.mobgen.halo.android.app.ui.batchimages;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bluelinelabs.logansquare.JsonMapper;
import com.bluelinelabs.logansquare.LoganSquare;
import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.BatchImage;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.utils.StatusInterceptor;
import com.mobgen.halo.android.app.utils.ViewUtils;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.edition.HaloContentEditApi;
import com.mobgen.halo.android.content.models.BatchOperationResults;
import com.mobgen.halo.android.content.models.BatchOperations;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.search.SearchQueryBuilderFactory;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;

import net.bohush.geometricprogressview.GeometricProgressView;

import java.util.ArrayList;
import java.util.List;

import icepick.Icepick;
import icepick.State;

import static com.mobgen.halo.android.app.ui.batchimages.GalleryBatchImageActivity.CODE_ACTIVITY;

public class BatchImageActivity extends MobgenHaloActivity implements SwipeRefreshLayout.OnRefreshListener, BatchImageAdapter.ImageSelectionListener {

    private static final String BUNDLE_MODULE_NAME = "bundle_module_name";
    private static final String BUNDLE_MODULE_ID = "bundle_module_id";

    @State
    ArrayList<BatchImage> mGalleryImages;
    @State
    HaloStatus mStatus;
    @State
    String mModuleName;
    @State
    String mModuleId;
    private View mStatusView;
    private SwipeRefreshLayout mSwipeToRefresh;
    private RecyclerView mRecyclerView;
    private BatchImageAdapter mAdapter;
    private Context mContext;
    private GeometricProgressView mProgressView;

    public static void start(@NonNull Context context, @NonNull String moduleName, @NonNull String moduleId) {
        Bundle data = new Bundle();
        data.putString(BUNDLE_MODULE_NAME, moduleName);
        data.putString(BUNDLE_MODULE_ID, moduleId);
        Intent intent = new Intent(context, BatchImageActivity.class);
        intent.putExtras(data);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mModuleName = getIntent().getExtras().getString(BUNDLE_MODULE_NAME);
        mModuleId = getIntent().getExtras().getString(BUNDLE_MODULE_ID);
        mContext = this;
        Icepick.restoreInstanceState(this, savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (mModuleName == null) {
            throw new IllegalStateException("You have to provide the module name to start the activity.");
        }
        mAdapter = new BatchImageAdapter(this, this);
        mSwipeToRefresh = (SwipeRefreshLayout) findViewById(R.id.srl_generic);
        mStatusView = findViewById(R.id.v_status);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_generic);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProgressView = (GeometricProgressView) findViewById(R.id.gm_progress);
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
        //refresh all status
        if (mGalleryImages != null) {
            for (BatchImage images : mGalleryImages) {
                images.setSelected(false);
            }
        }
        ViewUtils.refreshing(mSwipeToRefresh, true);
        SearchQuery options = SearchQueryBuilderFactory.getPublishedItems(mModuleName, mModuleName)
                .onePage(true)
                .segmentWithDevice()
                .build();
        HaloContentApi.with(MobgenHaloApplication.halo())
                .search(Data.NETWORK_AND_STORAGE, options)
                .asContent()
                .execute(new CallbackV2<Paginated<HaloContentInstance>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<Paginated<HaloContentInstance>> result) {
                        ViewUtils.refreshing(mSwipeToRefresh, false);
                        // mLoadingAction = null;
                        mStatus = result.status();
                        if (mStatus.isOk()) {
                            List<HaloContentInstance> data = result.data().data();
                            mGalleryImages = new ArrayList<>();
                            if (data != null) {
                                JsonMapper<BatchImage> mapper = LoganSquare.mapperFor(BatchImage.class);
                                for (int j = 0; j < data.size(); j++) {
                                    try {
                                        BatchImage batchImage = mapper.parse(data.get(j).getValues().toString());
                                        batchImage.setInstanceId(data.get(j).getItemId());
                                        mGalleryImages.add(batchImage);
                                    } catch (Exception e) {
                                    }
                                }
                                updateGallery();
                            }
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        return getString(R.string.batch_gallery_title);
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
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == CODE_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                loadGallery();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_batch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_add_batch:
                //open remote batch gallery to add elements
                GalleryBatchImageActivity.start(this, mModuleName, mModuleId);
                break;
            case R.id.action_delete_batch:
                //delete instances on a batch operation
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
                                        //refresh the list
                                        loadGallery();
                                    } else {
                                        Toast.makeText(BatchImageActivity.this, "We have some problems with the batch request", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    mProgressView.setVisibility(View.GONE);
                    Toast.makeText(BatchImageActivity.this, "Sorry we cannot make the operation", Toast.LENGTH_SHORT).show();
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
                    HaloContentInstance instance = new HaloContentInstance.Builder(mModuleName)
                            .withAuthor("Android SDK app")
                            .withContentData(mGalleryImages.get(i))
                            .withName("Create from batch request")
                            .withId(mGalleryImages.get(i).getInstanceId())
                            .withModuleId(mModuleId)
                            .build();
                    operations.delete(instance);
                }
            }
            return operations.build();
        } else {
            return null;
        }
    }

    public void onImageSelected(String originalUrl, String thumb) {
    }
}
