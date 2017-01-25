package com.mobgen.halo.android.content.search;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.PaginationInfo;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.spec.HaloContentContract;
import com.mobgen.halo.android.content.utils.HaloContentHelper;
import com.mobgen.halo.android.framework.api.HaloStorageApi;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.storage.database.dsl.queries.Select;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageParseException;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.core.selectors.ISelectorConverter;

import java.util.List;

/**
 * @hide Parses the cursor to a content instance.
 */
public class Cursor2ContentInstanceSearchConverter implements ISelectorConverter<Paginated<HaloContentInstance>, Cursor> {

    /**
     * The query used.
     */
    private SearchQuery mQuery;
    /**
     * The parser factory.
     */
    private Parser.Factory mParser;
    /**
     * The storage api.
     */
    private HaloStorageApi mStorageApi;

    /**
     * Creates a cursor to instance converter.
     *
     * @param query      The query to convert.
     * @param parser     The parser.
     * @param storageApi The storage api.
     */
    public Cursor2ContentInstanceSearchConverter(@NonNull SearchQuery query, @NonNull Parser.Factory parser, @NonNull HaloStorageApi storageApi) {
        mQuery = query;
        mParser = parser;
        mStorageApi = storageApi;
    }

    @NonNull
    @Override
    public HaloResultV2<Paginated<HaloContentInstance>> convert(@NonNull HaloResultV2<Cursor> cursor) throws HaloStorageParseException, HaloStorageGeneralException {
        return new HaloResultV2<>(cursor.status(), parse(cursor.data()));
    }

    /**
     * Parses the cursor into a paginated content instance list.
     *
     * @param cursor The cursor.
     * @return The paginated instances.
     * @throws HaloStorageParseException   Error while parsing the data.
     * @throws HaloStorageGeneralException General storage error, for example produced when the database
     *                                     corrupts.
     */
    @NonNull
    private Paginated<HaloContentInstance> parse(@NonNull Cursor cursor) throws HaloStorageParseException, HaloStorageGeneralException {
        Cursor optionsCursor = null;
        try {
            String optionsId = mQuery.createHash(mParser);

            //Take the query using the info from the search table
            optionsCursor = Select.all()
                    .from(HaloContentContract.ContentSearchQuery.class)
                    .where(HaloContentContract.ContentSearchQuery.QUERY_ID)
                    .eq(optionsId)
                    .on(mStorageApi.db(), "Take the search info to get the pagination params in order to parse the data");

            //Bring gc instances
            List<HaloContentInstance> instances = HaloContentHelper.createList(cursor, true);

            //In case we find a search item bring the pagination info
            PaginationInfo info;
            if (optionsCursor.moveToFirst()) {
                int limit = optionsCursor.getInt(optionsCursor.getColumnIndexOrThrow(HaloContentContract.ContentSearchQuery.PAGINATION_LIMIT));
                int page = optionsCursor.getInt(optionsCursor.getColumnIndexOrThrow(HaloContentContract.ContentSearchQuery.PAGINATION_PAGE));
                int count = optionsCursor.getInt(optionsCursor.getColumnIndexOrThrow(HaloContentContract.ContentSearchQuery.PAGINATION_COUNT));
                optionsCursor.close();
                info = new PaginationInfo(page, limit, count);
            } else {
                //TODO: remove this pagination info change also when it comes ready
                // From the server
                info = new PaginationInfo(instances.size());
            }
            //Just return
            return new Paginated<>(instances, info);
        } catch (HaloParsingException e) {
            throw new HaloStorageParseException("Error while creating the hash", e);
        } finally {
            if(optionsCursor!=null && !optionsCursor.isClosed()){
                optionsCursor.close();
            }
        }
    }
}