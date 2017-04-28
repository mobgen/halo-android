package com.mobgen.halo.android.content.search;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.mock.dummy.DummyItem;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.threading.ICancellable;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mobgen.halo.android.content.mock.fixtures.ServerFixtures.SEARCH_NOT_PAGINATED_RESPONSE;
import static com.mobgen.halo.android.content.mock.fixtures.ServerFixtures.SEARCH_PAGINATED_RESPONSE;
import static com.mobgen.halo.android.content.mock.fixtures.ServerFixtures.enqueueServerError;
import static com.mobgen.halo.android.content.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloContentApiMock.givenAContentApi;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenADefaultHalo;
import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenAComplexQuery;
import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenANotPaginatedQuery;
import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenASearchLikePatternQuery;
import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenCallbackContentParsedEmptyDataLocal;
import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenCallbackContentParsedSuccessData;
import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenCallbackContentSuccessData;
import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenCallbackThatChecksDataIsInconsistent;
import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenCallbackWithErrorType;
import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenTheSimplestQuery;
import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenTimedCacheQuery;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloContentSearchTest extends HaloRobolectricTest {

    private MockServer mMockServer;
    private Halo mHalo;
    private HaloContentApi mHaloContentApi;
    private CallbackFlag mCallbackFlag;

    @Override
    public void onStart() throws IOException {
        mMockServer = MockServer.create();
        mHalo = givenADefaultHalo(mMockServer.start());
        mHaloContentApi = givenAContentApi(mHalo);
        mCallbackFlag = newCallbackFlag();
    }

    @Override
    public void onDestroy() throws IOException {
        mHalo.uninstall();
        mMockServer.shutdown();
    }

    @Test
    public void thatANetworkPaginatedSearchCannotBeRequestedAsCursor() {
        SearchQuery query = givenTheSimplestQuery();
        CallbackV2<Cursor> callback = givenCallbackWithErrorType(mCallbackFlag, Cursor.class, UnsupportedOperationException.class);

        ICancellable cancellable = mHaloContentApi.search(Data.NETWORK_ONLY, query)
                .asRaw()
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatANetworkRequestWithErrorProvidesInconsistentNoDataStatus() {
        SearchQuery query = givenTheSimplestQuery();
        enqueueServerError(mMockServer, 500);
        CallbackV2<Paginated<HaloContentInstance>> callback = givenCallbackThatChecksDataIsInconsistent(mCallbackFlag);

        ICancellable cancellable = mHaloContentApi.search(Data.NETWORK_ONLY, query)
                .asContent()
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatCanPerfomASearchQueryWithOperationLikePattern() throws IOException {
        enqueueServerFile(mMockServer, SEARCH_PAGINATED_RESPONSE);
        SearchQuery query = givenASearchLikePatternQuery();
        CallbackV2<Paginated<HaloContentInstance>> callback = givenCallbackContentSuccessData(mCallbackFlag,true);

        ICancellable cancellable = mHaloContentApi.search(Data.NETWORK_ONLY, query)
                .asContent()
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatANetworkPaginatedSearchProvidesDataWithValidResponse() throws IOException {
        enqueueServerFile(mMockServer, SEARCH_PAGINATED_RESPONSE);
        SearchQuery query = givenTheSimplestQuery();
        CallbackV2<Paginated<HaloContentInstance>> callback = givenCallbackContentSuccessData(mCallbackFlag, true);

        ICancellable cancellable = mHaloContentApi.search(Data.NETWORK_ONLY, query)
                .asContent()
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatANetworkPaginatedSearchProvidesDataParsedWithValidResponse() throws IOException {
        enqueueServerFile(mMockServer, SEARCH_PAGINATED_RESPONSE);
        SearchQuery query = givenTheSimplestQuery();
        CallbackV2<List<DummyItem>> callback = givenCallbackContentParsedSuccessData(mCallbackFlag, true, false);

        ICancellable cancellable = mHaloContentApi.search(Data.NETWORK_ONLY, query)
                .asContent(DummyItem.class)
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatANetworkNotPaginatedSearchProvidesDataWithValidResponse() throws IOException {
        enqueueServerFile(mMockServer, SEARCH_NOT_PAGINATED_RESPONSE);
        SearchQuery query = givenANotPaginatedQuery();
        CallbackV2<Paginated<HaloContentInstance>> callback = givenCallbackContentSuccessData(mCallbackFlag, true);

        ICancellable cancellable = mHaloContentApi.search(Data.NETWORK_ONLY, query)
                .asContent()
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatANetworkNotPaginatedSearchProvidesDataParsedWithValidResponse() throws IOException {
        enqueueServerFile(mMockServer, SEARCH_NOT_PAGINATED_RESPONSE);
        SearchQuery query = givenANotPaginatedQuery();
        CallbackV2<List<DummyItem>> callback = givenCallbackContentParsedSuccessData(mCallbackFlag, true, false);

        ICancellable cancellable = mHaloContentApi.search(Data.NETWORK_ONLY, query)
                .asContent(DummyItem.class)
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatALocalSearchProvidesEmptyResponseWithoutData() {
        SearchQuery query = givenTheSimplestQuery();
        CallbackV2<List<DummyItem>> callback = givenCallbackContentParsedEmptyDataLocal(mCallbackFlag);

        ICancellable cancellable = mHaloContentApi.search(Data.STORAGE_ONLY, query)
                .asContent(DummyItem.class)
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatANetworkAndLocalRequestProvidesHaloContentInstances() throws IOException {
        enqueueServerFile(mMockServer, SEARCH_PAGINATED_RESPONSE);
        SearchQuery query = givenTheSimplestQuery();
        CallbackV2<Paginated<HaloContentInstance>> callback = givenCallbackContentSuccessData(mCallbackFlag, true);

        ICancellable cancellable = mHaloContentApi.search(Data.NETWORK_AND_STORAGE, query)
                .asContent()
                .execute(callback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(cancellable).isNotNull();
    }

    @Test
    public void thatALocalSearchProvidesSameResponseAfterCachingSameQuery() throws IOException {
        enqueueServerFile(mMockServer, SEARCH_PAGINATED_RESPONSE);
        SearchQuery query = givenTheSimplestQuery();
        CallbackV2<List<DummyItem>> fullCallback = givenCallbackContentParsedSuccessData(mCallbackFlag, true, false);
        CallbackV2<List<DummyItem>> cachedCallback = givenCallbackContentParsedSuccessData(mCallbackFlag, false, false);

        //Request to cache
        ICancellable cancellableFullRequest = mHaloContentApi.search(Data.NETWORK_AND_STORAGE, query)
                .asContent(DummyItem.class)
                .execute(fullCallback);

        //Cached query
        ICancellable cancellableCachedRequest = mHaloContentApi.search(Data.STORAGE_ONLY, query)
                .asContent(DummyItem.class)
                .execute(cachedCallback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(2);
        assertThat(cancellableFullRequest).isNotNull();
        assertThat(cancellableCachedRequest).isNotNull();
    }

    @Test
    public void thatALocalSearchProvidesEmptyResponseAfterCachingWithDifferentQuery() throws IOException {
        enqueueServerFile(mMockServer, SEARCH_PAGINATED_RESPONSE);
        SearchQuery query = givenTheSimplestQuery();
        SearchQuery complexQuery = givenAComplexQuery();
        CallbackV2<List<DummyItem>> fullCallback = givenCallbackContentParsedSuccessData(mCallbackFlag, true, false);
        CallbackV2<List<DummyItem>> cachedCallback = givenCallbackContentParsedEmptyDataLocal(mCallbackFlag);

        //Request to cache
        ICancellable cancellableFullRequest = mHaloContentApi.search(Data.NETWORK_AND_STORAGE, query)
                .asContent(DummyItem.class)
                .execute(fullCallback);

        //Cached query
        ICancellable cancellableCachedRequest = mHaloContentApi.search(Data.STORAGE_ONLY, complexQuery)
                .asContent(DummyItem.class)
                .execute(cachedCallback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(2);
        assertThat(cancellableFullRequest).isNotNull();
        assertThat(cancellableCachedRequest).isNotNull();
    }

    @Test
    public void thatALocalSearchTimedOutIsEmpty() throws IOException, InterruptedException {
        enqueueServerFile(mMockServer, SEARCH_PAGINATED_RESPONSE);
        long waitingTime = 100;
        long timeWindow = 50;
        SearchQuery query = givenTimedCacheQuery(waitingTime);
        CallbackV2<List<DummyItem>> fullCallback = givenCallbackContentParsedSuccessData(mCallbackFlag, true, false);
        CallbackV2<List<DummyItem>> cachedCallback = givenCallbackContentParsedEmptyDataLocal(mCallbackFlag);

        //Request to cache
        ICancellable cancellableFullRequest = mHaloContentApi.search(Data.NETWORK_AND_STORAGE, query)
                .asContent(DummyItem.class)
                .execute(fullCallback);

        Thread.sleep(waitingTime + timeWindow);

        //Cached query
        ICancellable cancellableCachedRequest = mHaloContentApi.search(Data.STORAGE_ONLY, query)
                .asContent(DummyItem.class)
                .execute(cachedCallback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
        assertThat(mCallbackFlag.timesExecuted()).isEqualTo(2);
        assertThat(cancellableFullRequest).isNotNull();
        assertThat(cancellableCachedRequest).isNotNull();
    }

    @Test
    public void thatANetworkLocalSearchProvidesLocalIfOnNetworkError() throws IOException {
        enqueueServerFile(mMockServer, SEARCH_PAGINATED_RESPONSE);
        enqueueServerError(mMockServer, 500);
        SearchQuery query = givenTheSimplestQuery();

        mHaloContentApi.search(Data.NETWORK_AND_STORAGE, query)
                .asContent()
                .execute(null);

        CallbackV2<List<DummyItem>> cachedCallback = givenCallbackContentParsedSuccessData(mCallbackFlag, false, true);
        mHaloContentApi.search(Data.NETWORK_AND_STORAGE, query)
                .asContent(DummyItem.class)
                .execute(cachedCallback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatANetworkAndStorageSearchWithRepeatedAndTtlOverridesTheTttl() throws IOException, InterruptedException {
        long timeWindow = 50;
        long waitingTime = 100;
        enqueueServerFile(mMockServer, SEARCH_PAGINATED_RESPONSE);
        enqueueServerFile(mMockServer, SEARCH_PAGINATED_RESPONSE);
        SearchQuery query = givenTimedCacheQuery(waitingTime);
        SearchQuery queryWithDefaultTtl = givenTheSimplestQuery();
        CallbackV2<List<DummyItem>> cachedCallback = givenCallbackContentParsedSuccessData(mCallbackFlag, false, false);

        mHaloContentApi.search(Data.NETWORK_AND_STORAGE, query)
                .asContent()
                .execute(null);

        mHaloContentApi.search(Data.NETWORK_AND_STORAGE, queryWithDefaultTtl)
                .asContent()
                .execute(null);

        Thread.sleep(waitingTime + timeWindow);

        mHaloContentApi.search(Data.STORAGE_ONLY, queryWithDefaultTtl)
                .asContent(DummyItem.class)
                .execute(cachedCallback);

        assertThat(mCallbackFlag.isFlagged()).isTrue();
    }

    @Test
    public void thatCanSearchWithInOperatorArraySet() throws IOException {
        enqueueServerFile(mMockServer, SEARCH_PAGINATED_RESPONSE);
        Date now = new Date();
        List<String> ids = new ArrayList<>();
        ids.add("1");
        ids.add("2");
        ids.add("3");
        SearchQuery query = SearchQuery.builder()
                .beginMetaSearch()
                .lte("publishedAt", now)
                .and()
                .eq("deletedAt", null)
                .and()
                .beginGroup()
                .gt("removedAt", now)
                .or()
                .eq("removedAt", null)
                .endGroup()
                .end()
                .populate("Artist", "Venue", "Artwork Category")
                .beginSearch()
                .in("Artwork ID", ids)
                .end()
                .moduleIds("myAwesomeID")
                .onePage(true)
                .build();

        HaloContentApi.with(mHalo)
                .search(Data.NETWORK_AND_STORAGE, query)
                .asContent()
                .execute(new CallbackV2<Paginated<HaloContentInstance>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<Paginated<HaloContentInstance>> result) {
                        assertThat(result.status().isOk()).isTrue();
                    }
                });

    }

}
