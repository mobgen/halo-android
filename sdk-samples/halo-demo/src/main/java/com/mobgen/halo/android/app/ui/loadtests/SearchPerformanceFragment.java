package com.mobgen.halo.android.app.ui.loadtests;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.MobgenHaloFragment;
import com.mobgen.halo.android.app.ui.generalcontent.HaloContentInstancesAdapter;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;

import icepick.State;

public class SearchPerformanceFragment extends MobgenHaloFragment implements View.OnClickListener {

    private static final String BUNDLE_MODULE = "bundle_module";

    private Button mSearchButton;
    private TextView mSearchTime;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    @State
    String mModuleId;

    private HaloContentInstancesAdapter mAdapter;

    public static SearchPerformanceFragment create(String moduleId) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_MODULE, moduleId);
        SearchPerformanceFragment fragment = new SearchPerformanceFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mModuleId = getArguments().getString(BUNDLE_MODULE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_performance, container, false);
        mSearchButton = (Button) view.findViewById(R.id.bt_retrieve_items);
        mSearchTime = (TextView) view.findViewById(R.id.tv_full_search);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_generic);
        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_loader);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new HaloContentInstancesAdapter(getContext());
        mRecyclerView.setAdapter(mAdapter);
        mSearchButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_retrieve_items) {
            retrieveSearchItems();
        }
    }

    private void retrieveSearchItems() {
        final long measurement = System.currentTimeMillis();
        mProgressBar.setVisibility(View.VISIBLE);

        SearchQuery options = SearchQuery.builder()
                .onePage(true)
                .moduleIds(mModuleId)
                .searchTag("performance_search")
                .segmentWithDevice()
                .build();
        HaloContentApi.with(MobgenHaloApplication.halo())
                .search(Data.NETWORK_AND_STORAGE, options)
                .asContent()
                .execute(new CallbackV2<Paginated<HaloContentInstance>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<Paginated<HaloContentInstance>> result) {
                        if (result.status().isOk()) {
                            if (result.data() != null) {
                                mAdapter.setModuleDataItems(new HaloResultV2<>(result.status(), result.data().data()));
                                mAdapter.notifyDataSetChanged();
                                mSearchTime.setText(String.valueOf(System.currentTimeMillis() - measurement));
                            }
                        }
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }
}
