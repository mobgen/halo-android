package com.mobgen.halo.android.app.ui.loadtests;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloFragment;
import com.mobgen.halo.android.app.ui.generalcontent.HaloContentInstancesAdapter;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SyncQuery;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.HaloContentApi.HaloSyncListener;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.HaloSyncLog;

import java.util.List;

import icepick.State;

public class SyncPerformanceFragment extends MobgenHaloFragment implements View.OnClickListener, HaloSyncListener {

    private static final String BUNDLE_MODULE = "bundle_module";
    private static final String MODULE_NAME = "Load tests";

    private Button mSyncAllButton;
    private Button mRetrieveItemsButton;
    private TextView mFullSyncText;
    private TextView mDatabaseFetchText;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    @State
    String mModuleId;
    private HaloContentInstancesAdapter mAdapter;
    private ISubscription mSyncSubscription;
    private long mRetrieveMeasure;
    private long mSyncMeasure;

    public static SyncPerformanceFragment create(String moduleId) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_MODULE, moduleId);
        SyncPerformanceFragment fragment = new SyncPerformanceFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            mModuleId = getArguments().getString(BUNDLE_MODULE);
        }
        mSyncSubscription = HaloContentApi.with(Halo.instance()).subscribeToSync(mModuleId, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sync_performance, container, false);
        mSyncAllButton = (Button) view.findViewById(R.id.bt_resync);
        mRetrieveItemsButton = (Button) view.findViewById(R.id.bt_retrieve_items);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_generic);
        mFullSyncText = (TextView) view.findViewById(R.id.tv_full_sync);
        mDatabaseFetchText = (TextView) view.findViewById(R.id.tv_fetch_time);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_loader);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSyncAllButton.setOnClickListener(this);
        mRetrieveItemsButton.setOnClickListener(this);
        mAdapter = new HaloContentInstancesAdapter(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
        retrieveSyncedInstances();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_resync:
                resync();
                break;
            case R.id.bt_retrieve_items:
                retrieveSyncedInstances();
                break;
        }
    }

    private void resync() {
        /*final HaloContentApi api = HaloContentApi.with(Halo.instance());

        api.clearSyncInstances(mModuleId, ActionOptions.POOL_QUEUE_POLICY, new Callback<Void, Void>() {
            @Override
            public void onFinish(@NonNull HaloResult<Void, Void> result) {
                disableButtons();
                startMeasuringSync();
                api.sync(SyncOptions.create(mModuleId, null, ActionOptions.POOL_QUEUE_POLICY));
            }
        });*/

        final HaloContentApi api = HaloContentApi.with(Halo.instance());

        api.clearSyncInstances(MODULE_NAME)
                .threadPolicy(Threading.POOL_QUEUE_POLICY)
                .execute(new CallbackV2<Void>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<Void> result) {
                        disableButtons();
                        startMeasuringSync();
                        api.sync(SyncQuery.create(MODULE_NAME, null, Threading.POOL_QUEUE_POLICY),true);
                    }
                });



    }

    private void disableButtons(){
        mSyncAllButton.setEnabled(false);
        mRetrieveItemsButton.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void enableButtons(){
        mSyncAllButton.setEnabled(true);
        mRetrieveItemsButton.setEnabled(true);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSyncSubscription != null){
            mSyncSubscription.unsubscribe();
        }
    }

    @Override
    public void onSyncFinished(@NonNull HaloStatus status, @Nullable HaloSyncLog log) {
        finishMeasuringSync();
        retrieveSyncedInstances();
    }

    private void retrieveSyncedInstances() {
        disableButtons();
        startMeasureRetrieve();
        final HaloContentApi api = HaloContentApi.with(Halo.instance());
        api.getSyncInstances(MODULE_NAME)
                .asContent()
                .threadPolicy(Threading.POOL_QUEUE_POLICY)
                .execute(new CallbackV2<List<HaloContentInstance>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<HaloContentInstance>> result) {
                        finishMeasureRetrieve();
                        mAdapter.setModuleDataItems(new HaloResultV2<>(result.status(), result.data()));
                        mAdapter.notifyDataSetChanged();
                        enableButtons();
                    }
                });


    }

    private void startMeasuringSync(){
        mSyncMeasure = System.currentTimeMillis();
        mFullSyncText.setText(R.string.load_measuring);
    }

    private void finishMeasuringSync(){
        mFullSyncText.setText(String.valueOf(System.currentTimeMillis() - mSyncMeasure));
        mSyncMeasure = 0;
    }

    private void startMeasureRetrieve() {
        mRetrieveMeasure = System.currentTimeMillis();
        mDatabaseFetchText.setText(R.string.load_measuring);
    }

    private void finishMeasureRetrieve() {
        mDatabaseFetchText.setText(String.valueOf(System.currentTimeMillis() - mRetrieveMeasure));
        mRetrieveMeasure = 0;
    }
}
