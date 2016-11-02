package com.mobgen.halo.android.app.ui.gallery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.GalleryImage;
import com.mobgen.halo.android.sdk.media.HaloCloudinary;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {

    private static final int CHUNK_SIZE = 3;
    private List<List<GalleryImage>> mImageChunks;
    private Context mContext;
    private ImageSelectionListener mListener;

    public GalleryAdapter(@NonNull Context context, @Nullable ImageSelectionListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setImages(@Nullable List<GalleryImage> images) {
        if (images != null) {
            int numGroups = images.size() % CHUNK_SIZE;
            mImageChunks = new ArrayList<>(numGroups);
            for (int i = 0; i < images.size(); i += CHUNK_SIZE) {
                int end = Math.min(images.size(), i + CHUNK_SIZE);
                mImageChunks.add(images.subList(i, end));
            }
        } else {
            mImageChunks = null;
        }
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final GalleryViewHolder viewHolder = new GalleryViewHolder(LayoutInflater.from(mContext).inflate(getLayout(viewType), parent, false));
        int index = 0;
        for(final ImageView view : viewHolder.mImageViews){
            final int finalIndex = index;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = viewHolder.getAdapterPosition();
                    List<GalleryImage> images = mImageChunks.get(adapterPosition);
                    if(finalIndex < images.size() && mListener != null){
                        mListener.onImageSelected(images.get(finalIndex).url(), (String) view.getTag());
                    }
                }
            });
            index++;
        }
        return viewHolder;
    }

    private int getLayout(int viewType) {
        if (viewType % 2 == 0) {
            return R.layout.adapter_even_gallery;
        } else {
            return R.layout.adapter_odd_gallery;
        }
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {
        holder.bind(mImageChunks.get(position), mContext);
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    @Override
    public int getItemCount() {
        if (mImageChunks != null) {
            return mImageChunks.size();
        }
        return 0;
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {

        private ImageView[] mImageViews;

        public GalleryViewHolder(View itemView) {
            super(itemView);
            mImageViews = new ImageView[]{
                    (ImageView) itemView.findViewById(R.id.iv_gallery_1),
                    (ImageView) itemView.findViewById(R.id.iv_gallery_2),
                    (ImageView) itemView.findViewById(R.id.iv_gallery_3)
            };
        }

        public void bind(@NonNull List<GalleryImage> galleryImages, @NonNull final Context context) {
            int imageIndex = 0;
            for (final GalleryImage image : galleryImages) {
                final ImageView imageView = mImageViews[imageIndex];
                imageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                        String url = HaloCloudinary.builder()
                                .width(imageView.getWidth())
                                .height(imageView.getHeight())
                                .crop(HaloCloudinary.CROP_MODE_THUMB)
                                .build(image.url()).url();
                        imageView.setTag(url);
                        Picasso.with(context).load(url).into(imageView);
                        return true;
                    }
                });
                imageIndex++;
            }
            for(int i = imageIndex; i < CHUNK_SIZE; i++){
                mImageViews[i].setImageBitmap(null);
            }
        }
    }

    public interface ImageSelectionListener {
        void onImageSelected(String originalUrl, String thumUrl);
    }
}
