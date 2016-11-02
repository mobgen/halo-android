package com.mobgen.halo.android.app.ui;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.app.Fragment;

import com.mobgen.halo.android.app.model.MockAppConfiguration;
import com.mobgen.halo.android.app.module.ConfigurationModule;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.HaloReadyListener;

/**
 * Base activity for the halo mock application.
 */
public abstract class MobgenHaloFragment extends Fragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onPresenterInitialized();
    }

    /**
     * Method used to update data. This is used to avoid problems when halo is not initialized.
     */
    @CallSuper
    public void onPresenterInitialized() {
        loadConfiguration();
    }

    @Override
    public void onStart() {
        super.onStart();
        loadConfiguration();
    }

    private void loadConfiguration() {
        Halo.instance().ready(new HaloReadyListener() {
            @Override
            public void onHaloReady() {
                MockAppConfiguration configuration = ConfigurationModule.instance().getConfiguration();
                if (configuration != null) {
                    applyConfig(configuration);
                }
            }
        });
    }

    protected void applyConfig(MockAppConfiguration configuration) {
        //Applies the configuration
    }
}
