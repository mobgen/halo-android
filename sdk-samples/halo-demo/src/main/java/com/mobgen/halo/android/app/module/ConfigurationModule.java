package com.mobgen.halo.android.app.module;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.app.model.MockAppConfiguration;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.search.SearchQueryBuilderFactory;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.startup.processes.StartupProcess;
import com.mobgen.halo.android.sdk.core.management.HaloManagerApi;
import com.mobgen.halo.android.sdk.core.management.models.HaloModule;

import java.util.List;

public class ConfigurationModule extends StartupProcess {

    private static final String CONFIGURATION_MODULE = "ColorConfiguration";
    /**
     * The current configuration module to access it.
     */
    private static ConfigurationModule sModule;

    /**
     * The mock application configuration.
     */
    private MockAppConfiguration mConfiguration;

    public ConfigurationModule() {
        sModule = this;
    }

    /**
     * Loads the configuration from the given module.
     *
     * @param callback The callback.
     */
    public void loadConfiguration(@Threading.Policy final int policy, final Halo halo, @Nullable final CallbackV2<MockAppConfiguration> callback) {
        SearchQuery options = SearchQueryBuilderFactory.getPublishedItems(CONFIGURATION_MODULE, CONFIGURATION_MODULE)
                .onePage(true)
                .segmentWithDevice()
                .build();

        HaloContentApi.with(halo)
                .search(Data.NETWORK_AND_STORAGE, options)
                .asContent(MockAppConfiguration.class)
                .bypassHaloReadyCheck()
                .threadPolicy(policy)
                .execute(new CallbackV2<List<MockAppConfiguration>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<List<MockAppConfiguration>> result) {
                        if (result.status().isOk()) {
                            List<MockAppConfiguration> data = result.data();
                            if (data != null) {
                                mConfiguration = data.get(0);
                                if (callback != null) {
                                    callback.onFinish(new HaloResultV2<>(result.status(), mConfiguration));
                                }
                            }
                        }
                    }
                });
    }

    public static ConfigurationModule instance() {
        return sModule;
    }

    @Nullable
    public MockAppConfiguration getConfiguration() {
        return mConfiguration;
    }

    @Override
    public int getThreadPolicy() {
        return Threading.POOL_QUEUE_POLICY;
    }

    @Override
    protected void onStart(@NonNull Halo halo) {
        loadConfiguration(Threading.SAME_THREAD_POLICY, halo, null);
    }
}
