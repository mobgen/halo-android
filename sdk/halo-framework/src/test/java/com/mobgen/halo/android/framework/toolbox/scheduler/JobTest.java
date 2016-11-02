package com.mobgen.halo.android.framework.toolbox.scheduler;

import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.mock.FrameworkMock;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static com.mobgen.halo.android.framework.mock.instrumentation.HaloThreadInstrument.givenAJob;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloThreadInstrument.givenAJobWithTrigger;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloThreadInstrument.givenAJobWithoutDeadline;
import static org.assertj.core.api.Java6Assertions.assertThat;


public class JobTest extends HaloRobolectricTest {

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
    public void thatCanPersist() throws IOException {
        Job job = givenAJob(mCallbackFlag, "job");
        assertThat(job.canBePersisted()).isFalse();
    }

    @Test
    public void thatGetTriggers() throws IOException {
        Job job = givenAJobWithTrigger(mCallbackFlag);
        assertThat(job.triggers().size()).isEqualTo(1);
    }

    @Test
    public void thatResetCondigtions(){
        Job job = givenAJob(mCallbackFlag, "job");
        Job.JobInfo jobInfo = job.info();
        job.resetConds();
        assertThat(job.info().equals(jobInfo)).isTrue();
    }

    @Test
    public void thatCanPersistJobInfoToFileAndDelete() throws IOException {
        Job job = givenAJob(mCallbackFlag,"job");
        String pathFile = getPathToFileResource();
        File fileJobInfo = new File(pathFile);
        File fileToVerify = new File(pathFile, job.info().mIdentity + ".job");
        job.info().writeToFile(fileJobInfo);
        job.info().tryDelete(fileJobInfo);
        assertThat(fileJobInfo.isDirectory()).isTrue();
        assertThat(fileToVerify.exists()).isFalse();
    }

    @Test
    public void thatCanPersistJobInfoWithTriggerToFileAndDelete() throws IOException {
        Job job = givenAJobWithTrigger(mCallbackFlag);
        String pathFile = getPathToFileResource();
        File fileJobInfo = new File(pathFile);
        File fileToVerify = new File(pathFile, job.info().mIdentity + ".job");
        job.info().writeToFile(fileJobInfo);
        job.info().tryDelete(fileJobInfo);
        assertThat(fileJobInfo.isDirectory()).isTrue();
        assertThat(fileToVerify.exists()).isFalse();
    }

    @Test
    public void thatCanReadJobInfoFromFile() throws IOException {
        Job job = givenAJobWithoutDeadline(mCallbackFlag);
        String pathFile = getPathToFileResource();
        File fileJobInfo = new File(pathFile);
        File fileToRead = new File(pathFile, job.info().mIdentity + ".job");
        job.info().writeToFile(fileJobInfo);
        Job.JobInfo jobInfo =  Job.JobInfo.readFromFile(fileToRead);
        assertThat(fileJobInfo.exists()).isTrue();
        assertThat(jobInfo.mTag).isEqualTo(job.info().mTag);
        assertThat(jobInfo.equals(job.info())).isTrue();
    }

    private static String getPathToFileResource() {
        URL pathURL = RuntimeEnvironment.application.getClass().getClassLoader().getResource("jobInfo");
        String pathFile =  pathURL.toString();
        pathFile = pathFile.replace("file:","");
        return pathFile;
    }

}

