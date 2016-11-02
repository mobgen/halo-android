package com.mobgen.halo.android.app.ui.qr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.QROffer;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.views.DividerItemDecoration;
import com.mobgen.halo.android.app.utils.ViewUtils;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.search.SearchQueryBuilderFactory;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.content.HaloContentApi;

import java.util.ArrayList;
import java.util.List;

public class QRListActivity extends MobgenHaloActivity implements QRAdapter.QRListener, SwipeRefreshLayout.OnRefreshListener {

    public static final String BUNDLE_MODULE_NAME = "module_name_bundle";
    public static final String BUNDLE_LIST_STATUS = "bundle_previous_list_status";
    public static final String BUNDLE_LIST = "bundle_previous_list";

    private HaloResultV2<List<QROffer>> mQROffers;
    private String mModuleName;

    private QRAdapter mAdapter;
    private SwipeRefreshLayout mRefreshLayout;

    public static void start(Context context, String moduleName) {
        Intent intent = new Intent(context, QRListActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_MODULE_NAME, moduleName);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras().containsKey(BUNDLE_MODULE_NAME)) {
            mModuleName = getIntent().getExtras().getString(BUNDLE_MODULE_NAME);
        } else {
            finish();
        }

        setContentView(R.layout.generic_recycler_refresh);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_generic);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.srl_generic);
        mAdapter = new QRAdapter(this, this);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.addItemDecoration(new DividerItemDecoration(this));
            recyclerView.setAdapter(mAdapter);
        }
        mRefreshLayout.setOnRefreshListener(this);
        ViewUtils.refreshing(mRefreshLayout, true);
    }

    @Override
    public void onPresenterInitialized() {
        super.onPresenterInitialized();
        ViewUtils.refreshing(mRefreshLayout, false);
        if (mQROffers == null) {
            loadQrData();
        } else {
            mAdapter.setQRList(mQROffers);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadQrData() {
        ViewUtils.refreshing(mRefreshLayout, true);

        SearchQuery options = SearchQueryBuilderFactory.getPublishedItems(mModuleName,mModuleName)
                .onePage(true)
                .segmentWithDevice()
                .build();
        HaloContentApi.with(MobgenHaloApplication.halo())
                .search(Data.NETWORK_AND_STORAGE, options)
                .asContent(QROffer.class)
                .execute(new CallbackV2<List<QROffer>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<QROffer>> result) {
                        ViewUtils.refreshing(mRefreshLayout, false);
                        if (result.status().isOk()) {
                            List<QROffer> offers = result.data();
                            if (offers != null) {
                                mQROffers = new HaloResultV2<>(result.status(), offers);
                                mAdapter.setQRList(mQROffers);
                                mAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(QRListActivity.this, "The QR articles could not be loaded.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onQRClicked(QROffer offer) {
        QRActivity.start(this, offer, mAdapter.getStatus());
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.qr_title);
    }

    @Override
    public void onRefresh() {
        loadQrData();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mQROffers != null) {
            outState.putParcelableArrayList(BUNDLE_LIST, (ArrayList<? extends Parcelable>) mQROffers.data());
            outState.putParcelable(BUNDLE_LIST_STATUS, mQROffers.status());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        List<QROffer> offers = savedInstanceState.getParcelableArrayList(BUNDLE_LIST);
        HaloStatus status = savedInstanceState.getParcelable(BUNDLE_LIST_STATUS);
        if (status != null) {
            mQROffers = new HaloResultV2<List<QROffer>>(status, offers);
        }
    }
}
