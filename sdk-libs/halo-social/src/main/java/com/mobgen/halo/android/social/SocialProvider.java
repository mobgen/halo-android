package com.mobgen.halo.android.social;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.sync.callbacks.Callback;
import com.mobgen.halo.android.sdk.api.Halo;

/**
 * Common interface for the social providers.
 */
public interface SocialProvider {

    /**
     * Checks if the library is available.
     *
     * @return True if the library is available. False otherwise.
     */
    boolean isLibraryAvailable(@NonNull Context context);

    /**
     * Checks if the linked app is available.
     *
     * @param context The context to make the checks.
     * @return The linked app.
     */
    boolean linkedAppAvailable(@NonNull Context context);

    /**
     * Authenticates using the halo instance and providing the result in the callback.
     *
     * @param halo     The halo instance.
     * @param callback The callback.
     */
    void authenticate(@NonNull Halo halo, @NonNull Callback<HaloSocialProfile, Void> callback);

    /**
     * Releases all the resources reserved by this provider.
     */
    void release();
}
