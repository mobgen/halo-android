package com.mobgen.halo.android.sdk.mock.instrumentation;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.api.HaloConfig;
import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.network.client.HaloNetClient;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.toolbox.scheduler.HaloJobScheduler;
import com.mobgen.halo.android.framework.toolbox.threading.HaloThreadManager;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;

import org.robolectric.RuntimeEnvironment;

import java.util.concurrent.Future;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FrameworkMock {

    public static HaloFramework createSameThreadFramework(Parser.Factory parser) {
        return HaloFramework.create(createSameThreadMockConfig(parser));
    }

    @NonNull
    public static HaloConfig.Builder createSameThreadMockConfig(Parser.Factory factory) {
        return HaloConfig.builder(RuntimeEnvironment.application).threadManager(new TestThreadManager())
                .setDisableKitKatCertificate(true)
                .setParser(factory)
                .jobScheduler(mock(HaloJobScheduler.class));
    }

    @NonNull
    public static HaloConfig.Builder createMockConfig() {
        return HaloConfig.builder(RuntimeEnvironment.application).threadManager(new HaloThreadManager() {

            @Override
            public Future<?> enqueue(@Threading.Policy int thread, @NonNull final Runnable runnable) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        runnable.run();
                    }
                }, 100);
                return null;
            }
        });
    }

    @NonNull
    public static HaloNetworkApi mockNetworkApi(Parser.Factory factory) {
        HaloNetworkApi clientApi = mock(HaloNetworkApi.class);
        HaloNetClient client = mock(HaloNetClient.class);
        when(clientApi.requestUrl(any(String.class), any(String.class))).thenReturn("http://google.es");
        when(clientApi.client()).thenReturn(client);
        when(clientApi.framework()).thenReturn(createSameThreadFramework(factory));
        return clientApi;
    }
}
