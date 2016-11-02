package com.mobgen.halo.android.framework.mock;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.api.HaloConfig;
import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.network.client.HaloNetClient;
import com.mobgen.halo.android.framework.network.client.endpoint.HaloEndpoint;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.toolbox.scheduler.Job;
import com.mobgen.halo.android.framework.toolbox.scheduler.Schedule;
import com.mobgen.halo.android.framework.toolbox.threading.HaloThreadManager;
import com.mobgen.halo.android.framework.toolbox.scheduler.HaloJobScheduler;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Method;
import java.util.concurrent.Future;

import okhttp3.OkHttpClient;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FrameworkMock {


    public static HaloFramework createSameThreadFramework(String endpointUrl) {
        return HaloFramework.create(createSameThreadMockConfig(mock(Parser.Factory.class),endpointUrl));
    }

    @NonNull
    public static HaloConfig.Builder createSameThreadMockConfig(Parser.Factory factory,String endpointUrl) {
        return HaloConfig.builder(RuntimeEnvironment.application).threadManager(new TestThreadManager())
                .setParser(factory)
                .jobScheduler(givenAMockedScheduler())
                .setDebug(true)
                .setOkClient(givenOkHttpBuilder())
                .addEndpoint(new HaloEndpoint("1",endpointUrl));
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

    public static HaloJobScheduler givenAMockedScheduler() {
        HaloJobScheduler scheduler = mock(HaloJobScheduler.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Job job = (Job) invocation.getArguments()[0];
                Schedule schedule = (Schedule) job.action();
                Method method = Schedule.class.getDeclaredMethod("execute");
                method.setAccessible(true);
                method.invoke(schedule);
                method.setAccessible(false);
                return null;
            }
        }).when(scheduler).schedule(any(Job.class));
        return scheduler;
    }

    public static OkHttpClient.Builder givenOkHttpBuilder(){
        return new OkHttpClient.Builder().build().newBuilder();
    }

    @NonNull
    public static HaloNetworkApi mockNetworkApi(Parser.Factory factory) {
        HaloNetworkApi clientApi = mock(HaloNetworkApi.class);
        HaloNetClient client = mock(HaloNetClient.class);
        when(clientApi.requestUrl(any(String.class), any(String.class))).thenReturn("http://google.es");
        when(clientApi.client()).thenReturn(client);
        when(clientApi.framework()).thenReturn(createSameThreadFramework("http://mytest"));
        return clientApi;
    }

    public static HaloNetworkApi givenAHaloNetWorkApi(HaloFramework mFramework,String endpointUrl){
        HaloConfig haloConfig = createSameThreadMockConfig(mock(Parser.Factory.class),endpointUrl).build();
        return HaloNetworkApi.newNetworkApi(mFramework,haloConfig);
    }
}
