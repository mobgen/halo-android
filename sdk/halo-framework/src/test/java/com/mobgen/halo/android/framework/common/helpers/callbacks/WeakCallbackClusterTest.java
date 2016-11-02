package com.mobgen.halo.android.framework.common.helpers.callbacks;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class WeakCallbackClusterTest {

    private WeakCallbackCluster<ExampleCallbackObject> mWeakCallbacks;
    private WeakCallbackCluster<ExampleCallbackObject> mWeakCallbacksParameters;

    @Before
    public void initialize() {
        mWeakCallbacks = new WeakCallbackCluster<ExampleCallbackObject>() {
            @Override
            public void notifyCallback(ExampleCallbackObject callback, Object... args) {
                callback.doTask();
            }
        };
        mWeakCallbacksParameters = new WeakCallbackCluster<ExampleCallbackObject>() {
            @Override
            public void notifyCallback(ExampleCallbackObject callback, Object... args) {
                callback.doTask((boolean) args[0]);
            }
        };
    }

    @Test
    public void addWeakRemoveCallbackTest() throws InterruptedException {
        ExampleCallbackObject callback = new ExampleCallbackObject();
        ExampleCallbackObject callback2 = new ExampleCallbackObject();
        mWeakCallbacks.addCallback(callback);
        Assert.assertNotNull(mWeakCallbacks.getCallbacks());
        Assert.assertEquals(1, mWeakCallbacks.getCallbacks().size());
        mWeakCallbacks.addCallback(callback2);
        Assert.assertEquals(2, mWeakCallbacks.getCallbacks().size());

        mWeakCallbacks.removeCallback(callback);
        Assert.assertEquals(1, mWeakCallbacks.getCallbacks().size());
        mWeakCallbacks.removeCallback(callback2);
        Assert.assertEquals(0, mWeakCallbacks.getCallbacks().size());

        mWeakCallbacks.addCallback(callback);
        mWeakCallbacks.removeCallback(callback2);
        Assert.assertEquals(1, mWeakCallbacks.getCallbacks().size());
        mWeakCallbacks.clear();

        mWeakCallbacks.addCallback(callback2);
        callback2 = null;
        System.gc();
        Assert.assertEquals(1, mWeakCallbacks.getCallbacks().size());
        Assert.assertNull(mWeakCallbacks.getCallbacks().get(0).get());
        mWeakCallbacks.removeCallback(null);
        Assert.assertEquals(0, mWeakCallbacks.getCallbacks().size());
    }

    @Test
    public void notifyWeakCallbackTest() {
        ExampleCallbackObject callback = new ExampleCallbackObject();
        Assert.assertEquals(false, callback.mTaskDone);
        mWeakCallbacks.notifyCallbacks();
        Assert.assertEquals(false, callback.mTaskDone);
        mWeakCallbacks.addCallback(callback);
        mWeakCallbacks.notifyCallbacks();
        Assert.assertEquals(true, callback.mTaskDone);
        //Garbage collect the reference
        callback = null;
        System.gc();
        mWeakCallbacks.notifyCallbacks();
        Assert.assertNotNull(mWeakCallbacks.getCallbacks());
        Assert.assertTrue(mWeakCallbacks.getCallbacks().size() == 0);
    }

    @Test
    public void clearWeakCallbacksTest() {
        ExampleCallbackObject callback = new ExampleCallbackObject();
        Assert.assertEquals(null, mWeakCallbacks.getCallbacks());
        mWeakCallbacks.addCallback(callback);
        Assert.assertNotNull(mWeakCallbacks.getCallbacks());
        Assert.assertEquals(1, mWeakCallbacks.getCallbacks().size());
        mWeakCallbacks.clear();
        Assert.assertEquals(null, mWeakCallbacks.getCallbacks());
    }

    @Test
    public void emptyWeakCallbackAddTest() {
        mWeakCallbacks.clear();
        Assert.assertEquals(null, mWeakCallbacks.getCallbacks());
        mWeakCallbacks.addCallback(null);
        Assert.assertEquals(null, mWeakCallbacks.getCallbacks());
        mWeakCallbacks.removeCallback(null);
        Assert.assertEquals(null, mWeakCallbacks.getCallbacks());
    }

    @Test
    public void callbackWeakWithParametersTest() {
        ExampleCallbackObject callback = new ExampleCallbackObject();
        mWeakCallbacksParameters.addCallback(callback);
        mWeakCallbacksParameters.notifyCallbacks(false);
        Assert.assertEquals(false, callback.mTaskDone);
        mWeakCallbacksParameters.notifyCallbacks(true);
        Assert.assertEquals(true, callback.mTaskDone);
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
