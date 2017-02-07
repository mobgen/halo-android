package com.mobgen.halo.android.content.generated;

import android.database.Cursor;
import android.database.SQLException;
import android.support.annotation.NonNull;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;

/**
 *  The repository to perfom generated queries.
 */
public class GeneratedContentQueriesRepository {

    /**
     * The local data source to perfom queries.
     */
    private GeneratedContentQueriesLocalDataSource mLocalDataSource;

    /**
     * The constructor of the repository.
     *
     * @param localDataSource The local data source.
     */
    public GeneratedContentQueriesRepository(@NonNull GeneratedContentQueriesLocalDataSource localDataSource) {
        mLocalDataSource = localDataSource;
    }

    /**
     * The autogen queries repository.
     *
     * @param query The query to perfom
     * @param bindArgs The args to evaluate.
     * @return HaloResultV2<Cursor>
     */
    @NonNull
    public HaloResultV2<Cursor> perfomQuery(@NonNull String query, @NonNull Object[] bindArgs) {
        HaloStatus.Builder status = HaloStatus.builder();
        Cursor response = null;
        try {
            response = mLocalDataSource.perfomQuery(query,bindArgs);
        } catch (SQLException haloException) {
            status.error(haloException);
        }
        return new HaloResultV2<>(status.build(), response);

    }

}
