package com.mobgen.halo.android.framework.toolbox.scheduler;


import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.mock.FrameworkMock;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import java.lang.reflect.Field;
import java.util.HashMap;

import static com.mobgen.halo.android.framework.mock.instrumentation.HaloThreadInstrument.givenAHaloJobScheduler;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloThreadInstrument.givenAJob;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloJobSchedulerTest extends HaloRobolectricTest {

    private CallbackFlag mCallbackFlag;
    private HaloFramework mFramework;

    @Before
    public void initialize() {
        mFramework = FrameworkMock.createSameThreadFramework("url");
        mCallbackFlag =  new CallbackFlag();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void thatCanScheduleAJob() throws NoSuchFieldException, IllegalAccessException {
        HaloJobScheduler haloJobScheduler = givenAHaloJobScheduler();
        Job job = givenAJob(mCallbackFlag,"job");
        haloJobScheduler.schedule(job);
        HashMap<String, Job> pendingList = getPendingListByReflection(haloJobScheduler);
        assertThat(pendingList.containsKey(job.info().mTag)).isFalse();
    }

    @Test
    public void thatCanScheduleAListOfJobs() throws NoSuchFieldException, IllegalAccessException {
        HaloJobScheduler haloJobScheduler = givenAHaloJobScheduler();
        Job job1 = givenAJob(mCallbackFlag,"job1");
        Job job2 = givenAJob(mCallbackFlag,"job2");
        Job [] jobs = new Job[] {job1,job2};
        haloJobScheduler.schedule(jobs);
        HashMap<String, Job> pendingList = getPendingListByReflection(haloJobScheduler);
        assertThat(pendingList.containsKey(job1.info().mTag)).isFalse();
        assertThat(pendingList.containsKey(job2.info().mTag)).isFalse();
    }

    @Test
    public void thatCanCancelAJob() throws NoSuchFieldException, IllegalAccessException {
        HaloJobScheduler haloJobScheduler = givenAHaloJobScheduler();
        Job jobToCancel = givenAJob(mCallbackFlag,"jobToCancel");
        Job job1 = givenAJob(mCallbackFlag,"job1");
        Job job2 = givenAJob(mCallbackFlag,"job2");
        Job [] jobs = new Job[] {job1,job2,jobToCancel};
        haloJobScheduler.schedule(jobs);
        haloJobScheduler.cancel("jobToCancel");
        HashMap<String, Job> pendingList = getPendingListByReflection(haloJobScheduler);
        assertThat(pendingList.containsKey(jobToCancel.info().mTag)).isFalse();
    }

    @Test
    public void thatCanStopAndResetAJob() throws NoSuchFieldException, IllegalAccessException {
        HaloJobScheduler haloJobScheduler = givenAHaloJobScheduler();
        Job job1 = givenAJob(mCallbackFlag,"job1");
        Job job2 = givenAJob(mCallbackFlag,"job2");
        Job job3 = givenAJob(mCallbackFlag,"jo3");
        Job [] jobs = new Job[] {job1,job2,job3};
        haloJobScheduler.schedule(jobs);
        haloJobScheduler.stopAndReset();
        HashMap<String, Job> pendingList = getPendingListByReflection(haloJobScheduler);
        assertThat(pendingList.isEmpty()).isTrue();
    }

    @Test
    public void thatCloseDoorToNewJobs() throws NoSuchFieldException, IllegalAccessException {
        HaloJobScheduler haloJobScheduler = givenAHaloJobScheduler();
        Job job1 = givenAJob(mCallbackFlag,"job1");
        Job job2 = givenAJob(mCallbackFlag,"job2");
        Job job3 = givenAJob(mCallbackFlag,"jo3");
        Job [] jobs = new Job[] {job1,job2,job3};
        haloJobScheduler.schedule(jobs);
        haloJobScheduler.closeDoor();
        haloJobScheduler.schedule(jobs);
        HashMap<String, Job> pendingList = getPendingListByReflection(haloJobScheduler);
        assertThat(pendingList.isEmpty()).isFalse();
    }

    private HashMap<String, Job> getPendingListByReflection(HaloJobScheduler haloJobScheduler) throws NoSuchFieldException, IllegalAccessException {
        Field entityRenderMapField = null;
        entityRenderMapField = HaloJobScheduler.class.getDeclaredField("mPendingList");
        entityRenderMapField.setAccessible(true);
        return (HashMap<String, Job>)entityRenderMapField.get(haloJobScheduler);
    }
}
