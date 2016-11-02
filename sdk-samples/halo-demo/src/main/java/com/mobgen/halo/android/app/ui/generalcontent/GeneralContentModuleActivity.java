package com.mobgen.halo.android.app.ui.generalcontent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.views.DividerItemDecoration;
import com.mobgen.halo.android.app.utils.ViewUtils;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.search.SearchQueryBuilderFactory;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.core.management.models.HaloModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity that displays a list of the elements of a given module.
 */
public class GeneralContentModuleActivity extends MobgenHaloActivity implements SwipeRefreshLayout.OnRefreshListener,
        HaloContentInstancesAdapter.ModuleItemListener {

    /**
     * Bundle argument to send the module that this activity depends on.
     */
    private static final String BUNDLE_ARGUMENT_MODULE = "bundle_module";

    /**
     * The module name.
     */
    private static final String BUNDLE_ARGUMENT_MODULE_NAME = "bundle_module_name";

    /**
     * Saves the status of the items.
     */
    private static final String BUNDLE_SAVE_MODULE_ITEMS_STATUS = "bundle_module_items_status";

    /**
     * Bundle name for the saved instance items.
     */
    private static final String BUNDLE_SAVE_MODULE_ITEMS = "bundle_module_items";

    /**
     * General content module that will be displayed.
     */
    private HaloModule mModule;

    /**
     * The recycler view to show the content.
     */
    private RecyclerView mRecyclerView;

    /**
     * The swipe to refresh layout to refresh the list.
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * The adapter that contains the data retrieved from the server.
     */
    private HaloContentInstancesAdapter mAdapter;

    /**
     * The refresh broadcast receiver.
     */
    private RefreshBroadcastReceiver mRefreshReceiver;

    /**
     * Factory method to start this activity.
     *
     * @param context The context to start the activity.
     * @param module  The module.
     */
    public static void start(Context context, HaloModule module) {
        Intent intent = new Intent(context, GeneralContentModuleActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelable(BUNDLE_ARGUMENT_MODULE, module);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    public static Intent getIntent(Context context, String moduleName) {
        Intent intent = new Intent(context, GeneralContentModuleActivity.class);
        Bundle extras = new Bundle();
        extras.putString(BUNDLE_ARGUMENT_MODULE_NAME, moduleName);
        intent.putExtras(extras);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Bring the module
        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            mModule = extras.getParcelable(BUNDLE_ARGUMENT_MODULE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_recycler_refresh);

        //Creates the adapter.
        mAdapter = new HaloContentInstancesAdapter(this);

        if (savedInstanceState != null) {
            List<HaloContentInstance> instances = savedInstanceState.getParcelableArrayList(BUNDLE_SAVE_MODULE_ITEMS);
            HaloStatus status = savedInstanceState.getParcelable(BUNDLE_SAVE_MODULE_ITEMS_STATUS);
            assert status != null;
            mAdapter.setModuleDataItems(new HaloResultV2<>(status, instances));
        }

        //Get the views
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_generic);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_generic);

        mRefreshReceiver = new RefreshBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshReceiver, new IntentFilter("generalcontent-notification"));
    }

    @Override
    public void onPresenterInitialized() {
        super.onPresenterInitialized();
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnModuleItemListener(this);
        //Make the request
        if (mAdapter.getItemCount() == 0) {
            listGeneralContentModuleData(getModuleName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRefreshReceiver);
    }

    /**
     * Lists the content of a general content module
     */
    public void listGeneralContentModuleData(String moduleName) {
        if (moduleName != null) {
            ViewUtils.refreshing(mSwipeRefreshLayout, true);
            SearchQuery options = SearchQueryBuilderFactory.getPublishedItems(moduleName, moduleName)
                    .onePage(true)
                    .segmentWithDevice()
                    .build();
            HaloContentApi.with(MobgenHaloApplication.halo())
                    .search(Data.NETWORK_AND_STORAGE, options)
                    .asContent()
                    .execute(new CallbackV2<Paginated<HaloContentInstance>>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<Paginated<HaloContentInstance>> result) {
                            ViewUtils.refreshing(mSwipeRefreshLayout, false);
                            if (result.status().isOk()) {
                                if (result.data() != null) {
                                    mAdapter.setModuleDataItems(new HaloResultV2<>(result.status(), result.data().data()));
                                    mAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
        } else {
            ViewUtils.refreshing(mSwipeRefreshLayout, false);
        }
    }

    @Override
    public void onRefresh() {
        mAdapter.notifyDataSetChanged();
        listGeneralContentModuleData(getModuleName());
    }

    @Override
    public void onModuleItemSelected(HaloContentInstance instanceSelected) {
        GeneralContentItemActivity.startActivity(this, instanceSelected, mAdapter.getStatus());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_SAVE_MODULE_ITEMS, (ArrayList<? extends Parcelable>) mAdapter.getModuleDataItems());
        outState.putParcelable(BUNDLE_SAVE_MODULE_ITEMS_STATUS, mAdapter.getStatus());
    }

    @Override
    public String getToolbarTitle() {
        if (mModule != null) {
            return mModule.getName();
        } else {
            return "";
        }
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }

    public String getModuleName() {
        if (mModule != null && mModule.getName() != null) {
            return mModule.getName();
        } else if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(BUNDLE_ARGUMENT_MODULE_NAME)) {
            return getIntent().getExtras().getString(BUNDLE_ARGUMENT_MODULE_NAME);
        }
        return null;
    }

    /**
     * The refreshing broadcast receiver.
     */
    public class RefreshBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                String moduleName = bundle.getString("moduleName");
                if (mModule.getName().equalsIgnoreCase(moduleName)) {
                    onRefresh();
                }
            }
        }
    }
}