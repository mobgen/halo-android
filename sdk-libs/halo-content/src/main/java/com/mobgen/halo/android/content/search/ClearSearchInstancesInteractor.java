package com.mobgen.halo.android.content.search;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * Clears all the search instances cached removing all the local cache.
 */
public class ClearSearchInstancesInteractor implements HaloInteractorExecutor.Interactor<Void>{

    /**
     * The content repository.
     */
    private ContentSearchRepository mContentRepository;

    /**
     * Constructor for the interactor.
     * @param contentSearchRepository The content repository.
     */
    public ClearSearchInstancesInteractor(@NonNull ContentSearchRepository contentSearchRepository) {
        mContentRepository = contentSearchRepository;
    }

    @NonNull
    @Override
    public HaloResultV2<Void> executeInteractor() throws Exception {
        return mContentRepository.clearSearch();
    }
}
