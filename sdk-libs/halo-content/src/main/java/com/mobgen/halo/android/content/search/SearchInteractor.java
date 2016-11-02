package com.mobgen.halo.android.content.search;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.selectors.SelectorProviderAdapter;

/**
 * @hide Provider for the call to search data.
 */
public class SearchInteractor extends SelectorProviderAdapter<Paginated<HaloContentInstance>, Cursor> {
    /**
     * The repository that contains the information.
     */
    private ContentSearchRepository mRepository;
    /**
     * The query that will be performed.
     */
    private SearchQuery mQuery;

    /**
     * Constructor for the data provider.
     *
     * @param repository The repository.
     * @param query      The query.
     */
    public SearchInteractor(@NonNull ContentSearchRepository repository, @NonNull SearchQuery query) {
        mRepository = repository;
        mQuery = query;
    }

    @NonNull
    @Override
    public HaloResultV2<Paginated<HaloContentInstance>> fromNetwork() {
        return mRepository.searchNetwork(mQuery);
    }

    @NonNull
    @Override
    public HaloResultV2<Cursor> fromStorage() {
        return mRepository.searchStorage(mQuery);
    }

    @NonNull
    @Override
    public HaloResultV2<Cursor> fromNetworkStorage() {
        return mRepository.searchNetworkAndStorage(mQuery);
    }
}
