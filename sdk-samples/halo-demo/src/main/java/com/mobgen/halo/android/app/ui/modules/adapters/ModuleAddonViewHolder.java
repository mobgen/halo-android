package com.mobgen.halo.android.app.ui.modules.adapters;

import android.view.View;
import android.widget.TextView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.DataStatusRecyclerAdapter;

/**
 * The view holder for the modules and addons.
 */
public class ModuleAddonViewHolder extends DataStatusRecyclerAdapter.StatusViewHolder {

    /**
     * The title used in title views of the adapter.
     */
    public TextView mTitleText;

    /**
     * The name used on addon views of the adapter.
     */
    public TextView mAddonText;

    /**
     * Name used for module views of the adapter.
     */
    public TextView mNameText;

    /**
     * Sub-name used for module views of the adapter.
     */
    public TextView mInfoText;

    /**
     * Constructor of the view holder.
     *
     * @param itemView The item that will be used to get the views.
     * @param viewType The view type to be sure which views are available.
     */
    public ModuleAddonViewHolder(View itemView, int viewType) {
        super(itemView, viewType);
        if (viewType == ModulesAdapter.MODULE_VIEW_TYPE) {
            mNameText = (TextView) itemView.findViewById(R.id.tv_title);
            mInfoText = (TextView) itemView.findViewById(R.id.tv_subtitle);
        } else if (viewType == ModulesAdapter.TITLE_VIEW_TYPE) {
            mTitleText = (TextView) itemView.findViewById(R.id.tv_title);
        } else if (viewType == ModulesAdapter.ADDON_VIEW_TYPE) {
            mAddonText = (TextView) itemView.findViewById(R.id.tv_addon);
        }
    }
}