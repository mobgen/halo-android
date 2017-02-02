package com.mobgen.halo.android.content.generated;

import android.database.Cursor;
import android.database.SQLException;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.edition.ContentManipulationRemoteDataSource;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.SyncQuery;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;

import java.util.List;


public class GeneratedContentQueriesRepository {


    private GeneratedContentQueriesLocalDataSource mLocalDataSource;

    public GeneratedContentQueriesRepository(@NonNull GeneratedContentQueriesLocalDataSource localDataSource) {
        mLocalDataSource = localDataSource;
    }


    @NonNull
    public HaloResultV2<List<HaloContentInstance>> perfomQuery(@NonNull String query, @NonNull Object[] bindArgs) {
        HaloStatus.Builder status = HaloStatus.builder();
        List<HaloContentInstance> response = null;
        try {
            response = mLocalDataSource.perfomQuery(query,bindArgs);
        } catch (SQLException haloException) {
            status.error(haloException);
        }
        return new HaloResultV2<>(status.build(), response);

    }

}
