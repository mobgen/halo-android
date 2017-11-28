package com.mobgen.halo.android.framework.mock.instrumentation;


import android.content.ComponentName;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.scheduler.HaloJobScheduler;
import com.mobgen.halo.android.framework.toolbox.scheduler.IHaloSchedulerServiceBinder;
import com.mobgen.halo.android.framework.toolbox.scheduler.Job;
import com.mobgen.halo.android.framework.toolbox.scheduler.Schedule;
import com.mobgen.halo.android.framework.toolbox.scheduler.Trigger;
import com.mobgen.halo.android.framework.toolbox.threading.DefaultThreadManager;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.testing.CallbackFlag;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.robolectric.Shadows.shadowOf;

public class HaloThreadInstrument {

    public static Runnable givenARunnable(final CallbackFlag flag) {
        return new Runnable() {
            @Override
            public void run() {
                assertThat(flag.isFlagged());
            }
        };
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

    public static HaloJobScheduler givenAHaloJobScheduler() {
        final ComponentName componentName = new ComponentName("com.mobgen.halo.android.testing", "HaloJobScheduler");
        IHaloSchedulerServiceBinder.Stub stubBinder = mock(IHaloSchedulerServiceBinder.Stub.class);
        shadowOf(RuntimeEnvironment.application).setComponentNameAndServiceForBindService(componentName, stubBinder);
        HaloJobScheduler haloJobScheduler = new HaloJobScheduler(RuntimeEnvironment.application, new DefaultThreadManager());
        return haloJobScheduler;
    }

    public static Schedule givenASchedule(final CallbackFlag flag) {
        return new Schedule() {
            @Override
            protected void execute() {
                flag.flagExecuted();
                assertThat(flag.isFlagged()).isTrue();
            }
        };
    }

    public static Job givenAJob(CallbackFlag flag, String tag) {
        return Job.builder(givenASchedule(flag))
                .deadline(5, TimeUnit.SECONDS)
                .needCharging(false)
                .needDeviceIdle(false)
                .persist(false)
                .tag(tag)
                .repeat(60 * 60 * 1000)
                .needsNetwork(Job.NETWORK_TYPE_ANY)
                .thread(Threading.POOL_QUEUE_POLICY)
                .build();
    }

    public static Job givenAJobWithTrigger(CallbackFlag flag) {
        MyTrigger trigger = new MyTrigger();
        return Job.builder(givenASchedule(flag))
                .needCharging(false)
                .needDeviceIdle(false)
                .persist(false)
                .tag("tag")
                .trigger(trigger)
                .needsNetwork(Job.NETWORK_TYPE_NONE)
                .thread(Threading.POOL_QUEUE_POLICY)
                .build();
    }

    public static Job givenAJobWithoutDeadline(CallbackFlag flag) {
        return Job.builder(givenASchedule(flag))
                .needCharging(false)
                .needDeviceIdle(false)
                .persist(true)
                .tag("tag")
                .needsNetwork(Job.NETWORK_TYPE_ANY)
                .thread(Threading.POOL_QUEUE_POLICY)
                .build();
    }

    public static class MyTrigger extends Trigger {

        @NonNull
        @Override
        public String[] getAction() {
            return new String[]{"action1"};
        }
    }

}
