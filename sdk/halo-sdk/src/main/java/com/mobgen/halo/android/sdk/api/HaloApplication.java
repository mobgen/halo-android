package com.mobgen.halo.android.sdk.api;

import android.annotation.SuppressLint;
import android.app.Application;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;

/**
 * Halo application to install the Halo framework.
 */
@Keep
@SuppressLint("Registered")
public class HaloApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //Use the Halo installer to install the sdk
        installHalo();
    }

    /**
     * Installs the HALO SDK.
     */
    @Api(1.0)
    public final void installHalo() {
        if (Halo.isInitialized()) {
            Halo.instance().uninstall();
        }
        onHaloCreated(doInstall(beforeInstallHalo(onCreateInstaller())));
    }

    /**
     * Creates the installer for Halo.
     *
     * @return The installer for Halo.
     */
    @Api(1.0)
    @NonNull
    public Halo.Installer onCreateInstaller() {
        return Halo.installer(this);
    }

    /**
     * Callback received when the halo SDK is beind installed.
     *
     * @param installer The installer.
     * @return The installer modified if it is needed so.
     */
    @Api(1.1)
    @NonNull
    public Halo.Installer beforeInstallHalo(@NonNull Halo.Installer installer) {
        return installer;
    }

    /**
     * Callback that can be used to override the behaviour of halo and
     * the correct place to add your own modules before the installation.
     *
     * @param halo The halo object created.
     * @return The halo instance.
     */
    @Api(1.2)
    @NonNull
    public Halo onHaloCreated(@NonNull Halo halo) {
        return halo;
    }

    /**
     * Installs the halo instance.
     *
     * @param installer The installer.
     * @return The halo instance.
     */
    @Api(1.2)
    public Halo doInstall(@NonNull Halo.Installer installer) {
        return installer.install();
    }

    /**
     * Provides the HALO instance stored in the application.
     *
     * @return The halo instance.
     */
    @Api(1.3)
    public static Halo halo() {
        return Halo.instance();
    }
}
