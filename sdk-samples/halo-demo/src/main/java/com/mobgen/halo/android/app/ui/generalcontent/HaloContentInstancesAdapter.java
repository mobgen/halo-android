package com.mobgen.halo.android.app.ui.generalcontent;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.DataStatusRecyclerAdapter;
import com.mobgen.halo.android.app.utils.DateUtils;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;

import java.util.List;

/**
 * Adapter that displays module data items.
 */
public class HaloContentInstancesAdapter extends DataStatusRecyclerAdapter<ModuleItemView> {

    /**
     * Interface to click on a module item.
     */
    public interface ModuleItemListener {

        /**
         * Callback invoked when the general content instance has been selected.
         *
         * @param instanceSelected The instance selected.
         */
        void onModuleItemSelected(HaloContentInstance instanceSelected);
    }

    /**
     * The module item listener.
     */
    private ModuleItemListener mCallback;

    /**
     * The general content instances.
     */
    private List<HaloContentInstance> mModuleDataItems;

    /**
     * The modules data adapter constructor.
     *
     * @param context The context.
     */
    public HaloContentInstancesAdapter(@NonNull Context context) {
        super(context);
    }

    /**
     * Sets the module item listener.
     *
     * @param listener The listener.
     */
    public void setOnModuleItemListener(@Nullable ModuleItemListener listener) {
        mCallback = listener;
    }

    /**
     * Sets the instances for the modules data items.
     *
     * @param instances The instances to set.
     */
    public void setModuleDataItems(@NonNull HaloResultV2<List<HaloContentInstance>> instances) {
        mModuleDataItems = instances.data();
        setStatus(instances.status());
    }

    /**
     * Gets the module data items.
     *
     * @return The module data items.
     */
    @Nullable
    public List<HaloContentInstance> getModuleDataItems() {
        return mModuleDataItems;
    }

    @Override
    public ModuleItemView onCreateOverViewholder(ViewGroup parent, int viewType) {
        return new ModuleItemView(LayoutInflater.from(getContext()).inflate(R.layout.generic_title_subtitle_black, parent, false), viewType);
    }

    @Override
    public void onBindOverViewHolder(ModuleItemView holder, int position) {
        final HaloContentInstance instance = mModuleDataItems.get(position);
        holder.mModuleItemName.setText(instance.getName());
        holder.mModuleItemAuthor.setText(instance.getAuthor() + " - " + DateUtils.formatDate(instance.getLastUpdate()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onModuleItemSelected(instance);
                }
            }
        });
    }

    @Override
    public int getOverItemCount() {
        if (mModuleDataItems != null) {
            return mModuleDataItems.size();
        }
        return 0;
    }
}
