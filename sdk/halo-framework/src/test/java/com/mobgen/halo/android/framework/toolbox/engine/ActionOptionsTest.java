//package com.mobgen.halo.android.framework.toolbox.engine;
//
//import com.mobgen.halo.android.framework.api.HaloFramework;
//import com.mobgen.halo.android.framework.mock.FrameworkMock;
//import com.mobgen.halo.android.framework.toolbox.data.Data;
//import com.mobgen.halo.android.framework.toolbox.threading.Threading;
//import com.mobgen.halo.android.testing.HaloRobolectricTest;
//
//import org.junit.Assert;
//import org.junit.Test;
//
//import static org.mockito.Mockito.mock;
//
//public class ActionOptionsTest extends HaloRobolectricTest {
//
//    @Test
//    public void haloActionOptionsTest() {
//        HaloFramework framework = HaloFramework.create(FrameworkMock.createSameThreadMockConfig(mock(Parser.Factory.class)));
//        ActionOptions options = new ActionOptions();
//        //Check default options
//        Assert.assertEquals(Data.NETWORK_ONLY, options.getExecutionMode());
//        Assert.assertEquals(Threading.POOL_QUEUE_POLICY, options.getThreadingMode());
//        options.execution(Data.NETWORK_AND_STORAGE);
//        options.thread(Threading.SAME_THREAD_POLICY);
//        Action.Executor executor = new Action.Executor(framework)
//                .actionRequest(ActionRequest.createNetworkStorage("syncId", "storageId"));
//        options.set(executor);
//        Action action = executor.build();
//        Assert.assertEquals(ActionOptions.NETWORK_AND_STORAGE, action.executionMode());
//        Assert.assertEquals(ActionOptions.SAME_THREAD_POLICY, action.threadPolicy());
//    }
//}
