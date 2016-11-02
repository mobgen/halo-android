package com.mobgen.halo.android.framework.toolbox.scheduler;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.helpers.logger.Halog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The device environment that contains the global status of it. This can be used
 * to make sure the triggers are met or not.
 */
final class StatusDevice {

    /**
     * The device is being charged.
     */
    static final AtomicBoolean CHARGING_CONSTRAINT_SATISFIED = new AtomicBoolean();
    /**
     * The device is idle.
     */
    static final AtomicBoolean IDLE_CONSTRAINT_SATISFIED = new AtomicBoolean();
    /**
     * The network is unmetered.
     */
    static final AtomicBoolean UNMETERED_CONSTRAINT_SATISFIED = new AtomicBoolean();
    /**
     * The device has connectivity.
     */
    static final AtomicBoolean CONNECTIVITY_CONSTRAINT_SATISFIED = new AtomicBoolean();
    /**
     * Synchronized lock.
     */
    private static final Object sLock = new Object();
    /**
     * The unique instance for the device status.
     */
    private static StatusDevice sInstance;

    /**
     * The default controllers for the properties of the device status.
     */
    public Class<?>[] CONTROLLERS = new Class[]{
            StatusNetworkController.class,
            StatusChargingController.class,
            StatusIdleController.class
    };
    /**
     * The list of controllers available.
     */
    private List<StatusController> mControllers;

    /**
     * Constructor for the device status. It only allows one instance.
     *
     * @param context The context.
     */
    private StatusDevice(Context context) {
        mControllers = new ArrayList<>();
        for (Class<?> controller : CONTROLLERS) {
            try {
                mControllers.add((StatusController) controller.newInstance());
            } catch (Exception e) {
                Halog.e(getClass(), "The controller for the background service could not be triggered.", e);
            }
        }
        //The status controller
        for (StatusController controller : mControllers) {
            controller.onCreate(context);
        }
    }

    /**
     * Checks which network type is satisfied.
     *
     * @param type The type.
     * @return The network type satisfied.
     */
    static boolean networkTypeSatisfied(int type) {
        switch (type) {
            case Job.NETWORK_TYPE_NONE:
                return !CONNECTIVITY_CONSTRAINT_SATISFIED.get();
            case Job.NETWORK_TYPE_ANY:
                return CONNECTIVITY_CONSTRAINT_SATISFIED.get();
            case Job.NETWORK_TYPE_UNMETERED:
                return CONNECTIVITY_CONSTRAINT_SATISFIED.get() && UNMETERED_CONSTRAINT_SATISFIED.get();
            default:
                return false;
        }
    }

    /**
     * Creates a single instance of the device status to avoid multiple receivers
     * from being registered.
     *
     * @param context The context.
     * @return The current status.
     */
    @NonNull
    static StatusDevice get(@NonNull Context context) {
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new StatusDevice(context);
            }
            return sInstance;
        }
    }

    /**
     * Destroys the device status and all its controllers.
     */
    void onDestroy() {
        for (StatusController controller : mControllers) {
            controller.onDestroy();
        }
    }

}
