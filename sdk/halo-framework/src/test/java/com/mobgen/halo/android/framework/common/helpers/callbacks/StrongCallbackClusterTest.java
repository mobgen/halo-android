package com.mobgen.halo.android.framework.common.helpers.callbacks;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class StrongCallbackClusterTest {

    private StrongCallbackCluster<ExampleCallbackObject> mStrongCallbacks;
    private StrongCallbackCluster<ExampleCallbackObject> mStrongCallbacksParameters;

    @Before
    public void initialize() {
        mStrongCallbacks = new StrongCallbackCluster<ExampleCallbackObject>() {
            @Override
            public void notifyCallback(ExampleCallbackObject callback, Object... args) {
                callback.doTask();
            }
        };
        mStrongCallbacksParameters = new StrongCallbackCluster<ExampleCallbackObject>() {
            @Override
            public void notifyCallback(ExampleCallbackObject callback, Object... args) {
                callback.doTask((boolean) args[0]);
            }
        };
    }

    @Test
    public void addStrongRemoveCallbackTest() {
        ExampleCallbackObject callback = new ExampleCallbackObject();
        ExampleCallbackObject callback2 = new ExampleCallbackObject();
        mStrongCallbacks.addCallback(callback);
        Assert.assertNotNull(mStrongCallbacks.getCallbacks());
        Assert.assertEquals(1, mStrongCallbacks.getCallbacks().size());
        mStrongCallbacks.addCallback(callback2);
        Assert.assertEquals(2, mStrongCallbacks.getCallbacks().size());

        mStrongCallbacks.removeCallback(callback);
        Assert.assertEquals(1, mStrongCallbacks.getCallbacks().size());
        mStrongCallbacks.removeCallback(callback2);
        Assert.assertEquals(0, mStrongCallbacks.getCallbacks().size());

        mStrongCallbacks.addCallback(callback);
        mStrongCallbacks.removeCallback(callback2);
        Assert.assertEquals(1, mStrongCallbacks.getCallbacks().size());
        mStrongCallbacks.clear();

        mStrongCallbacks.addCallback(callback2);
        callback2 = null;
        System.gc();
        mStrongCallbacks.removeCallback(null);
        //Memory leaked
        Assert.assertEquals(1, mStrongCallbacks.getCallbacks().size());
    }

    @Test
    public void notifyStrongCallbackTest() {
        ExampleCallbackObject callback = new ExampleCallbackObject();
        Assert.assertEquals(false, callback.mTaskDone);
        mStrongCallbacks.notifyCallbacks();
        Assert.assertEquals(false, callback.mTaskDone);
        mStrongCallbacks.addCallback(callback);
        mStrongCallbacks.notifyCallbacks();
        Assert.assertEquals(true, callback.mTaskDone);
        //Garbage collect the reference
        callback = null;
        System.gc();
        mStrongCallbacks.notifyCallbacks();
        Assert.assertNotNull(mStrongCallbacks.getCallbacks());
        //Memory leaked
        Assert.assertEquals(1, mStrongCallbacks.getCallbacks().size());
    }

    @Test
    public void clearStrongCallbacksTest() {
        ExampleCallbackObject callback = new ExampleCallbackObject();
        Assert.assertEquals(null, mStrongCallbacks.getCallbacks());
        mStrongCallbacks.addCallback(callback);
        Assert.assertNotNull(mStrongCallbacks.getCallbacks());
        Assert.assertEquals(1, mStrongCallbacks.getCallbacks().size());
        mStrongCallbacks.clear();
        Assert.assertEquals(null, mStrongCallbacks.getCallbacks());
    }

    @Test
    public void emptyStrongCallbackAddTest() {
        mStrongCallbacks.clear();
        Assert.assertEquals(null, mStrongCallbacks.getCallbacks());
        mStrongCallbacks.addCallback(null);
        Assert.assertEquals(null, mStrongCallbacks.getCallbacks());
        mStrongCallbacks.removeCallback(null);
        Assert.assertEquals(null, mStrongCallbacks.getCallbacks());
    }

    @Test
    public void callbackStrongWithParametersTest() {
        ExampleCallbackObject callback = new ExampleCallbackObject();
        mStrongCallbacksParameters.addCallback(callback);
        mStrongCallbacksParameters.notifyCallbacks(false);
        Assert.assertEquals(false, callback.mTaskDone);
        mStrongCallbacksParameters.notifyCallbacks(true);
        Assert.assertEquals(true, callback.mTaskDone);
    }

    @Test
    public void thatGetCount(){
        ExampleCallbackObject callback = new ExampleCallbackObject();
        mStrongCallbacks.addCallback(callback);
        mStrongCallbacks.notifyCallbacks(false);
        assertThat(mStrongCallbacks.getCount()).isEqualTo(1);

    }

    private class ExampleCallbackObject {
        private boolean mTaskDone;

        private void doTask() {
            mTaskDone = true;
        }

        private void doTask(boolean isDone) {
            mTaskDone = isDone;
        }
    }
}
