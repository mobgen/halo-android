package com.mobgen.halo.android.auth.pocket;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.auth.models.Pocket;
import com.mobgen.halo.android.auth.models.PocketOperation;
import com.mobgen.halo.android.auth.models.ReferenceFilter;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * Created by f.souto.gonzalez on 19/06/2017.
 */

public class PocketInteractor implements HaloInteractorExecutor.Interactor<Pocket> {

    //TODO JAVADOC
    private PocketRepository mPocketRepository;

    private String mReferenceFilter;

    private Pocket mPocket;

    private PocketOperation mPocketOperation;


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
