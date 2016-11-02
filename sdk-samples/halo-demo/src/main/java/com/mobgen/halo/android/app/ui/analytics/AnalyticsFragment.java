package com.mobgen.halo.android.app.ui.analytics;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.MobgenHaloFragment;


/**
 * Fragment for the analytics demo.
 */
public class AnalyticsFragment extends MobgenHaloFragment implements View.OnClickListener, TextWatcher {

    @StringDef({FIREBASE_PROVIDER, HALO_PROVIDER})
    public @interface Provider {

    }

    public static final String FIREBASE_PROVIDER = "firebase";

    public static final String HALO_PROVIDER = "halo";

    private static final String BUNDLE_ARGUMENT = "bundle_provider";
    private String mProvider;

    //private HaloAnalyticsApi mAnalytics;

    private AppCompatEditText mProductName;
    private AppCompatEditText mProductCategory;
    private AppCompatEditText mProductPrice;
    private AppCompatEditText mProductQuantity;
    private Button mSendButton;

    public static AnalyticsFragment create(@NonNull @Provider String provider) {
        AnalyticsFragment fragment = new AnalyticsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_ARGUMENT, provider);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null) {
            throw new IllegalArgumentException("You must use the create() method to create a new instance.");
        }
        mProvider = getArguments().getString(BUNDLE_ARGUMENT);
        if (mProvider == null) {
            throw new NullPointerException("Don't create a null provider.");
        }
        /*mAnalytics = HaloAnalyticsApi.with(MobgenHaloApplication.halo())
                .provider(createProvider())
                .build();*/
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics_provider, container, false);
        mProductName = (AppCompatEditText) view.findViewById(R.id.et_product_name);
        mProductCategory = (AppCompatEditText) view.findViewById(R.id.et_product_category);
        mProductQuantity = (AppCompatEditText) view.findViewById(R.id.et_product_quantity);
        mProductPrice = (AppCompatEditText) view.findViewById(R.id.et_product_price);
        mSendButton = (Button) view.findViewById(R.id.bt_send);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSendButton.setOnClickListener(this);
        mProductName.addTextChangedListener(this);
        mProductCategory.addTextChangedListener(this);
        mProductQuantity.addTextChangedListener(this);
        mProductPrice.addTextChangedListener(this);
        mSendButton.setEnabled(isValidTag());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_send) {
            //mAnalytics.logTransactionAnalytic(createAnalyticFromFields());
            mProductName.getText().clear();
            mProductCategory.getText().clear();
            mProductQuantity.getText().clear();
            mProductPrice.getText().clear();
            mSendButton.setEnabled(isValidTag());
        }
    }
/*
    private TransactionAnalytic createAnalyticFromFields() {
        return TransactionAnalytic.builder(Analytic.Type.ECOMMERCE_PURCHASE)
                .productName(mProductName.getText().toString())
                .productCategory(mProductCategory.getText().toString())
                .quantity(Integer.parseInt(mProductQuantity.getText().toString()))
                .price(Float.parseFloat(mProductPrice.getText().toString()))
                .build();
    }*/

    private boolean isValidTag() {
        boolean isValid = true;
        if (mProductName.getText().length() == 0) {
            isValid = false;
        }
        if (mProductCategory.getText().length() == 0) {
            isValid = false;
        }
        if (mProductQuantity.getText().length() == 0) {
            isValid = false;
        }
        if (mProductPrice.getText().length() == 0) {
            isValid = false;
        }
        return isValid;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //Do nothing here
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mSendButton.setEnabled(isValidTag());
    }

    @Override
    public void afterTextChanged(Editable s) {
        //Do nothing here
    }

    /*private AnalyticsProvider createProvider() {
        AnalyticsProvider provider = null;
        if (mProvider.equals(FIREBASE_PROVIDER)) {
            provider = new FirebaseAnalyticsProvider(getContext());
        } else if (mProvider.equals(HALO_PROVIDER)) {
            provider = new HaloAnalyticsProvider(getContext());
        }
        return provider;
    }*/
}
