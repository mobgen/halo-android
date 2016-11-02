package com.mobgen.halo.android.sdk.mock.instrumentation;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.threading.HaloThreadManager;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class TestThreadManager extends HaloThreadManager {

    @Override
    public Future enqueue(@Threading.Policy int thread, @NonNull Runnable runnable) {
        Future<?> future = new FutureTask<>(runnable, null);
        runnable.run();
        return future;
    }
}
