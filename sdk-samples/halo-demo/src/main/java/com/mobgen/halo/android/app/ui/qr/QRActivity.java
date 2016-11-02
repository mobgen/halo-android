package com.mobgen.halo.android.app.ui.qr;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.QROffer;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.common.FullScreenImageFragment;
import com.mobgen.halo.android.app.utils.DateUtils;
import com.mobgen.halo.android.app.utils.StatusInterceptor;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.api.Halo;
import com.squareup.picasso.Picasso;

public class QRActivity extends MobgenHaloActivity implements View.OnClickListener {

    public static final String BUNDLE_OFFER = "offer";
    public static final String BUNDLE_OFFER_STATUS = "offer_status";

    private QROffer mOffer;
    private HaloStatus mStatus;
    private QRViewHolder mViewHolder;

    public static void start(Context context, QROffer offer, HaloStatus offerStatus) {
        Intent intent = new Intent(context, QRActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_OFFER, offer);
        bundle.putParcelable(BUNDLE_OFFER_STATUS, offerStatus);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_qr_offer);
        mViewHolder = new QRViewHolder(getWindow().getDecorView());

        if (getIntent().getExtras() != null) {
            //Comes from the list and it has an article
            if (getIntent().getExtras().containsKey(BUNDLE_OFFER)) {
                mOffer = getIntent().getExtras().getParcelable(BUNDLE_OFFER);
            }
            if (getIntent().getExtras().containsKey(BUNDLE_OFFER_STATUS)) {
                mStatus = getIntent().getExtras().getParcelable(BUNDLE_OFFER_STATUS);
            }
        } else {
            finish();
        }
    }

    @Override
    public void onPresenterInitialized() {
        super.onPresenterInitialized();
        if (mOffer != null) {
            if (!TextUtils.isEmpty(mOffer.getTitle())) {
                setToolbarTitle(mOffer.getTitle());
                mViewHolder.mTitle.setText(mOffer.getTitle());
            }
            if (!TextUtils.isEmpty(mOffer.getArticle())) {
                mViewHolder.mOffer.loadData(mOffer.getArticle(), "text/html; charset=utf-8", "UTF-8");
            } else {
                mViewHolder.mOffer.setVisibility(View.GONE);
            }

            if (mOffer.getDate() != null) {
                mViewHolder.mDate.setText(DateUtils.formatDate(mOffer.getDate()));
            } else {
                mViewHolder.mDate.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mOffer.getQRImage())) {
                Picasso.with(this).load(mOffer.getQRImage()).into(mViewHolder.mQrImage);
                mViewHolder.mImageContainer.setOnClickListener(this);
            } else {
                mViewHolder.mQrImage.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mOffer.getThumbnail())) {
                Picasso.with(this).load(mOffer.getThumbnail()).into(mViewHolder.mOfferImage);
            } else {
                mViewHolder.mOfferImage.setVisibility(View.GONE);
            }

            mViewHolder.mActivateButton.setVisibility(View.VISIBLE);
            mViewHolder.mActivateButton.setOnClickListener(this);

            if (isRedeemed()) {
                redeem();
            }
        }
        StatusInterceptor.intercept(mStatus, mViewHolder.mStatusBar);
    }

    private void redeem() {
        mViewHolder.mActivateButton.setEnabled(false);
        mViewHolder.mActivateButton.setText(getString(R.string.qr_redeemed));
        mViewHolder.mQrImage.setVisibility(View.GONE);
        mViewHolder.mImageContainer.setOnClickListener(null);
        Halo.instance().getCore().manager().storage().prefs().edit().putBoolean(mOffer.getId(), true).apply();
    }

    private Boolean isRedeemed() {
        return  Halo.instance().getCore().manager().storage().prefs().getBoolean(mOffer.getId(), false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mOffer != null) {
            outState.putParcelable(BUNDLE_OFFER, mOffer);
        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mOffer = savedInstanceState.getParcelable(BUNDLE_OFFER);
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fl_image_click) {
            FullScreenImageFragment fragment = FullScreenImageFragment.create(mOffer.getQRImage(), mOffer.getQRImage());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fragment.setEnterTransition(new Slide(Gravity.TOP));
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_fragment_container, fragment)
                    .addToBackStack(null).commit();
        } else if (v.getId() == R.id.bt_activate) {
            redeem();
        }
    }

    private class QRViewHolder {
        private ImageView mQrImage;
        private ImageView mOfferImage;
        private TextView mTitle;
        private WebView mOffer;
        private TextView mDate;
        private Button mActivateButton;
        private View mImageContainer;
        private View mStatusBar;

        public QRViewHolder(View container) {
            mQrImage = (ImageView) container.findViewById(R.id.iv_qr_image);
            mOfferImage = (ImageView) container.findViewById(R.id.iv_offer);
            mActivateButton = (Button) container.findViewById(R.id.bt_activate);
            mTitle = (TextView) container.findViewById(R.id.tv_title);
            mOffer = (WebView) container.findViewById(R.id.wb_qr);
            mDate = (TextView) container.findViewById(R.id.tv_date);
            mImageContainer = container.findViewById(R.id.fl_image_click);
            mStatusBar = container.findViewById(R.id.v_status);
        }
    }
}
