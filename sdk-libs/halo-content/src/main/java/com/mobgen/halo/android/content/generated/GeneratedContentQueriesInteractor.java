package com.mobgen.halo.android.content.generated;


import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.content.edition.ContentManipulationRepository;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;

import java.util.List;


public class GeneratedContentQueriesInteractor implements HaloInteractorExecutor.Interactor<List<HaloContentInstance>>{


    private GeneratedContentQueriesRepository mGeneratedContentQueriesRepository;

    private String mQuery;

    private Object[] mBindArgs;



    public GeneratedContentQueriesInteractor(@NonNull GeneratedContentQueriesRepository generatedContentQueriesRepository, @NonNull String query, @NonNull Object[] bindArgs) {
        mGeneratedContentQueriesRepository = generatedContentQueriesRepository;
        mQuery = query;
        mBindArgs = bindArgs;
    }


    @NonNull
    @Override
    public HaloResultV2<List<HaloContentInstance>> executeInteractor() throws Exception {
        HaloResultV2<Cursor> result = null;
        return mGeneratedContentQueriesRepository.perfomQuery(mQuery, mBindArgs);
    }
}
