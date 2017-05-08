package com.mobgen.halo.android.app.ui.batchimages;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.BatchImage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BatchImageAdapter extends RecyclerView.Adapter<BatchImageAdapter.GalleryViewHolder> {

    private static final int CHUNK_SIZE = 2;
    private List<List<BatchImage>> mImageChunks;
    private Context mContext;
    private TextChangeListener mListener;

    public BatchImageAdapter(@NonNull Context context, @Nullable TextChangeListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void setImages(@Nullable List<BatchImage> images) {
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
        final GalleryViewHolder viewHolder = new GalleryViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_batch_gallery, parent, false));
        int index = 0;
        for (final ImageView view : viewHolder.mImageViews) {
            final int finalIndex = index;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int adapterPosition = viewHolder.getAdapterPosition();
                    List<BatchImage> images = mImageChunks.get(adapterPosition);
                    if (finalIndex < images.size()) {
                        if (images.get(finalIndex).isSelected()) {
                            view.setBackgroundColor(Color.TRANSPARENT);
                            images.get(finalIndex).setSelected(false);
                        } else {
                            view.setBackgroundColor(ContextCompat.getColor(mContext, R.color.orange_mobgen));
                            images.get(finalIndex).setSelected(true);
                        }
                    }
                }
            });
            index++;
        }

        index = 0;
        for (final EditText view : viewHolder.mEditTextViews) {
            final int finalIndex = index;
            view.clearFocus();
            if (mListener != null) {
                view.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        int adapterPosition = viewHolder.getAdapterPosition();
                        int arrayindex = (CHUNK_SIZE * adapterPosition) + finalIndex;
                        List<BatchImage> batchImages = mImageChunks.get(adapterPosition);
                        batchImages.get(finalIndex).author(s.toString());
                        mListener.onTextChange(batchImages.get(finalIndex), arrayindex);
                    }
                });
            } else {
                view.setVisibility(View.GONE);
            }
            index++;
        }
        return viewHolder;
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
        private EditText[] mEditTextViews;

        public GalleryViewHolder(View itemView) {
            super(itemView);
            mImageViews = new ImageView[]{
                    (ImageView) itemView.findViewById(R.id.iv_gallery_1),
                    (ImageView) itemView.findViewById(R.id.iv_gallery_2)
            };
            mEditTextViews = new EditText[]{
                    (EditText) itemView.findViewById(R.id.et_gallery_1),
                    (EditText) itemView.findViewById(R.id.et_gallery_2)
            };
        }

        public void bind(@NonNull List<BatchImage> galleryImages, @NonNull final Context context) {
            int imageIndex = 0;
            for (final BatchImage image : galleryImages) {
                final ImageView imageView = mImageViews[imageIndex];
                if (image.isSelected()) {
                    imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.orange_mobgen));
                } else {
                    imageView.setBackgroundColor(Color.TRANSPARENT);
                }
                String url = image.image();
                imageView.setVisibility(View.VISIBLE);
                imageView.setTag(url);
                Picasso.with(context).load(url).placeholder(ContextCompat.getDrawable(context, R.color.light_gray)).into(imageView);
                final EditText editText = mEditTextViews[imageIndex];
                editText.setVisibility(View.VISIBLE);
                editText.setText(image.author());
                imageIndex++;
            }
            for (int i = imageIndex; i < CHUNK_SIZE; i++) {
                mImageViews[i].setImageBitmap(null);
            }
        }
    }

    public interface TextChangeListener {
        void onTextChange(BatchImage batchImage, int position);
    }
}
