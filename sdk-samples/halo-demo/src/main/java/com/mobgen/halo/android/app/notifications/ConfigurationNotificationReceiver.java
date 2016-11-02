package com.mobgen.halo.android.app.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.app.model.MockAppConfiguration;
import com.mobgen.halo.android.app.module.ConfigurationModule;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;

/**
 * Broadcast receiver to listen for configuration changes.
 */
public class ConfigurationNotificationReceiver extends BroadcastReceiver {

    public interface ConfigurationChangeListener {
        void onConfigurationChanged(MockAppConfiguration newConfiguration);
    }

    private ConfigurationChangeListener mListener;
    private ConfigurationModule mConfigModule;

    public ConfigurationNotificationReceiver(@NonNull ConfigurationChangeListener listener, ConfigurationModule module) {
        mListener = listener;
        mConfigModule = module;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mConfigModule.loadConfiguration(Threading.POOL_QUEUE_POLICY, Halo.instance(), new CallbackV2<MockAppConfiguration>() {
            @Override
            public void onFinish(@NonNull HaloResultV2<MockAppConfiguration> result) {
                mListener.onConfigurationChanged(result.data());
            }
        });
    }
}
