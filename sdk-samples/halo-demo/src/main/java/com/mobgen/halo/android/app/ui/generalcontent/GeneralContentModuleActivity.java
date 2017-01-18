package com.mobgen.halo.android.app.ui.generalcontent;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloSegmentationTag;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
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
     * Bundle name for perfom operations
     */
    private static final String BUNDLE_PERFOM_OPERATIONS = "bundle_perfom_operations";

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
     * The haloContentInstace
     */
    private HaloContentInstance mHaloContentInstance;
    /**
     * Determine if its content creation or not
     */
    private boolean mContentCreation = false;
    /**
     * The context.
     */
    private Context mContext;

    /**
     * The tag dialog.
     */
    private AlertDialog mTagDialog;

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

    /**
     * Factory method to start new instance of this activity.
     *
     * @param context The context to start the activity.
     * @param perfomOperation  True if a operation was performed (create or delete content).
     */
    public static void start(Context context, boolean perfomOperation) {
        Intent intent = new Intent(context, GeneralContentModuleActivity.class);
        Bundle extras = new Bundle();
        extras.putBoolean(BUNDLE_PERFOM_OPERATIONS, perfomOperation);
        intent.putExtras(extras);
        context.startActivity(intent);
    }

    /**
     * Factory method to start new instance of this activity.
     * @param context The context to start the activity.
     * @param moduleName  The module name.
     * @return
     */
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

        mContext = this;

        //Creates the adapter.
        mAdapter = new HaloContentInstancesAdapter(this);

        if (savedInstanceState != null) {
            List<HaloContentInstance> instances = savedInstanceState.getParcelableArrayList(BUNDLE_SAVE_MODULE_ITEMS);
            HaloStatus status = savedInstanceState.getParcelable(BUNDLE_SAVE_MODULE_ITEMS_STATUS);
            assert status != null;
            //Creates the adapter.
            mAdapter.setModuleDataItems(new HaloResultV2<>(status, instances));
        }

        //Get the views
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_generic);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_generic);

        mRefreshReceiver = new RefreshBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshReceiver, new IntentFilter("generalcontent-notification"));

    }

    @Override
    public void onResume(){
        super.onResume();
        mContentCreation = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getExtras().getBoolean(BUNDLE_PERFOM_OPERATIONS)) {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
                listGeneralContentModuleData(getModuleName());
            }
        }
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
    protected void onStop() {
        super.onStop();
        if (mTagDialog != null) {
            mTagDialog.dismiss();
            mTagDialog = null;
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
    public void listGeneralContentModuleData(final String moduleName) {
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
                                    //create generic object of the instance to push a new one
                                    if(result.data().data().size()>0) {
                                        HaloContentInstance instance = result.data().data().get(0);
                                        setGenericHaloContentInstance(instance);
                                    } else {
                                        mHaloContentInstance = null;
                                    }
                                }
                            }
                        }
                    });
        } else {
            ViewUtils.refreshing(mSwipeRefreshLayout, false);
        }
    }

    /**
     * Generates the mock HaloContentInstance to save on server
     */
    private void launchHaloContentInstaceAlert(){
        JSONObject valuesObject = new JSONObject();
        if(mHaloContentInstance==null){
            mHaloContentInstance = new HaloContentInstance(null,null, mModule.getId(), mModule.getName(), valuesObject,null, new Date(), new Date(), new Date(), new Date(), new Date(), null);
        } else{
            valuesObject = mHaloContentInstance.getValues();
        }
        createAddDialog(valuesObject);
    }

    /**
     * Creates the add values dialog.
     * @param valuesObject
     */
    public void createAddDialog(final JSONObject valuesObject) {
        if (mTagDialog != null) mTagDialog.dismiss();
        @SuppressLint("InflateParams") final View customView = getLayoutInflater().inflate(R.layout.dialog_create_coontent_values, null);
        mTagDialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(getString(R.string.addons_content_values_title))
                .setView(customView)
                .setNegativeButton(R.string.bt_close_content_values,null)
                .setNeutralButton(R.string.bt_more_content_values, null)
                .setPositiveButton(R.string.bt_more_content_done, null).create();
        mTagDialog.show();
        final EditText tagName = (EditText) customView.findViewById(R.id.et_content_values_name);
        final EditText tagValue = (EditText) customView.findViewById(R.id.et_content_values_value);
        mTagDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHaloContentInstance = null;
                mTagDialog.dismiss();
            }
        });

        mTagDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseContentInstanceValues(valuesObject,tagName.getText().toString(),tagValue.getText().toString());
                tagName.setText("");
                tagValue.setText("");
            }
        });

        mTagDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                parseContentInstanceValues(valuesObject,tagName.getText().toString(),tagValue.getText().toString());
                mTagDialog.dismiss();
                mContentCreation = true;
                GeneralContentItemActivity.startActivity(mContext, mHaloContentInstance,mModule.getName(), mAdapter.getStatus() , mContentCreation);
            }
        });
    }

    /**
     * Parse values to typed values.
     *
     * @param valuesObject The current values JSONObject.
     * @param tagName The EditText with tag name.
     * @param tagValue  The EditText with tag value.
     */
    private void parseContentInstanceValues(JSONObject valuesObject,String tagName, String tagValue) {
        try {
            try{
                if(TextUtils.isDigitsOnly(tagValue)){
                    valuesObject.put(tagName, Integer.valueOf(tagValue));
                } else if (TextUtils.isEmpty(tagValue)){
                    valuesObject.put(tagName, null);
                } else if(tagValue.equalsIgnoreCase("true") || tagValue.equalsIgnoreCase("false")){
                    valuesObject.put(tagName.toString(), Boolean.valueOf(tagValue).booleanValue());
                } else {
                    valuesObject.put(tagName, tagValue);
                }
            } catch (Exception e){
                valuesObject.put(tagName, null);
            }
            Toast.makeText(GeneralContentModuleActivity.this, "Content value added!!.", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates a mock HaloContentInstance from previous one.
     *
     * @param contentInstance
     */
    private void setGenericHaloContentInstance(HaloContentInstance contentInstance){
        JSONObject valuesObject = null;
        try {
            valuesObject = new JSONObject(contentInstance.getValues().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HaloContentInstance defaultInstance = new HaloContentInstance(contentInstance.getItemId(),null,contentInstance.getModuleId(),contentInstance.getName(),valuesObject,contentInstance.getAuthor(),contentInstance.getArchivedDate(),contentInstance.getCreatedDate(),contentInstance.getLastUpdate(),contentInstance.getPublishedDate(),contentInstance.getRemoveDate(),null);
        Iterator<String> iterator = defaultInstance.getValues().keys();
        String key = null;
        //set all items to non value
        while(iterator.hasNext()){
            key = iterator.next();
            try {
                if (defaultInstance.getValues().get(key) instanceof String) {
                    defaultInstance.getValues().put(key,"");
                } else if (defaultInstance.getValues().get(key)  instanceof Boolean) {
                    defaultInstance.getValues().put(key,false);
                } else if (defaultInstance.getValues().get(key)  instanceof Number) {
                    defaultInstance.getValues().put(key,0);
                } else {
                    defaultInstance.getValues().put(key,"");
                }
            } catch (JSONException e) {
            }
        }
        mHaloContentInstance = new HaloContentInstance(null,null,defaultInstance.getModuleId(),defaultInstance.getName(),defaultInstance.getValues(),contentInstance.getAuthor(),null,null,null,contentInstance.getPublishedDate(),contentInstance.getRemoveDate(),null);
    }

    @Override
    public void onRefresh() {
        mAdapter.notifyDataSetChanged();
        listGeneralContentModuleData(getModuleName());
    }

    @Override
    public void onModuleItemSelected(HaloContentInstance instanceSelected) {
        GeneralContentItemActivity.startActivity(this, instanceSelected, mModule.getName(), mAdapter.getStatus(),mContentCreation);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_SAVE_MODULE_ITEMS, (ArrayList<? extends Parcelable>) mAdapter.getModuleDataItems());
        outState.putParcelable(BUNDLE_SAVE_MODULE_ITEMS_STATUS, mAdapter.getStatus());
        outState.putParcelable("mHaloContentInstance", mHaloContentInstance);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mHaloContentInstance = savedInstanceState.getParcelable("mHaloContentInstance");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_generalcontent_instance_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add_instance) {
            if(mHaloContentInstance==null ){
                launchHaloContentInstaceAlert();
            } else {
                mContentCreation = true;
                GeneralContentItemActivity.startActivity(this, mHaloContentInstance, mModule.getName(), mAdapter.getStatus(), mContentCreation);
            }
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