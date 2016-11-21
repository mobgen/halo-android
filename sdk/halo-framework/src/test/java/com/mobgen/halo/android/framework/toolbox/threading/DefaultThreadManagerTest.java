package com.mobgen.halo.android.framework.toolbox.threading;

import android.annotation.SuppressLint;

import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.common.exceptions.HaloConfigurationException;
import com.mobgen.halo.android.framework.mock.FrameworkMock;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Future;

import static com.mobgen.halo.android.framework.mock.instrumentation.HaloThreadInstrument.givenARunnable;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class DefaultThreadManagerTest extends HaloRobolectricTest {

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
    public void thatCanCreateADefaultThreadManagerWithPoolQueuePolicy() {

        DefaultThreadManager defaultThreadManager = new DefaultThreadManager();
        Future future = defaultThreadManager.enqueue(Threading.POOL_QUEUE_POLICY, givenARunnable(mCallbackFlag));
        assertThat(mCallbackFlag.isFlagged()).isFalse();
        assertThat(future).isNotNull();
    }

    @Test
    public void thatCanCreateADefaultThreadManagerWithSingleQueuePolicy() {

        DefaultThreadManager defaultThreadManager = new DefaultThreadManager();
        Future future = defaultThreadManager.enqueue(Threading.SINGLE_QUEUE_POLICY, givenARunnable(mCallbackFlag));
        assertThat(mCallbackFlag.isFlagged()).isFalse();
        assertThat(future).isNotNull();
    }

    @Test
    public void thatCanCreateADefaultThreadManagerWithSameThreadQueuePolicy() {

        DefaultThreadManager defaultThreadManager = new DefaultThreadManager();
        Future future =  defaultThreadManager.enqueue(Threading.SAME_THREAD_POLICY, givenARunnable(mCallbackFlag));
        assertThat(mCallbackFlag.isFlagged()).isFalse();
        assertThat(future).isNotNull();
    }
    @Test
    public void thatCanCreateADefaultThreadManagerAndThrowAHaloConfigurationException() {

        DefaultThreadManager defaultThreadManager = new DefaultThreadManager();
        try {
            Future future = defaultThreadManager.enqueue(5, givenARunnable(mCallbackFlag));
        }catch(HaloConfigurationException haloConfigurationException){
            assertThat(haloConfigurationException.getMessage()).contains("Unsupported");
        }

    }
}
