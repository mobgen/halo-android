package com.mobgen.halo.android.sdk.mock;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloApplication;

import static com.mobgen.halo.android.sdk.mock.HaloMock.givenAHaloInstaller;

public class HaloApplicationMock extends HaloApplication {

    private boolean onInstallCalled;
    private boolean onHaloCreatedCalled;

    @NonNull
    @Override
    public Halo.Installer beforeInstallHalo(@NonNull Halo.Installer installer) {
        onInstallCalled = true;
        return super.beforeInstallHalo(givenAHaloInstaller());
    }

    @Override
    public Halo doInstall(@NonNull Halo.Installer installer) {
        return HaloMock.create();
    }

    @NonNull
    @Override
    public Halo onHaloCreated(@NonNull Halo halo) {
        onHaloCreatedCalled = true;
        return super.onHaloCreated(halo);
    }

    public boolean isInstallerCreated() {
        return onInstallCalled;
    }

    public boolean isHaloCreated() {
        return onHaloCreatedCalled;
    }
}
