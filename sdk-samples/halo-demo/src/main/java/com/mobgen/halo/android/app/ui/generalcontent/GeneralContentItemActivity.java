package com.mobgen.halo.android.app.ui.generalcontent;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.modules.partial.ModulesActivity;
import com.mobgen.halo.android.app.ui.views.DividerItemDecoration;
import com.mobgen.halo.android.app.utils.StatusInterceptor;
import com.mobgen.halo.android.app.utils.ViewUtils;
import com.mobgen.halo.android.content.edition.HaloContentEditApi;
import com.mobgen.halo.android.content.models.HaloEditContentOptions;
import com.mobgen.halo.android.content.models.HaloSyncLog;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Activity that displays the content of an item from the general content module. Simply displays the json content.
 */
public class GeneralContentItemActivity extends MobgenHaloActivity implements SwipeRefreshLayout.OnRefreshListener,HaloContentApi.HaloSyncListener {

    /**
     * The bundle name to provide the values between activities.
     */
    private static final String BUNDLE_INSTANCE_ITEM = "bundle_instance";

    /**
     * The status of the item.
     */
    private static final String BUNDLE_INSTANCE_ITEM_STATUS = "bundle_instance_status";

    /**
     *  The condition to know if its creating content
     */
    private static final String BUNDLE_CONTENT_CREATION = "bundle_content_creation";

    /**
     * The module name.
     */
    private static final String BUNDLE_ARGUMENT_MODULE_NAME = "bundle_module_name";

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
     * Save instance
     */
    private Button mSaveButton;
    /**
     * Put screen in edit mode
     */
    private boolean mEditable = false;

    /**
     * Determine if its content creation or not
     */
    private boolean mContentCreation = false;

    /**
     * The context
     */
    private Context mContext;

    /**
     * The operation status
     */
    private boolean mOperationStatus = false;

    /**
     * The module name.
     */
    private String mModuleName;

    /**
     * The subscription to sync.
     */
    private ISubscription mSyncSubscription;

    /**
     * Starts the current activity with the required parameters.
     *
     * @param context  The context for the previous activity.
     * @param instance The instance of the general content being displayed.
     * @param status   The status of the instance.
     */
    public static void startActivity(@NonNull Context context, @NonNull HaloContentInstance instance, @NonNull String moduleName, @NonNull HaloStatus status, @NonNull boolean contentCreation) {
        Intent intent = new Intent(context, GeneralContentItemActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_INSTANCE_ITEM, instance);
        bundle.putParcelable(BUNDLE_INSTANCE_ITEM_STATUS, status);
        bundle.putBoolean(BUNDLE_CONTENT_CREATION, contentCreation);
        bundle.putString(BUNDLE_ARGUMENT_MODULE_NAME,moduleName);
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
            mContentCreation = getIntent().getExtras().getBoolean(BUNDLE_CONTENT_CREATION);
            mModuleName = getIntent().getExtras().getString(BUNDLE_ARGUMENT_MODULE_NAME);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_status_recycler_refresh);

        mContext = this;

        //Get the views
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_generic);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_generic);
        mRefreshReceiver = new RefreshBroadcastReceiver();
        mStatusBar = findViewById(R.id.v_status);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this));
        LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshReceiver, new IntentFilter("generalcontent-notification"));

        mSyncSubscription = HaloContentApi.with(MobgenHaloApplication.halo()).subscribeToSync(mModuleName,this);

        mSaveButton = (Button) findViewById(R.id.instance_save);
        if(mContentCreation){
            //remove swippe to refresh and show save button
            mEditable = true;
            mSaveButton.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(false);

            mSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Map<String, Object> values = new HashMap<>();
                    List<Pair<String, Object>> elements = ((GeneralContentItemAdapter) mRecyclerView.getAdapter()).getItems();
                    for (int i = 0; i < elements.size(); i++) {
                        values.put(elements.get(i).first, elements.get(i).second);
                    }

                    HaloEditContentOptions.Builder instanceBuilder = new HaloEditContentOptions.Builder(mModuleName)
                            .withModuleId(mInstance.getModuleId())
                            .withPublishDate(mInstance.getPublishedDate())
                            .withName("From Android SDK: "+ new Date().toGMTString())
                            .withContentData(values);
                    HaloContentEditApi.addContent(instanceBuilder.build())
                            .execute(new CallbackV2<HaloContentInstance>() {
                                @Override
                                public void onFinish(@NonNull HaloResultV2<HaloContentInstance> result) {
                                    if (result.status().isAuthenticationError()) {
                                        Toast.makeText(GeneralContentItemActivity.this, "You must sigin or login to add a content.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if (result.data() != null) {
                                            mOperationStatus = true;
                                            Toast.makeText(GeneralContentItemActivity.this, "Content added succesfully: " + result.data().getName(), Toast.LENGTH_SHORT).show();
                                            GeneralContentModuleActivity.start(mContext,mOperationStatus);
                                        } else {
                                            Toast.makeText(GeneralContentItemActivity.this, "Please review the content of the fields.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }
            });
        } else {
            if(mEditable) {
                mSaveButton.setVisibility(View.VISIBLE);
            }
            mSaveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Map<String,Object> values = new HashMap<>();
                    List<Pair<String, Object>> elements = ((GeneralContentItemAdapter)mRecyclerView.getAdapter()).getItems();
                    for(int i=0;i<elements.size();i++){
                        values.put(elements.get(i).first,elements.get(i).second);
                    }

                    HaloEditContentOptions.Builder instanceBuilder = new HaloEditContentOptions.Builder(mModuleName)
                            .withId(mInstance.getItemId())
                            .withModuleId(mInstance.getModuleId())
                            .withName(mInstance.getName())
                            .withContentData(values);

                    HaloContentEditApi.updateContent(instanceBuilder.build())
                            .execute(new CallbackV2<HaloContentInstance>() {
                                @Override
                                public void onFinish(@NonNull HaloResultV2<HaloContentInstance> result) {
                                    if(result.status().isAuthenticationError()){
                                        Toast.makeText(GeneralContentItemActivity.this, "You must sigin or login to update a content.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        if(result.data()!=null) {
                                            mOperationStatus = true;
                                            mInstance = result.data();
                                            Toast.makeText(GeneralContentItemActivity.this, "Congrats! Your content was updated.", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(GeneralContentItemActivity.this, "Please review the content of the fields.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                }
            });
        }
    }

    @Override
    public void onPresenterInitialized() {
        super.onPresenterInitialized();
        if (mInstance != null) {
            //Set the adapter
            mRecyclerView.setAdapter(new GeneralContentItemAdapter(this, mInstance,mEditable));
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("mEditable",mEditable);
        outState.putParcelable("mInstance",mInstance);
        outState.putBoolean("mContentCreation",mContentCreation);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mEditable = savedInstanceState.getBoolean("mEditable");
        if(mEditable) {
            mSaveButton.setVisibility(View.VISIBLE);
        } else {
            mSaveButton.setVisibility(View.GONE);
        }
        mInstance = savedInstanceState.getParcelable("mInstance");
        mContentCreation = savedInstanceState.getBoolean("mContentCreation");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSyncSubscription.unsubscribe();
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
                                mRecyclerView.setAdapter(new GeneralContentItemAdapter(GeneralContentItemActivity.this, mInstance,mEditable));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!mContentCreation) {
            getMenuInflater().inflate(R.menu.menu_generalcontent_instance, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_delete_instance) {
            HaloEditContentOptions.Builder instanceBuilder = new HaloEditContentOptions.Builder(mModuleName)
                    .withId(mInstance.getItemId());
            HaloContentEditApi.deleteContent(instanceBuilder.build())
                    .execute(new CallbackV2<HaloContentInstance>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<HaloContentInstance> result) {
                            if(result.status().isAuthenticationError()){
                                Toast.makeText(GeneralContentItemActivity.this, "You must sigin or login to delete a content.", Toast.LENGTH_SHORT).show();
                            } else {
                                if(result.data()!=null) {
                                    mOperationStatus = true;
                                    Toast.makeText(GeneralContentItemActivity.this, "Congrats. The content was deleted.", Toast.LENGTH_SHORT).show();
                                    GeneralContentModuleActivity.start(mContext,mOperationStatus);
                                }
                            }
                        }
                    });
            return true;
        } else if (item.getItemId() == R.id.action_update_instance) {
            mEditable = !mEditable;
            if(mEditable) {
                mSaveButton.setVisibility(View.VISIBLE);
            } else {
                mSaveButton.setVisibility(View.GONE);
            }
            mRecyclerView.getAdapter().notifyDataSetChanged();
            mRecyclerView.setAdapter(mRecyclerView.getAdapter());
            ((GeneralContentItemAdapter)mRecyclerView.getAdapter()).editableMode(mEditable);
            return true;
        } else {
            super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
        GeneralContentModuleActivity.start(mContext,mOperationStatus);
    }

    @Override
    public void onSyncFinished(@NonNull HaloStatus status, @Nullable HaloSyncLog log) {
        Halog.v(GeneralContentItemActivity.class,status.toString());
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
