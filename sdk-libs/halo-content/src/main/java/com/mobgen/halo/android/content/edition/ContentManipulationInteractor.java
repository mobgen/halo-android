package com.mobgen.halo.android.content.edition;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.HaloEditContentOptions;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

/**
 * General content instance content manipulation interactor.
 */
public class ContentManipulationInteractor implements HaloInteractorExecutor.Interactor<HaloContentInstance>{

    /**
     * Operation repository.
     */
    private ContentManipulationRepository mContentManipulationRepository;
    /**
     * The Halo Request method to operate.
     */
    private HaloRequestMethod mOperation;
    /**
     * The Halo general content instace to operate with.
     */
    private HaloEditContentOptions mHaloEditContentOptions;


    /**
     * Constructor for the interactor.
     *
     * @param contentManipulationRepository The repository.
     * @param haloEditContentOptions The general content instance.
     * @param operation The Halo request method.
     */
    public ContentManipulationInteractor(@NonNull ContentManipulationRepository contentManipulationRepository, @Nullable HaloEditContentOptions haloEditContentOptions, @NonNull HaloRequestMethod operation) {
        mContentManipulationRepository = contentManipulationRepository;
        mOperation = operation;
        mHaloEditContentOptions = haloEditContentOptions;
    }


    @NonNull
    @Override
    public HaloResultV2<HaloContentInstance> executeInteractor() throws Exception {
        HaloResultV2<HaloContentInstance> result = null;
        switch (mOperation){
            case POST:
                return mContentManipulationRepository.addContent(mOperation, mHaloEditContentOptions);
            case PUT:
                return mContentManipulationRepository.updateContent(mOperation, mHaloEditContentOptions);
            case DELETE:
                return mContentManipulationRepository.deleteContent(mOperation, mHaloEditContentOptions);
        }
        return result;
    }
}
