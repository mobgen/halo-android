package com.mobgen.halo.android.framework.common.helpers.callbacks;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;

import com.mobgen.halo.android.framework.common.annotations.Api;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Enhanced callbacks allows to have weak referenced managed callbacks. It removes the references
 * when they are not available anymore without any interaction, so we avoid any kind of memory
 * leaks on this way.
 */
public abstract class WeakCallbackCluster<C> implements CallbackCluster<C> {

    /**
     * The callbacks stored.
     */
    private List<WeakReference<C>> mCallbacks;

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
            mCallbacks.add(new WeakReference<>(callback));
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
                C innerCallback = mCallbacks.get(i).get();
                if (innerCallback != null) {
                    if (innerCallback == callback) {
                        mCallbacks.remove(i);
                        return;
                    }
                } else {
                    mCallbacks.remove(i);
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
            List<WeakReference<C>> callbackCopy = new ArrayList<>(mCallbacks);
            for (WeakReference<C> callback : callbackCopy) {
                C innerCallback = callback.get();
                if (innerCallback != null) {
                    notifyCallback(innerCallback, args);
                } else {
                    mCallbacks.remove(callback);
                }
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
    public List<WeakReference<C>> getCallbacks() {
        return mCallbacks;
    }
}
