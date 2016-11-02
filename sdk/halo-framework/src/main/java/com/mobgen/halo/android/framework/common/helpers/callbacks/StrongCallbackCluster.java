package com.mobgen.halo.android.framework.common.helpers.callbacks;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.mobgen.halo.android.framework.common.annotations.Api;

import java.util.ArrayList;
import java.util.List;

/**
 * Strong enhanced callbacks allows to have a list of callbacks and notify all
 * of them at the same time. The references are long lived so it is needed to
 * call implicitly to remove to avoid memory leaks. Keep in mind it specially if
 * it is called from an activity, fragment or view.
 */
public abstract class StrongCallbackCluster<C> implements CallbackCluster<C> {

    /**
     * The callbacks stored.
     */
    private List<C> mCallbacks;

    /**
     * Adds a callback to the enhanced callback pool.
     *
     * @param callback The callback to add.
     */
    @Api(1.0)
    @Override
    public void addCallback(@Nullable C callback) {
        if (callback != null) {
            if (mCallbacks == null) {
                mCallbacks = new ArrayList<>();
            }
            mCallbacks.add(callback);
        }
    }

    /**
     * Removes a callback from the list of callbacks.
     *
     * @param callback The callback.
     */
    @Api(1.0)
    @Override
    public void removeCallback(@Nullable C callback) {
        if (mCallbacks != null) {
            for (int i = 0; i < mCallbacks.size(); i++) {
                if (mCallbacks.get(i) == callback) {
                    mCallbacks.remove(i);
                    return;
                }
            }
        }
    }

    /**
     * Notifies to the listeners.
     *
     * @param args The arguments of the listeners.
     */
    @Api(1.0)
    @Override
    public void notifyCallbacks(@Nullable Object... args) {
        if (mCallbacks != null) {
            //Create a copy of the callbacks to ensure all of them are called even if they are removed
            //inside the callback
            List<C> callbackCopy = new ArrayList<>(mCallbacks);
            for (C callback : callbackCopy) {
                notifyCallback(callback, args);
            }
        }
    }

    /**
     * Clears all the available callbacks.
     */
    @Api(1.0)
    @Override
    public void clear() {
        if (mCallbacks != null) {
            mCallbacks.clear();
            mCallbacks = null;
        }
    }

    /**
     * Provides the callbacks inside the enhanced callbacks.
     *
     * @return The callbacks.
     */
    @VisibleForTesting
    @Nullable
    public List<C> getCallbacks() {
        return mCallbacks;
    }

    /**
     * Provides the amount of callbacks in the cluster.
     *
     * @return The amount of callbacks.
     */
    @VisibleForTesting
    public int getCount() {
        int count = 0;
        if (mCallbacks != null) {
            count = mCallbacks.size();
        }
        return count;
    }
}
