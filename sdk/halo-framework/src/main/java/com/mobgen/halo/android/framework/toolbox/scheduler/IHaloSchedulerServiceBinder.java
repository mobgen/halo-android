package com.mobgen.halo.android.framework.toolbox.scheduler;

import android.os.IBinder;
import android.os.IInterface;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.threading.HaloThreadManager;

/**
 * The Halo scheduler interface to handle the binder when the service connects
 */
public interface IHaloSchedulerServiceBinder extends IInterface {
    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements IHaloSchedulerServiceBinder {
        private static final String DESCRIPTOR = "com.mobgen.halo.android.framework.toolbox.scheduler.IHaloSchedulerServiceBinder";

        /**
         * Construct the stub an attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an IHaloSchedulerServiceBinder interface,
         * generating a proxy binder if needed.
         */
        public static IHaloSchedulerServiceBinder asInterface(final IBinder iBinder) {
            if ((iBinder == null)) {
                return null;
            }
            IInterface iInterface = iBinder.queryLocalInterface(DESCRIPTOR);
            if (((iInterface != null) && (iInterface instanceof IHaloSchedulerServiceBinder))) {
                return ((IHaloSchedulerServiceBinder) iInterface);
            }
            return new ProxyServiceBinder(iBinder);
        }


        /**
         * The proxy binder when the service receive a differt binder than the binder we expect.
         */
        public static class ProxyServiceBinder implements IHaloSchedulerServiceBinder {

            private IBinder mRemoteService;

            ProxyServiceBinder(IBinder remote) {
                mRemoteService = remote;
            }

            @Override
            public void schedule(@NonNull Job job) {
                //nothing to do
            }

            @Override
            public void cancel(String tag) {
                //nothing to do
            }

            @Override
            public void removePersistJob(String tag) {
                //nothing to do
            }

            @Override
            public void stopAndReset() {
                //nothing to do
            }

            @Override
            public void threadManager(@NonNull HaloThreadManager threadManager) {
                //nothing to do
            }

            @Override
            public IBinder asBinder() {
                return mRemoteService;
            }
        }

        @Override
        public IBinder asBinder() {
            return this;
        }
    }

    /**
     * Schedule a new job into the service.
     *
     * @param job The job.
     */
    void schedule(@NonNull Job job);

    /**
     * Cancel the job by tag.
     *
     * @param tag The tag.
     */
    void cancel(String tag);

    /**
     * Removes a persisiting job.
     *
     * @param tag The tag.
     */
    void removePersistJob(String tag);

    /**
     * Stop the service and reset all the variables.
     */
    void stopAndReset();

    /**
     * Sets the thread manager.
     *
     * @param threadManager The thread manager.
     */
    void threadManager(@NonNull HaloThreadManager threadManager);

}
