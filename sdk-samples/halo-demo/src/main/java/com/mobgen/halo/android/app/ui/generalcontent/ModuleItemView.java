package com.mobgen.halo.android.app.ui.generalcontent;

import android.view.View;
import android.widget.TextView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.DataStatusRecyclerAdapter;

/**
 * View holder to hold the information for the adapter items.
 */
public class ModuleItemView extends DataStatusRecyclerAdapter.StatusViewHolder {

    /**
     * The module item name view.
     */
    public TextView mModuleItemName;

    /**
     * The author item view.
     */
    public TextView mModuleItemAuthor;

    /**
     * The constructor of the view holder.
     *
     * @param itemView The base view.
     */
    public ModuleItemView(View itemView, int viewType) {
        super(itemView, viewType);
        mModuleItemName = (TextView) itemView.findViewById(R.id.tv_title);
        mModuleItemAuthor = (TextView) itemView.findViewById(R.id.tv_subtitle);
    }
}