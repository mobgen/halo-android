package com.mobgen.halo.android.content.generated;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.edition.ContentManipulationRemoteDataSource;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.SyncQuery;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;


public class GeneratedContentQueriesRepository {


    private GeneratedContentQueriesLocalDataSource mLocalDataSource;

    public GeneratedContentQueriesRepository(@NonNull GeneratedContentQueriesLocalDataSource localDataSource) {
        mLocalDataSource = localDataSource;
    }


    @NonNull
    public HaloResultV2<Cursor> perfomQuery(@NonNull String query,@NonNull Object[] bindArgs) throws HaloNetException, HaloParsingException {
        HaloStatus.Builder status = HaloStatus.builder();
        Cursor response = null;
        try {
            response = mLocalDataSource.perfomQuery(query,bindArgs);
        } catch (HaloNetException | HaloParsingException haloException) {
            status.error(haloException);
        }
        return new HaloResultV2<>(status.build(), response);

    }

}
