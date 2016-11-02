package com.mobgen.halo.android.app.ui.modules.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.Addon;
import com.mobgen.halo.android.app.ui.DataStatusRecyclerAdapter;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.management.models.HaloModule;

import java.util.List;

/**
 * The modules and addons adapter. It contains all the elements used to access different parts of the application.
 */
public class ModulesAdapter extends DataStatusRecyclerAdapter<ModuleAddonViewHolder> {

    /**
     * The module view type id.
     */
    public static final int MODULE_VIEW_TYPE = 0;

    /**
     * The addon view type id.
     */
    public static final int ADDON_VIEW_TYPE = 1;

    /**
     * The title view type id.
     */
    public static final int TITLE_VIEW_TYPE = 2;

    /**
     * The number of titles that are being displayed. One for modules and one for addons.
     */
    public static final int NUM_TITLES = 3;

    /**
     * The modules and addons listener.
     */
    public interface ModuleAddonListener {

        /**
         * Callback when a module has been selected.
         *
         * @param module The module selected.
         */
        void onModuleSelected(HaloModule module);

        /**
         * Callback when an addon has been selected.
         *
         * @param addon The addon selected.
         */
        void onAddonSelected(Addon addon);

        /**
         * Selects the settings activity menu.
         */
        void onSettingsSelected();
    }

    /**
     * The array of modules dynamically provided.
     */
    private List<HaloModule> mModules;

    /**
     * The array of addons dynamically provided.
     */
    private List<Addon> mAddons;

    /**
     * The listener to take an action when somethin is clicked.
     */
    private ModuleAddonListener mListener;

    /**
     * The adapter constructor.
     *
     * @param ctx The context.
     */
    public ModulesAdapter(Context ctx) {
        super(ctx);
    }

    /**
     * Sets the remote modules from the activity.
     *
     * @param modules The modules.
     */
    public void setModules(HaloResultV2<List<HaloModule>> modules) {
        mModules = modules.data();
        setStatus(modules.status());
    }

    /**
     * Sets the addons from the activity.
     *
     * @param addons The addons.
     */
    public void setAddons(List<Addon> addons) {
        mAddons = addons;
    }

    /**
     * Provides a null safe module size.
     *
     * @return The module size.
     */
    private int getModulesSize() {
        int modulesSize = 0;
        if (mModules != null) {
            modulesSize = mModules.size();
        }
        return modulesSize;
    }

    /**
     * Provides a null safe addons size.
     *
     * @return The addon size.
     */
    private int getAddonsSize() {
        int addonsSize = 0;
        if (mAddons != null) {
            addonsSize = mAddons.size();
        }
        return addonsSize;
    }

    @Override
    public ModuleAddonViewHolder onCreateOverViewholder(ViewGroup parent, int viewType) {
        View elem;
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        if (viewType == MODULE_VIEW_TYPE) {
            elem = layoutInflater.inflate(R.layout.generic_title_subtitle_white, parent, false);
        } else if (viewType == TITLE_VIEW_TYPE) {
            elem = layoutInflater.inflate(R.layout.adapter_title, parent, false);
        } else if (viewType == ADDON_VIEW_TYPE) {
            elem = layoutInflater.inflate(R.layout.adapter_addon, parent, false);
        } else {
            throw new IllegalArgumentException("The view type has an illegal value for this adapter.");
        }
        return new ModuleAddonViewHolder(elem, viewType);
    }

    @Override
    public void onBindOverViewHolder(ModuleAddonViewHolder holder, int position) {
        if (holder.getItemViewType() == MODULE_VIEW_TYPE) {
            final HaloModule module = mModules.get(position - 1);
            holder.mNameText.setText(module.getName());
            holder.mInfoText.setText("General content");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onModuleSelected(module);
                    }
                }
            });
        } else if (holder.getItemViewType() == TITLE_VIEW_TYPE) {
            if (position == 0) {
                holder.mTitleText.setText(getContext().getString(R.string.modules));
            } else if (position == getModulesSize() + 1) {
                holder.mTitleText.setText(getContext().getString(R.string.addons));
            } else {
                holder.mTitleText.setText(getContext().getString(R.string.settings_title));
            }
        } else if (holder.getItemViewType() == ADDON_VIEW_TYPE) {
            if (position == getOverItemCount() - 1) { // Settings menu
                holder.mAddonText.setText(getContext().getString(R.string.settings_info));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onSettingsSelected();
                        }
                    }
                });
            } else {
                final Addon addon = mAddons.get(position - getModulesSize() - 2);
                holder.mAddonText.setText(getContext().getString(addon.getType().getStringResource()));
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onAddonSelected(addon);
                        }
                    }
                });
            }
        }
    }

    @Override
    public int getOverItemViewType(int position) {
        if (position == 0 || position == getModulesSize() + 1 || position == getModulesSize() + getAddonsSize() + 2) {
            return TITLE_VIEW_TYPE;
        } else if (position <= getModulesSize()) {
            return MODULE_VIEW_TYPE;
        } else {
            return ADDON_VIEW_TYPE;
        }
    }

    @Override
    public int getOverItemCount() {
        return getModulesSize() + getAddonsSize() + NUM_TITLES + 1; // + 1 Settings
    }

    /**
     * Sets the listener on the adapter.
     *
     * @param onModuleSelectedListener The listener to use on the adapter.
     */
    public void setOnModuleSelectedListener(ModuleAddonListener onModuleSelectedListener) {
        mListener = onModuleSelectedListener;
    }


}
