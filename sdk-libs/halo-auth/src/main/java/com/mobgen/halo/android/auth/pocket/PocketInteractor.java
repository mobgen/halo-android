package com.mobgen.halo.android.auth.pocket;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.auth.models.Pocket;
import com.mobgen.halo.android.auth.models.PocketOperation;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * Created by f.souto.gonzalez on 19/06/2017.
 */

/**
 * The Pocket interactor to perfom operations.
 */
public class PocketInteractor implements HaloInteractorExecutor.Interactor<Pocket> {

    /**
     * The pocket repository.
     */
    private PocketRepository mPocketRepository;

    /**
     * The reference filter to apply.
     */
    private String mReferenceFilter;

    /**
     * The pocket.
     */
    private Pocket mPocket;

    /**
     * The pocket operation to perfom.
     */
    private PocketOperation mPocketOperation;


    /**
     * Constructor for the pocket interactor.
     *
     * @param pocketRepository The pocket repository.
     * @param referenceFilter  The reference filter to apply.
     * @param pocket           The pocket to operate with.
     * @param pocketOperation  The pocket operation to perfom.
     */
    public PocketInteractor(@NonNull PocketRepository pocketRepository, @Nullable String referenceFilter, @Nullable Pocket pocket, @NonNull PocketOperation pocketOperation) {
        AssertionUtils.notNull(pocketRepository, "pocketRepository");
        mPocketRepository = pocketRepository;
        mPocketOperation = pocketOperation;
        mReferenceFilter = referenceFilter;
        mPocket = pocket;
    }


    @NonNull
    @Override
    public HaloResultV2<Pocket> executeInteractor() throws Exception {
        HaloResultV2<Pocket> result = null;
        switch (mPocketOperation) {
            case GET:
                return mPocketRepository.getOperation(mReferenceFilter);
            case SAVE:
                return mPocketRepository.saveOperation(mPocket);
        }
        return result;
    }
}
