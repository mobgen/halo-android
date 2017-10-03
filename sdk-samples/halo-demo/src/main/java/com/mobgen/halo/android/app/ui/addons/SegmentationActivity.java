package com.mobgen.halo.android.app.ui.addons;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.views.DividerItemDecoration;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.HaloManagerApi;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloSegmentationTag;

import java.util.List;

/**
 * The activity that shows some addons options.
 */
public class SegmentationActivity extends MobgenHaloActivity {

    /**
     * The segmentation tags adapter.
     */
    private SegmentationTagsAdapter mSegmentationsTagsAdapter;

    /**
     * The recycler view for the tags.
     */
    private RecyclerView mSegmentationRecycler;

    /**
     * The tag dialog.
     */
    private AlertDialog mTagDialog;

    /**
     * Starts the addons activity.
     *
     * @param context The context on which this activity will start.
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, SegmentationActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_recycler);

        mSegmentationRecycler = (RecyclerView) findViewById(R.id.rv_generic);
    }

    @Override
    public void onPresenterInitialized() {
        super.onPresenterInitialized();
        //Create the adapter
        mSegmentationsTagsAdapter = new SegmentationTagsAdapter(this);
        mSegmentationRecycler.setLayoutManager(new LinearLayoutManager(this));
        mSegmentationRecycler.addItemDecoration(new DividerItemDecoration(this));
        mSegmentationRecycler.setAdapter(mSegmentationsTagsAdapter);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                HaloSegmentationTag tag = mSegmentationsTagsAdapter.getTagAt(viewHolder.getAdapterPosition());
                HaloManagerApi.with(Halo.instance())
                        .removeDeviceTag(tag.getName(), true)
                        .threadPolicy(Threading.POOL_QUEUE_POLICY)
                        .execute(new CallbackV2<Device>() {
                            @Override
                            public void onFinish(@NonNull HaloResultV2<Device> result) {
                                updateTags();
                            }
                        });
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mSegmentationRecycler);
        updateTags();
    }

    /**
     * Updates the tags in the adapter.
     */
    private void updateTags() {
        HaloManagerApi.with(Halo.instance())
                .syncDevice()
                .execute(new CallbackV2<Device>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<Device> result) {
                        if (result.status().isOk()) {
                            Device device = result.data();
                            mSegmentationsTagsAdapter.setTags(device.getTags());
                            mSegmentationsTagsAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_segmentation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            createAddDialog();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Creates the add tag dialog.
     */
    public void createAddDialog() {
        if (mTagDialog != null) mTagDialog.dismiss();
        @SuppressLint("InflateParams") final View customView = getLayoutInflater().inflate(R.layout.dialog_create_tag, null);
        mTagDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.menu_add_segmentation_tag))
                .setView(customView)
                .setPositiveButton(R.string.confirm, null).create();
        mTagDialog.show();
        mTagDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox overrideTagKey = (CheckBox) customView.findViewById(R.id.ch_override_key);
                EditText tagName = (EditText) customView.findViewById(R.id.et_tag_name);
                EditText tagValue = (EditText) customView.findViewById(R.id.et_tag_value);
                HaloSegmentationTag tag = null;
                if (!TextUtils.isEmpty(tagName.getText().toString())) {
                    tag = new HaloSegmentationTag(tagName.getText().toString(), tagValue.getText().toString());
                }
                if (tag != null) {
                    HaloManagerApi.with(Halo.instance())
                            .addDeviceTag(tag, true, overrideTagKey.isChecked())
                            .threadPolicy(Threading.POOL_QUEUE_POLICY)
                            .execute(new CallbackV2<Device>() {
                                @Override
                                public void onFinish(@NonNull HaloResultV2<Device> result) {
                                    mTagDialog.dismiss();
                                    updateTags();
                                }
                            });
                } else {
                    customView.findViewById(R.id.tv_add_tag_error).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.addons_tags_title);
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mTagDialog != null) {
            mTagDialog.dismiss();
            mTagDialog = null;
        }
    }
}
