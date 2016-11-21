package com.mobgen.halo.android.app.ui.modules.partial;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.Addon;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.MobgenHaloFragment;
import com.mobgen.halo.android.app.ui.addons.SegmentationActivity;
import com.mobgen.halo.android.app.ui.analytics.AnalyticsActivity;
import com.mobgen.halo.android.app.ui.gallery.GalleryActivity;
import com.mobgen.halo.android.app.ui.generalcontent.GeneralContentItemActivity;
import com.mobgen.halo.android.app.ui.generalcontent.GeneralContentModuleActivity;
import com.mobgen.halo.android.app.ui.loadtests.LoadTestsActivity;
import com.mobgen.halo.android.app.ui.modules.adapters.ModulesAdapter;
import com.mobgen.halo.android.app.ui.news.ArticlesListActivity;
import com.mobgen.halo.android.app.ui.qr.QRListActivity;
import com.mobgen.halo.android.app.ui.settings.SettingsActivity;
import com.mobgen.halo.android.app.ui.social.SocialLoginActivity;
import com.mobgen.halo.android.app.ui.storelocator.StoreLocatorActivity;
import com.mobgen.halo.android.app.ui.translations.TranslationsActivity;
import com.mobgen.halo.android.app.utils.ViewUtils;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.search.SearchQueryBuilderFactory;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.HaloManagerApi;
import com.mobgen.halo.android.sdk.core.management.models.HaloModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Fragment that contains a list of modules and the behaviour to display different activities when they are
 * pressed.
 */
public class ModulesFragment extends MobgenHaloFragment implements SwipeRefreshLayout.OnRefreshListener,
        ModulesAdapter.ModuleAddonListener {

    /**
     * HaloModule list bundle.
     */
    private static final String BUNDLE_MODULE_LIST = "module_list_bundle";
    /**
     * The module list for status bundle.
     */
    private static final String BUNDLE_MODULE_LIST_STATUS = "module_list_status_bundle";
    /**
     * The list of modules.
     */
    private HaloResultV2<List<HaloModule>> mModuleList;
    /**
     * The recycler view that contains all the modules.
     */
    private RecyclerView mRecycler;

    /**
     * The swipe to refresh layout to update the modules.
     */
    private SwipeRefreshLayout mRefreshLayout;

    /**
     * The adapter with the modules.
     */
    private ModulesAdapter mAdapter;

    /**
     * The item for a single instance module.
     */
    private Map<String, HaloResultV2<HaloContentInstance>> mSingleInstanceItemMap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            List<HaloModule> moduleList = savedInstanceState.getParcelableArrayList(BUNDLE_MODULE_LIST);
            HaloStatus status = savedInstanceState.getParcelable(BUNDLE_MODULE_LIST_STATUS);
            if (status != null) {
                mModuleList = new HaloResultV2<>(status, moduleList);
            }
        }
        mSingleInstanceItemMap = new HashMap<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.generic_recycler_refresh, container, false);
        mRecycler = (RecyclerView) view.findViewById(R.id.rv_generic);
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl_generic);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRefreshLayout.setOnRefreshListener(this);
        mAdapter = new ModulesAdapter(getActivity());
        mAdapter.setOnModuleSelectedListener(this);
        mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycler.setAdapter(mAdapter);
        ViewUtils.refreshing(mRefreshLayout, true);
    }

    @Override
    public void onPresenterInitialized() {
        super.onPresenterInitialized();
        //Refresh the modules if it is empty or set the result
        if (mModuleList == null) {
            refreshModules();
        } else {
            mAdapter.setModules(mModuleList);
            ViewUtils.refreshing(mRefreshLayout, false);
        }

        //Add the addons
        List<Addon> addons = new ArrayList<>();
        addons.addAll(Arrays.asList(
                new Addon(Addon.AddonType.SEGMENTATION),
                new Addon(Addon.AddonType.ANALYTICS),
                new Addon(Addon.AddonType.SOCIAL_LOGIN)
        ));
        mAdapter.setAddons(addons);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Refreshes the modules by requesting them again.
     */
    public void refreshModules() {
        ViewUtils.refreshing(mRefreshLayout, true);
        HaloManagerApi.with(Halo.instance())
                .getModules(Data.NETWORK_AND_STORAGE)
                .asContent()
                .threadPolicy(Threading.POOL_QUEUE_POLICY)
                .execute(new CallbackV2<List<HaloModule>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<HaloModule>> result) {
                        ViewUtils.refreshing(mRefreshLayout, false);
                        mModuleList = result;
                        List<HaloModule> list = result.data();
                        if (list != null) {
                            for (HaloModule module : list) {
                                if (module.isSingleItemInstance() && mSingleInstanceItemMap.get(module.getInternalId()) == null) {
                                    requestSingleInstance(module);
                                }
                            }
                        }
                        mAdapter.setModules(mModuleList);
                        mAdapter.notifyDataSetChanged();
                        //Ensure the credentials error
                        if (result.status().isSecurityError()) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("The application credentials are not correct in HALO.")
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).show();
                        }
                    }
                });
    }

    private void requestSingleInstance(final HaloModule module) {
        SearchQuery options = SearchQueryBuilderFactory.getPublishedItems(module.getName(), module.getName())
                .onePage(true)
                .segmentWithDevice()
                .build();

        HaloContentApi.with(MobgenHaloApplication.halo())
                .search(Data.NETWORK_AND_STORAGE, options)
                .asContent()
                .execute(new CallbackV2<Paginated<HaloContentInstance>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<Paginated<HaloContentInstance>> result) {
                        if (result.status().isOk()) {
                            List<HaloContentInstance> data = result.data().data();
                            if (!data.isEmpty()) {
                                mSingleInstanceItemMap.put(module.getInternalId(), new HaloResultV2<>(result.status(), data.get(0)));
                            }
                        } else {
                            Halog.e(getClass(), "Impossible to retrieve the single instance for " + module.getName());
                        }
                    }
                });
    }

    /**
     * Refreshes the modules from Halo.
     */
    @Override
    public void onRefresh() {
        mAdapter.notifyDataSetChanged();
        refreshModules();
    }

    @Override
    public void onModuleSelected(HaloModule module) {
        if (module.getName().equalsIgnoreCase("store locator")) {
            StoreLocatorActivity.start(getContext(), module);
        } else if (module.getName().equalsIgnoreCase("news motorist")) {
            ArticlesListActivity.start(getContext(), module.getName());
        } else if (module.getName().equalsIgnoreCase("qroffer")) {
            QRListActivity.start(getContext(), module.getName());
        } else if (module.getName().equalsIgnoreCase("gallery")) {
            GalleryActivity.start(getContext(), module.getName());
        } else if (module.getName().equalsIgnoreCase("demo translations")) {
            TranslationsActivity.start(getContext());
        } else if (module.getName().equalsIgnoreCase("load tests")) {
            LoadTestsActivity.start(getContext(), module.getInternalId());
        } else if (module.isSingleItemInstance()) {
            HaloResultV2<HaloContentInstance> instance = mSingleInstanceItemMap.get(module.getInternalId());
            if (instance != null) {
                GeneralContentItemActivity.startActivity(getContext(), instance.data(), instance.status());
            } else {
                Toast.makeText(getContext(), getString(R.string.error_no_instance), Toast.LENGTH_LONG).show();
            }
        } else {
            GeneralContentModuleActivity.start(getContext(), module);
        }
    }

    @Override
    public void onAddonSelected(Addon addon) {
        if (addon.getType() == Addon.AddonType.SEGMENTATION) {
            SegmentationActivity.start(getContext());
        } else if (addon.getType() == Addon.AddonType.ANALYTICS) {
            AnalyticsActivity.startActivity(getContext());
        } else if (addon.getType() == Addon.AddonType.SOCIAL_LOGIN) {
            SocialLoginActivity.startActivity(getContext());
        }
    }

    @Override
    public void onSettingsSelected() {
        SettingsActivity.start(getContext());
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mModuleList != null) {
            outState.putParcelableArrayList(BUNDLE_MODULE_LIST, (ArrayList<? extends Parcelable>) mModuleList.data());
            outState.putParcelable(BUNDLE_MODULE_LIST_STATUS, mModuleList.status());
        }
        super.onSaveInstanceState(outState);
    }
}
