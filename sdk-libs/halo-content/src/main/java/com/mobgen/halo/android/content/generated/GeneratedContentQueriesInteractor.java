package com.mobgen.halo.android.content.generated;


import android.database.Cursor;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.selectors.SelectorProviderAdapter;

/**
 * Perfom the operation to perfom queries from codegen
 *
 * @param <T> The type
 */
@Keep
public class GeneratedContentQueriesInteractor<T>  extends SelectorProviderAdapter<Paginated<T>, Cursor> {

    /**
     * The repository to perfom the query
     */
    private GeneratedContentQueriesRepository mGeneratedContentQueriesRepository;
    /**
     * The query generated from annotations
     */
    private String mQuery;
    /**
     * The arguments to use on the query
     */
    private Object[] mBindArgs;

    /**
     * Constructor of the interactor.
     *
     * @param generatedContentQueriesRepository The repository.
     * @param query The query to perfom.
     * @param bindArgs The args to put on the query.
     */
    public GeneratedContentQueriesInteractor(@NonNull GeneratedContentQueriesRepository generatedContentQueriesRepository, @NonNull String query, @NonNull Object[] bindArgs) {
        mGeneratedContentQueriesRepository = generatedContentQueriesRepository;
        mQuery = query;
        mBindArgs = bindArgs;
    }

    @NonNull
    @Override
    public HaloResultV2<Cursor> fromStorage() {
        HaloResultV2<Cursor> result = null;
        return mGeneratedContentQueriesRepository.perfomQuery(mQuery, mBindArgs);
    }
}
