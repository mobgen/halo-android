package com.mobgen.halo.android.sdk.core.management.version;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.management.models.HaloServerVersion;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * The version data provider.
 */
public class GetVersionInteractor implements HaloInteractorExecutor.Interactor<HaloServerVersion> {

    /**
     * The version repository.
     */
    private VersionRepository mRemoteDatasource;

    /**
     * The version data source.
     * @param remoteDatasource The data source.
     */
    public GetVersionInteractor(@NonNull VersionRepository remoteDatasource) {
        mRemoteDatasource = remoteDatasource;
    }

    @NonNull
    @Override
    public HaloResultV2<HaloServerVersion> executeInteractor() throws Exception {
        return mRemoteDatasource.getCurrentVersion();
    }
}
