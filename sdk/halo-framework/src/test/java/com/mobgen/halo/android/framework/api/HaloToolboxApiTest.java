package com.mobgen.halo.android.framework.api;


import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.mock.FrameworkMock;
import com.mobgen.halo.android.framework.toolbox.scheduler.Job;
import com.mobgen.halo.android.framework.toolbox.scheduler.Schedule;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.concurrent.TimeUnit;

import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class HaloToolboxApiTest extends HaloRobolectricTest {

    private HaloFramework mFramework;
    private HaloToolboxApi mToolbox;
    private CallbackFlag mCallbackFlag;
    private  boolean runEnded;

    @Before
    public void initialize() {
        mFramework = FrameworkMock.createSameThreadFramework("myTestEndpoint");
        mToolbox = mFramework.toolbox();
        mCallbackFlag = newCallbackFlag();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void thatEnqueueATaskAndRunTheTask(){
        runEnded = false;
        mToolbox.queue().enqueue(Threading.POOL_QUEUE_POLICY, new Runnable() {
            @Override
            public void run() {
                runEnded = true;
            }
        });
        assertThat(runEnded).isTrue();
    }

    @Test
    public void thatScheduleAJob(){
        Job job = givenAJob();
        mToolbox.schedule(job);
        assertThat(job.conditionsOk().isEmpty()).isFalse();
        assertThat(job.canBePersisted()).isFalse();
        assertThat(job.info()).isNotNull();
    }

    @Test
    public void thatScheduleAListOfJobs(){
        Job[] jobList = new Job[]{givenAJob(),givenAJob()};
        mToolbox.schedule(jobList);
        assertThat(jobList[0].conditionsOk().isEmpty()).isFalse();
        assertThat(jobList[0].info()).isNotNull();
        assertThat(jobList[1].conditionsOk().isEmpty()).isFalse();
        assertThat(jobList[0].info()).isNotNull();

    }

    @Test
    public void thatReturnFramework(){
        assertThat(mToolbox.framework()).isEqualTo(mFramework);
    }

    @NonNull
    private Job givenAJob() {
        return Job.builder(givenAMockedSchedule())
                .thread(Threading.POOL_QUEUE_POLICY)
                .deadline(0, TimeUnit.MILLISECONDS)
                .needCharging(false)
                .needDeviceIdle(false)
                .needsNetwork(Job.NETWORK_TYPE_NONE)
                .persist(false)
                .build();
    }

    public static Schedule givenAMockedSchedule() {
        Schedule scheduler = mock(Schedule.class);
        return scheduler;
    }



}
