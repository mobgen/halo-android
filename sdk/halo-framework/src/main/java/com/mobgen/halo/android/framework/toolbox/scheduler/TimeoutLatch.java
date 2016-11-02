package com.mobgen.halo.android.framework.toolbox.scheduler;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A timeout latch for testing purposes.
 */
public final class TimeoutLatch {
    /**
     * The counter.
     */
    private AtomicInteger mCounter;

    /**
     * Contructor with the count that has to be performed.
     *
     * @param count The count.
     */
    public TimeoutLatch(int count) {
        mCounter = new AtomicInteger(count);
    }

    /**
     * Time to wait.
     *
     * @param millisecs Time waiting.
     * @throws TimeoutException Error produced when the timeout is reached.
     */
    public void await(long millisecs) throws TimeoutException {
        Timer timer = new Timer();
        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] timeout = {false};
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int count = mCounter.get();
                latch.countDown();
                timeout[0] = count != 0;
            }
        }, millisecs);
        try {
            latch.await();
            if (timeout[0]) {
                throw new TimeoutException("Timeout");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Take a count down.
     */
    public void countDown() {
        mCounter.decrementAndGet();
    }
}
