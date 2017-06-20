package com.mobgen.halo.android.auth.pocket;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.auth.models.Pocket;
import com.mobgen.halo.android.auth.models.ReferenceFilter;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;

/**
 * Created by f.souto.gonzalez on 19/06/2017.
 */

public class PocketRepository {
    //TODO JAVADOC
    /**
     * Remote data source.
     */
    private PocketRemoteDataSource mRemoteDatasource;

    public PocketRepository(@NonNull PocketRemoteDataSource pocketRemoteDataSource) {
        AssertionUtils.notNull(pocketRemoteDataSource, "pocketRemoteDataSource");
        mRemoteDatasource = pocketRemoteDataSource;
    }


    @NonNull
    public HaloResultV2<Pocket> getOperation(@NonNull String referenceFilter) throws HaloNetException, HaloParsingException {
        HaloStatus.Builder status = HaloStatus.builder();
        Pocket response = null;
        try {
            response = mRemoteDatasource.getPocket(referenceFilter);
        } catch (HaloNetException | HaloParsingException haloException) {
            status.error(haloException);
        }
        return new HaloResultV2<>(status.build(), response);
    }

    @NonNull
    public HaloResultV2<Pocket> saveOperation(@NonNull Pocket pocket) throws HaloNetException, HaloParsingException {
        HaloStatus.Builder status = HaloStatus.builder();
        Pocket response = null;
        try {
            response = mRemoteDatasource.savePocket(pocket);
        } catch (HaloNetException | HaloParsingException haloException) {
            status.error(haloException);
        }
        return new HaloResultV2<>(status.build(), response);
    }

}
