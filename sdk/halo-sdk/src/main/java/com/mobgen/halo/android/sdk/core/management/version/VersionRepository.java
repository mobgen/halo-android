package com.mobgen.halo.android.sdk.core.management.version;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.core.management.models.HaloServerVersion;

/**
 * Provides the current version of the server.
 */
public class VersionRepository {

    /**
     * The remote data source.
     */
    private VersionRemoteDatasource mRemoteDatasource;

    /**
     * Constructor for the version repo.
     * @param remoteDatasource The version repository.
     */
    public VersionRepository(VersionRemoteDatasource remoteDatasource) {
        mRemoteDatasource = remoteDatasource;
    }

    /**
     * Provides the current version.
     * @return The current version.
     */
    @NonNull
    public HaloResultV2<HaloServerVersion> getCurrentVersion(){
        HaloStatus.Builder status = HaloStatus.builder();
        HaloServerVersion version = null;
        try {
            version = mRemoteDatasource.getServerVersion();
        } catch (HaloNetException e) {
            status.error(e);
        }
        return new HaloResultV2<>(status.build(), version);
    }
}
