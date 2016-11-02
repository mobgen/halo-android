package com.mobgen.halo.android.app.ui.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloFragment;
import com.mobgen.halo.android.sdk.media.HaloCloudinary;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FullScreenImageFragment extends MobgenHaloFragment {

    private static final String ARGUMENT_IMAGE = "argument_image";
    private static final String ARGUMENT_IMAGE_THUMB = "argument_image_thumb";

    private ImageView mImageView;
    private ProgressBar mLoader;
    private String mImage;
    private String mThumbnail;
    private HaloCloudinary.Effect mEffect;
    private PhotoViewAttacher mAttacher;

    public static FullScreenImageFragment create(@NonNull String image, @NonNull String thumbnail) {
        FullScreenImageFragment fragment = new FullScreenImageFragment();
        Bundle args = new Bundle();
        args.putString(ARGUMENT_IMAGE, image);
        args.putString(ARGUMENT_IMAGE_THUMB, thumbnail);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mImage = savedInstanceState.getString(ARGUMENT_IMAGE);
            mThumbnail = savedInstanceState.getString(ARGUMENT_IMAGE_THUMB);
        }

        if (mImage == null && getArguments() != null) {
            mImage = getArguments().getString(ARGUMENT_IMAGE);
            mThumbnail = getArguments().getString(ARGUMENT_IMAGE_THUMB);
        }

        if (mImage == null || mThumbnail == null) {
            throw new IllegalStateException("The fragment should be opened with a QROffer inside.");
        }

        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fullscreen_image, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageView = (ImageView) view.findViewById(R.id.iv_qr_offer);
        mLoader = (ProgressBar) view.findViewById(R.id.pb_loader);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        startLoading();
        mAttacher = new PhotoViewAttacher(mImageView);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPresenterInitialized() {
        super.onPresenterInitialized();
        stopLoading();
        mImageView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                updateImage();
                return true;
            }
        });
    }

    private String getImageUrl(){
        return HaloCloudinary.builder()
                .width(mImageView.getWidth())
                .height(mImageView.getHeight())
                .addEffects(mEffect)
                .crop(HaloCloudinary.CROP_MODE_FIT)
                .build(mImage).url();
    }

    private void updateImage(){
        startLoading();
        if(mImageView.getDrawable() == null) {
            Picasso.with(getActivity()).load(mThumbnail).into(mImageView, new Callback.EmptyCallback() {
                @Override
                public void onSuccess() {
                    stopLoading();
                    Picasso.with(getContext())
                            .load(getImageUrl()) // image url goes here
                            .placeholder(mImageView.getDrawable())
                            .into(mImageView, new EmptyCallback(){
                                @Override
                                public void onSuccess() {
                                    super.onSuccess();
                                    mAttacher.update();
                                }
                            });
                    mAttacher.update();
                }
            });
        }else{
            Picasso.with(getActivity()).load(getImageUrl()).placeholder(mImageView.getDrawable()).into(mImageView, new Callback() {
                @Override
                public void onSuccess() {
                    stopLoading();
                    mAttacher.update();
                }

                @Override
                public void onError() {
                    stopLoading();
                }
            });
        }
    }

    private void startLoading() {
        mLoader.setVisibility(View.VISIBLE);
    }

    private void stopLoading() {
        mLoader.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_gallery, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.effect_hue:
                mEffect = HaloCloudinary.Effects.hue(50);
                break;
            case R.id.effect_red:
                mEffect = HaloCloudinary.Effects.red(50);
                break;
            case R.id.effect_green:
                mEffect = HaloCloudinary.Effects.green(50);
                break;
            case R.id.effect_blue:
                mEffect = HaloCloudinary.Effects.blue(50);
                break;
            case R.id.effect_negate:
                mEffect = HaloCloudinary.Effects.negate();
                break;
            case R.id.effect_brightness:
                mEffect = HaloCloudinary.Effects.brightness(50);
                break;
            case R.id.effect_sepia:
                mEffect = HaloCloudinary.Effects.sepia(50);
                break;
            case R.id.effect_grayscale:
                mEffect = HaloCloudinary.Effects.grayscale();
                break;
            case R.id.effect_black_and_white:
                mEffect = HaloCloudinary.Effects.blackAndWhite();
                break;
            case R.id.effect_saturation:
                mEffect = HaloCloudinary.Effects.saturation(50);
                break;
            case R.id.effect_contrast:
                mEffect = HaloCloudinary.Effects.contrast(50);
                break;
            case R.id.effect_auto_contrast:
                mEffect = HaloCloudinary.Effects.autoContrast();
                break;
            case R.id.effect_autoColor:
                mEffect = HaloCloudinary.Effects.autoColor();
                break;
            case R.id.effect_improve:
                mEffect = HaloCloudinary.Effects.improve();
                break;
            case R.id.effect_auto_brightness:
                mEffect = HaloCloudinary.Effects.autoBrightness();
                break;
            case R.id.effect_vignette:
                mEffect = HaloCloudinary.Effects.vignette(50);
                break;
            case R.id.effect_gradient_fade:
                mEffect = HaloCloudinary.Effects.gradientFade(50);
                break;
            case R.id.effect_pixelate:
                mEffect = HaloCloudinary.Effects.pixelate(100);
                break;
            case R.id.effect_blur:
                mEffect = HaloCloudinary.Effects.blur(1000);
                break;
        }
        if(mEffect != null){
            item.setChecked(!item.isChecked());
        }
        updateImage();
        return true;
    }
}
