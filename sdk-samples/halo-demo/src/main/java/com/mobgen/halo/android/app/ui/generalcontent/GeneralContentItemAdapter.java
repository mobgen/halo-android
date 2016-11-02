package com.mobgen.halo.android.app.ui.generalcontent;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This adapter displays the generic information of an instance organized by fields.
 */
public class GeneralContentItemAdapter extends RecyclerView.Adapter<GeneralContentItemAdapter.GeneralContentItemViewHolder> {

    /**
     * Displays the string as a second parameter.
     */
    private static final int VIEW_TYPE_STRING = 0;

    /**
     * Displays the color.
     */
    private static final int VIEW_TYPE_COLOR = 1;

    /**
     * The image preview view type.
     */
    private static final int VIEW_TYPE_IMAGE = 2;

    /**
     * Youtube regular expression.
     */
    private static final String YOUTUBE_PATTERN = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|watch\\?v%3D|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";

    /**
     * The current activity context.
     */
    private Context mContext;

    /**
     * The last position shown.
     */
    private int lastPosition = -1;

    /**
     * The items to display.
     */
    private List<Pair<String, String>> mItems;

    /**
     * Constructor for this adapter.
     *
     * @param context  The context of the activity.
     * @param instance The instance to display in the sample app.
     */
    public GeneralContentItemAdapter(Context context, HaloContentInstance instance) {
        mContext = context;
        if (instance == null) {
            throw new IllegalArgumentException("The instance should not be null to be displayed in the adapter.");
        }
        //Get the list of items
        mItems = new ArrayList<>();
        if(instance.getValues() != null) {
            Iterator<String> it = instance.getValues().keys();
            while (it.hasNext()) {
                String key = it.next();
                mItems.add(new Pair<>(key, instance.getValues().opt(key).toString()));
            }
        }
    }

    public static String extractYoutubeId(String url) {
        String videoId = null;
        Pattern pattern = Pattern.compile(
                YOUTUBE_PATTERN,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            videoId = matcher.group();
        }
        return videoId;
    }

    @Override
    public GeneralContentItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int resource = 0;
        if (viewType == VIEW_TYPE_STRING) {
            resource = R.layout.generic_keyvalue;
        } else if (viewType == VIEW_TYPE_COLOR) {
            resource = R.layout.adapter_keyvalue_color;
        } else if (viewType == VIEW_TYPE_IMAGE) {
            resource = R.layout.adapter_keyvalue_url;
        }
        return new GeneralContentItemViewHolder(LayoutInflater.from(mContext).inflate(resource, parent, false), viewType);
    }

    @Override
    public void onBindViewHolder(GeneralContentItemViewHolder holder, int position) {
        final Pair<String, String> pair = mItems.get(position);
        holder.mKey.setText(pair.first);
        if (holder.getItemViewType() == VIEW_TYPE_STRING) {
            holder.mValue.setText(pair.second);
        } else if (holder.getItemViewType() == VIEW_TYPE_COLOR) {
            holder.mColorValue.setBackgroundColor(HaloUtils.getArgb(pair.second));
        } else if (holder.getItemViewType() == VIEW_TYPE_IMAGE) {
            String urlToLoad = pair.second;
            String youtubeId = extractYoutubeId(urlToLoad);
            if (youtubeId != null) {
                urlToLoad = String.format("http://img.youtube.com/vi/%s/default.jpg", youtubeId);
            }
            Picasso.with(mContext).load(urlToLoad).into(holder.mImagePreview);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(pair.second));
                    try {
                        mContext.startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(mContext, "There is not an application available to open this link", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    /**
     * Animate how the items enter in the screen.
     *
     * @param viewToAnimate The view that will be animated.
     * @param position      The new position.
     */
    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Pair<String, String> elem = mItems.get(position);
        if (HaloUtils.isColor(elem.second)) {
            return VIEW_TYPE_COLOR;
        }
        if (URLUtil.isNetworkUrl(elem.second)) {
            return VIEW_TYPE_IMAGE;
        } else {
            return VIEW_TYPE_STRING;
        }
    }

    /**
     * The view holder for the list items.
     */
    public class GeneralContentItemViewHolder extends RecyclerView.ViewHolder {

        /**
         * The key name view of the current item.
         */
        private TextView mKey;
        /**
         * The value name view of this item.
         */
        private TextView mValue;

        /**
         * The color value.
         */
        private View mColorValue;

        private ImageView mImagePreview;

        /**
         * Constructor for the view holder.
         *
         * @param itemView The item view.
         */
        public GeneralContentItemViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEW_TYPE_STRING) {
                mKey = (TextView) itemView.findViewById(R.id.tv_general_key);
                mValue = (TextView) itemView.findViewById(R.id.tv_general_value);
            } else if (viewType == VIEW_TYPE_COLOR) {
                mKey = (TextView) itemView.findViewById(R.id.tv_general_key);
                mColorValue = itemView.findViewById(R.id.v_color);
            } else if (viewType == VIEW_TYPE_IMAGE) {
                mKey = (TextView) itemView.findViewById(R.id.tv_general_key);
                mImagePreview = (ImageView) itemView.findViewById(R.id.iv_preview);
            }
        }
    }
}
