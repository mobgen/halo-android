package com.mobgen.halo.android.content.search;

import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetParseException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageException;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.Test;

import java.lang.reflect.Field;

import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenACustomHalo;
import static com.mobgen.halo.android.content.mock.instrumentation.HaloMock.givenASingleThreadedWithParserConfig;
import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenCallbackThatChecksDataIsInconsistent;
import static com.mobgen.halo.android.content.mock.instrumentation.SearchInstruments.givenTheSimplestQuery;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class HaloContentSearchMockedSourceTest extends HaloRobolectricTest {

    private Parser.Factory mParserFactory;
    private ContentSearchRemoteDatasource mRemoteDatasource;
    private ContentSearchLocalDatasource mLocalDatasource;

    private Halo mHalo;
    private HaloContentApi mContentApi;

    private CallbackFlag mCallbackFlag;

    @Override
    public void onStart() throws Exception {
        mCallbackFlag = new CallbackFlag();
        mParserFactory = mock(Parser.Factory.class);
        mRemoteDatasource = mock(ContentSearchRemoteDatasource.class);
        mLocalDatasource = mock(ContentSearchLocalDatasource.class);
        mHalo = givenACustomHalo(givenASingleThreadedWithParserConfig("", mParserFactory));
        mContentApi = HaloContentApi.with(mHalo);

        //Replace the content repository
        Field field = HaloContentApi.class.getDeclaredField("mContentSearchRepository");
        field.setAccessible(true);
        field.set(mContentApi, new ContentSearchRepository(mRemoteDatasource, mLocalDatasource));
        field.setAccessible(false);
    }

    @Override
    public void onDestroy() throws Exception {
        mHalo.uninstall();
    }

    @Test
    public void thatANetworkParsingErrorProvidesInconsistentData() throws HaloNetException {
        when(mRemoteDatasource.findByQuery(any(SearchQuery.class))).thenThrow(HaloNetParseException.class);
        SearchQuery query = givenTheSimplestQuery();
        CallbackV2<Paginated<HaloContentInstance>> callback = givenCallbackThatChecksDataIsInconsistent(mCallbackFlag);
        mContentApi.search(Data.NETWORK_ONLY, query)
                .asContent()
                .execute(callback);
    }

    @Test
    public void thatALocalParsingErrorProvidesInconsistentData() throws HaloStorageException {
        when(mLocalDatasource.findByQuery(any(SearchQuery.class))).thenThrow(HaloNetParseException.class);
        SearchQuery query = givenTheSimplestQuery();
        CallbackV2<Paginated<HaloContentInstance>> callback = givenCallbackThatChecksDataIsInconsistent(mCallbackFlag);
        mContentApi.search(Data.STORAGE_ONLY, query)
                .asContent()
                .execute(callback);
    }

    @Test
    public void thatANetworkAndLocalParsingErrorProvidesInconsistentData() throws HaloStorageException {
        when(mLocalDatasource.findByQuery(any(SearchQuery.class))).thenThrow(HaloNetParseException.class);
        SearchQuery query = givenTheSimplestQuery();
        CallbackV2<Paginated<HaloContentInstance>> callback = givenCallbackThatChecksDataIsInconsistent(mCallbackFlag);
        mContentApi.search(Data.NETWORK_AND_STORAGE, query)
                .asContent()
                .execute(callback);
    }
}
