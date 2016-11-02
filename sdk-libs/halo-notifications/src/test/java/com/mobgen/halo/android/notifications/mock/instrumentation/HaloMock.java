package com.mobgen.halo.android.notifications.mock.instrumentation;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.api.HaloConfig;
import com.mobgen.halo.android.framework.network.client.endpoint.HaloEndpoint;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.network.sessions.HaloSessionManager;
import com.mobgen.halo.android.framework.toolbox.scheduler.HaloJobScheduler;
import com.mobgen.halo.android.framework.toolbox.scheduler.Job;
import com.mobgen.halo.android.framework.toolbox.scheduler.Schedule;
import com.mobgen.halo.android.framework.toolbox.threading.HaloThreadManager;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;
import com.mobgen.halo.android.sdk.core.internal.parser.LoganSquareParserFactory;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Method;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class HaloMock {

    public static Halo givenADefaultHalo() {
        return givenACustomHalo(givenASingleThreadedConfig());
    }

    public static Halo givenACustomHalo(HaloConfig.Builder builder) {
        return Halo.installer(RuntimeEnvironment.application)
                .install(builder,
                        new HaloSessionManager(),
                        Credentials.createClient("mockClient", "mockPassword"),
                        null,
                        null
                );
    }

    @NonNull
    public static HaloConfig.Builder givenASingleThreadedConfig() {
        return givenASingleThreadedWithParserConfig(
                null,
                LoganSquareParserFactory.create());
    }

    @NonNull
    public static HaloConfig.Builder givenASingleThreadedWithParserConfig(String urlEndpoint, Parser.Factory parser) {
        return HaloConfig.builder(RuntimeEnvironment.application)
                .addEndpoint(new HaloEndpoint(HaloNetworkConstants.HALO_ENDPOINT_ID, urlEndpoint))
                .threadManager(new HaloThreadManager() {
                    @Override
                    public Future enqueue(int thread, @NonNull Runnable runnable) {
                        Future<?> future = new FutureTask<>(runnable, null);
                        runnable.run();
                        return future;
                    }
                })
                .setParser(parser)
                .jobScheduler(givenAMockedScheduler());
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
}
