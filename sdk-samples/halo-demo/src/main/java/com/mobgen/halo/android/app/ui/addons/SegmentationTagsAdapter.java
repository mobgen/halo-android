package com.mobgen.halo.android.app.ui.addons;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloSegmentationTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Adapter that displays the segmentation tags in badges.
 */
public class SegmentationTagsAdapter extends RecyclerView.Adapter<SegmentationTagsAdapter.SegmentationTagsViewHolder> {

    /**
     * The context for this adapter.
     */
    private Context mContext;

    /**
     * The current segmentation tags.
     */
    private List<HaloSegmentationTag> mSegmentationTags;

    /**
     * Constructor of the adapter.
     *
     * @param context The context to create the views.
     */
    public SegmentationTagsAdapter(Context context) {
        mContext = context;
        mSegmentationTags = new ArrayList<>();
    }

    /**
     * Sets the tags.
     *
     * @param tags The tags.
     */
    public void setTags(List<HaloSegmentationTag> tags) {
        //Sort the tags
        Collections.sort(tags);
        mSegmentationTags = tags;
    }

    @Override
    public SegmentationTagsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SegmentationTagsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.generic_keyvalue, parent, false));
    }

    @Override
    public void onBindViewHolder(SegmentationTagsViewHolder holder, int position) {
        final HaloSegmentationTag tag = mSegmentationTags.get(position);
        holder.mSegmentationTagName.setText(tag.getName());
        if (tag.getValue() != null) {
            holder.mSegmentationTagValue.setText(tag.getValue().toString());
        }
    }

    @Override
    public int getItemCount() {
        return mSegmentationTags.size();
    }

    /**
     * Provides the tag in the given position.
     *
     * @param adapterPosition The adapter position.
     * @return The segmentation tag element.
     */
    public HaloSegmentationTag getTagAt(int adapterPosition) {
        return mSegmentationTags.get(adapterPosition);
    }

    /**
     * View holder class for the segmentation tag representation.
     */
    public class SegmentationTagsViewHolder extends RecyclerView.ViewHolder {

        /**
         * The segmentation name view.
         */
        private TextView mSegmentationTagName;

        /**
         * The segmentation value view.
         */
        private TextView mSegmentationTagValue;

        /**
         * Segmentation view holder constructor.
         *
         * @param itemView The view on which the references are.
         */
        public SegmentationTagsViewHolder(View itemView) {
            super(itemView);
            mSegmentationTagName = (TextView) itemView.findViewById(R.id.tv_general_key);
            mSegmentationTagValue = (TextView) itemView.findViewById(R.id.tv_general_value);
        }
    }
}
