package com.mobgen.halo.android.sdk.mock;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.api.HaloConfig;
import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.network.client.endpoint.HaloEndpoint;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.network.sessions.HaloSessionManager;
import com.mobgen.halo.android.framework.toolbox.scheduler.HaloJobScheduler;
import com.mobgen.halo.android.framework.toolbox.scheduler.Job;
import com.mobgen.halo.android.framework.toolbox.scheduler.Schedule;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.HaloCore;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;
import com.mobgen.halo.android.sdk.core.internal.parser.LoganSquareParserFactory;
import com.mobgen.halo.android.sdk.core.internal.startup.processes.StartupProcess;
import com.mobgen.halo.android.sdk.core.management.HaloManagerApi;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;
import com.mobgen.halo.android.sdk.core.management.segmentation.TagCollector;
import com.mobgen.halo.android.sdk.core.management.segmentation.TestDeviceCollector;
import com.mobgen.halo.android.sdk.mock.instrumentation.FrameworkMock;
import com.mobgen.halo.android.sdk.mock.instrumentation.TestThreadManager;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class HaloMock {

    public static HaloCore createCore(HaloFramework framework, Credentials credentials, List<TagCollector> tagCollectors) {
        return new HaloCore(framework, HaloManagerApi.with(Halo.instance()), new HaloSessionManager(), credentials, tagCollectors);
    }

    public static HaloCore createCore(HaloFramework framework, Credentials credentials) {
        return new HaloCore(framework, HaloManagerApi.with(Halo.instance()), new HaloSessionManager(), credentials, null);
    }

    public static HaloCore createCore(Credentials credentials, List<TagCollector> tagCollectors) {
        HaloFramework framework = HaloFramework.create(
                FrameworkMock.createSameThreadMockConfig(LoganSquareParserFactory.create())
                        .setParser(LoganSquareParserFactory.create())
        );
        return createCore(framework, credentials, tagCollectors);
    }

    public static HaloCore createCore(Credentials credentials) {
        HaloFramework framework = HaloFramework.create(
                FrameworkMock.createSameThreadMockConfig(LoganSquareParserFactory.create())
                        .setParser(LoganSquareParserFactory.create())
        );
        return createCore(framework, credentials);
    }

    public static Halo.Installer createInstaller() {
        return Halo.installer(RuntimeEnvironment.application).enableServiceOnBoot().channelServiceNotification("my channel", com.mobgen.halo.android.framework.R.drawable.ic_service_notification);
    }

    public static Halo create(HaloConfig.Builder configurationBuilder, HaloSessionManager sessionManager,
                              Credentials credentials, List<TagCollector> tagCollectors) {
        return createInstaller().install(configurationBuilder, sessionManager, credentials, tagCollectors, new StartupProcess[]{});
    }

    public static Halo create() {
        return create(FrameworkMock.createSameThreadMockConfig(LoganSquareParserFactory.create()), new HaloSessionManager(),
                Credentials.createClient("myClient", "myPassword"), new ArrayList<TagCollector>());
    }

    public static Halo create(List<TagCollector> collectors) {
        return create(FrameworkMock.createSameThreadMockConfig(LoganSquareParserFactory.create()), new HaloSessionManager(),
                Credentials.createClient("myClient", "myPassword"), collectors);
    }

    public static Halo create(String url) {
        return givenACustomHalo(givenASingleThreadedConfig(url));
    }

    public static Halo create(String url, List<TagCollector> collectors) {
        return givenACustomHalo(givenASingleThreadedConfig(url), collectors);
    }

    public static Halo givenACustomHalo(HaloConfig.Builder builder) {
        return Halo.installer(RuntimeEnvironment.application)
                .enableServiceOnBoot().channelServiceNotification("my channel", com.mobgen.halo.android.framework.R.drawable.ic_service_notification)
                .install(builder,
                        new HaloSessionManager(),
                        Credentials.createClient("mockClient", "mockPassword"),
                        null,
                        null
                );
    }

    public static Halo givenACustomHalo(HaloConfig.Builder builder, List<TagCollector> collectors) {
        return Halo.installer(RuntimeEnvironment.application)
                .enableServiceOnBoot().channelServiceNotification("my channel", com.mobgen.halo.android.framework.R.drawable.ic_service_notification)
                .install(builder,
                        new HaloSessionManager(),
                        Credentials.createClient("mockClient", "mockPassword"),
                        collectors,
                        null
                );
    }

    @NonNull
    public static HaloConfig.Builder givenASingleThreadedConfig(String urlEndpoint) {
        return givenASingleThreadedWithParserConfig(
                urlEndpoint,
                LoganSquareParserFactory.create());
    }

    @NonNull
    public static HaloConfig.Builder givenASingleThreadedWithParserConfig(String urlEndpoint, Parser.Factory parser) {
        return HaloConfig.builder(RuntimeEnvironment.application)
                .addEndpoint(new HaloEndpoint(HaloNetworkConstants.HALO_ENDPOINT_ID, urlEndpoint))
                .threadManager(new TestThreadManager())
                .setParser(parser)
                .setDisableKitKatCertificate(true)
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

    public static Halo.Installer givenAHaloInstaller() {
        Halo.Installer installer = Halo.installer(RuntimeEnvironment.application)
                .credentials(Credentials.createClient("clientId", "clientSecret"))
                .addTagCollector(mock(TestDeviceCollector.class))
                .endProcesses(mock(StartupProcess.class))
                .debug(true)
                .enableDefaultTags(true);

        installer.config().addEndpoint(new HaloEndpoint(HaloNetworkConstants.HALO_ENDPOINT_ID, "https://halourl.com"))
                .setDisableKitKatCertificate(true)
                .threadManager(new TestThreadManager())
                .setParser(LoganSquareParserFactory.create())
                .jobScheduler(givenAMockedScheduler());

        return installer;
    }
}
