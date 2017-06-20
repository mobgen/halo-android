package com.mobgen.halo.android.auth.pocket;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.auth.models.Pocket;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;

/**
 * Created by f.souto.gonzalez on 19/06/2017.
 */

/**
 * The repository to save or get pocket information.
 */
public class PocketRepository {

    /**
     * Remote data source.
     */
    private PocketRemoteDataSource mRemoteDatasource;

    /**
     * Constructor of the repository.
     *
     * @param pocketRemoteDataSource The pocket remote data source.
     */
    public PocketRepository(@NonNull PocketRemoteDataSource pocketRemoteDataSource) {
        AssertionUtils.notNull(pocketRemoteDataSource, "pocketRemoteDataSource");
        mRemoteDatasource = pocketRemoteDataSource;
    }

    /**
     * Perfom a get pocket operation.
     *
     * @param referenceFilter The reference filter to apply.
     * @return The result of the operation as a HaloResultV2<Pocket>
     * @throws HaloNetException
     * @throws HaloParsingException
     */
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

    /**
     * Perfom a save pocket operation.
     *
     * @param pocket The pocket to store.
     * @return The result of the operation as a HaloResultV2<Pocket>
     * @throws HaloNetException
     * @throws HaloParsingException
     */
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
