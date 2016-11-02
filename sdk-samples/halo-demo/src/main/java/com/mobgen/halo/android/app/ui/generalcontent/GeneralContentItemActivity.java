package com.mobgen.halo.android.app.ui.generalcontent;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.modules.partial.ModulesActivity;
import com.mobgen.halo.android.app.ui.views.DividerItemDecoration;
import com.mobgen.halo.android.app.utils.StatusInterceptor;
import com.mobgen.halo.android.app.utils.ViewUtils;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;

import java.util.List;

/**
 * Activity that displays the content of an item from the general content module. Simply displays the json content.
 */
public class GeneralContentItemActivity extends MobgenHaloActivity implements SwipeRefreshLayout.OnRefreshListener {

    /**
     * The bundle name to provide the values between activities.
     */
    private static final String BUNDLE_INSTANCE_ITEM = "bundle_instance";

    /**
     * The status of the item.
     */
    private static final String BUNDLE_INSTANCE_ITEM_STATUS = "bundle_instance_status";

    /**
     * The general content instance that will be displayed.
     */
    private HaloContentInstance mInstance;

    /**
     * The status of the instance.
     */
    private HaloStatus mStatus;

    /**
     * The list with the properties of then item.
     */
    private RecyclerView mRecyclerView;

    /**
     * The swipe to refresh layout to refresh the list.
     */
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * Broadcast receiver to refresh the data.
     */
    private RefreshBroadcastReceiver mRefreshReceiver;

    /**
     * The status bar.
     */
    private View mStatusBar;

    /**
     * Starts the current activity with the required parameters.
     *
     * @param context  The context for the previous activity.
     * @param instance The instance of the general content being displayed.
     * @param status   The status of the instance.
     */
    public static void startActivity(@NonNull Context context, @NonNull HaloContentInstance instance, @NonNull HaloStatus status) {
        Intent intent = new Intent(context, GeneralContentItemActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_INSTANCE_ITEM, instance);
        bundle.putParcelable(BUNDLE_INSTANCE_ITEM_STATUS, status);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    /**
     * Provides the deeplink for this activity.
     *
     * @param context The context to start the activity.
     * @param extras  The extras.
     * @param moduleName The module name
     */
    public static PendingIntent getDeeplink(Context context, Bundle extras, String moduleName) {
        Intent intentModulesActivity = ModulesActivity.getIntent(context);
        Intent intentModuleList = GeneralContentModuleActivity.getIntent(context, moduleName);
        Intent intentItem = new Intent(context, GeneralContentItemActivity.class);
        intentItem.putExtras(extras);
        return TaskStackBuilder.create(context)
                .addNextIntent(intentModulesActivity)
                .addNextIntent(intentModuleList)
                .addNextIntent(intentItem)
                .getPendingIntent(0, Intent.FILL_IN_PACKAGE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Take the extras
        if (getIntent().getExtras() != null) {
            mInstance = getIntent().getExtras().getParcelable(BUNDLE_INSTANCE_ITEM);
            mStatus = getIntent().getExtras().getParcelable(BUNDLE_INSTANCE_ITEM_STATUS);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_status_recycler_refresh);

        //Get the views
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_generic);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_generic);
        mRefreshReceiver = new RefreshBroadcastReceiver();
        mStatusBar = findViewById(R.id.v_status);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshReceiver, new IntentFilter("generalcontent-notification"));
    }

    @Override
    public void onPresenterInitialized() {
        super.onPresenterInitialized();
        if (mInstance != null) {
            //Set the adapter
            mRecyclerView.setAdapter(new GeneralContentItemAdapter(this, mInstance));
            mSwipeRefreshLayout.setOnRefreshListener(this);
            StatusInterceptor.intercept(mStatus, mStatusBar);
        } else {
            if (getIntent().getExtras().containsKey("instanceId")) {
                loadInstanceDeepLink(getIntent().getExtras().getString("instanceId"));
            }
        }
    }

    private void loadInstanceDeepLink(String instanceId) {

        SearchQuery options = SearchQuery.builder()
                .onePage(true)
                .instanceIds(instanceId)
                .searchTag(instanceId)
                .segmentWithDevice()
                .build();

        HaloContentApi.with(MobgenHaloApplication.halo())
                .search(Data.NETWORK_AND_STORAGE, options)
                .asContent()
                .execute(new CallbackV2<Paginated<HaloContentInstance>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<Paginated<HaloContentInstance>> result) {
                        if (result.status().isOk()) {
                            List<HaloContentInstance> data = result.data().data();
                            if (!data.isEmpty()) {
                                mInstance = data.get(0);
                                mStatus = result.status();
                                if (mInstance != null) {
                                    setToolbarTitle(mInstance.getName());
                                }
                                onPresenterInitialized();
                            }
                        } else {
                            Toast.makeText(GeneralContentItemActivity.this, "This instance is not available in your current environment.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRefreshReceiver);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onRefresh() {
        ViewUtils.refreshing(mSwipeRefreshLayout, true);
        SearchQuery options = SearchQuery.builder()
                .onePage(true)
                .instanceIds(mInstance.getItemId())
                .searchTag(mInstance.getItemId())
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
                            List<HaloContentInstance> data = result.data().data();
                            if (!data.isEmpty()) {
                                mInstance = data.get(0);
                                mStatus = result.status();
                                mRecyclerView.setAdapter(new GeneralContentItemAdapter(GeneralContentItemActivity.this, mInstance));
                                StatusInterceptor.intercept(mStatus, mStatusBar);
                            }
                        } else {
                            Halog.e(GeneralContentItemActivity.class, "Error while retrieving the new instance: " + result.status().getExceptionMessage());
                        }
                    }
                });
    }

    @Override
    public String getToolbarTitle() {
        if (mInstance != null) {
            return mInstance.getName();
        }
        return "";
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }

    private class RefreshBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Halog.d(getClass(), "Silent event received : " + intent.getExtras().toString());
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                //Refresh if it is the same instance
                String instanceId = bundle.getString("instanceId");
                if (instanceId != null && mInstance.getItemId().equalsIgnoreCase(instanceId)) {
                    onRefresh();
                }
            }
        }
    }
}
