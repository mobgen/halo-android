package com.mobgen.halo.android.framework.network.client.request;


import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.mock.FrameworkMock;
import com.mobgen.halo.android.framework.network.client.response.TypeReference;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.Response;

import static com.mobgen.halo.android.framework.mock.FrameworkMock.givenAHaloNetWorkApi;
import static com.mobgen.halo.android.framework.mock.fixtures.ServerFixtures.GET_TEST_ITEM;
import static com.mobgen.halo.android.framework.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAGetRequest;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAGetRequestTyped;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAGetRequestWithCache;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAGetRequestWithParams;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAGetRequestWithParamsAndSession;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAGetRequest;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloRequestTest extends HaloRobolectricTest {

    private HaloFramework mFramework;
    private MockServer mMockServer;
    private String mEndpointURL;

    @Before
    public void initialize() throws IOException {
        mMockServer = MockServer.create();
        mEndpointURL = mMockServer.start();
        mFramework = FrameworkMock.createSameThreadFramework(mEndpointURL);
    }

    @After
    public void tearDown() throws IOException {
        mMockServer.shutdown();
    }

    @Test
    public void thatMakeARequest() throws IOException {
        enqueueServerFile(mMockServer, GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework, mEndpointURL);
        HaloRequest request = givenAGetRequest(networkApi);
        Response response = request.execute();
        assertThat(response).isNotNull();
        assertThat(response.isSuccessful()).isTrue();
    }

    @Test
    public void thatMakeARequestWithParams() throws IOException {
        enqueueServerFile(mMockServer, GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework, mEndpointURL);
        HaloRequest request = givenAGetRequestWithParams(networkApi);
        Response response = request.execute();
        assertThat(response).isNotNull();
        assertThat(request.buildOkRequest().header("myHeader")).isEqualTo("myHeaderValue");
        assertThat(response.isSuccessful()).isTrue();
    }

    @Test
    public void thatMakeARequestWithParamsAndSession() throws IOException {
        enqueueServerFile(mMockServer, GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework, mEndpointURL);
        HaloRequest request = givenAGetRequestWithParamsAndSession(networkApi);
        Response response = request.execute();
        assertThat(response).isNotNull();
        assertThat(request.buildOkRequest().header("myHeader")).isEqualTo("myHeaderValue");
        assertThat(response.isSuccessful()).isTrue();
    }

    @Test
    public void thatMakeARequestWithClass() throws IOException, HaloParsingException, JSONException {
        enqueueServerFile(mMockServer, GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework, mEndpointURL);
        HaloRequest request = givenAGetRequestTyped(networkApi);
        String response = request.execute(String.class);
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo("ExampleResponseTestFromNetWork");
    }

    @Test
    public void thatMakeARequestWithTyped() throws IOException, HaloParsingException, JSONException {
        enqueueServerFile(mMockServer, GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework, mEndpointURL);
        HaloRequest request = givenAGetRequestTyped(networkApi);
        String response = request.execute(new TypeReference<String>() {
        });
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo("ExampleResponseTestFromNetWork");
    }

    @Test
    public void thatCanSetServerCacheOnRequest() throws IOException {
        enqueueServerFile(mMockServer, GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework, mEndpointURL);
        HaloRequest request = givenAGetRequestWithCache(networkApi);
        Response response = request.execute();
        assertThat(response).isNotNull();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(request.buildOkRequest().header("to-cache")).isEqualTo("1234");
    }

    @Test
    public void thatCanSetCacheControlOnRequest() throws IOException {
        enqueueServerFile(mMockServer, GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework, mEndpointURL);
        HaloRequest request = givenAGetRequestWithCache(networkApi);
        Response response = request.execute();
        assertThat(response).isNotNull();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(request.buildOkRequest().header("cache-control")).isEqualTo("no-cache");
    }

    @Test
    public void thatCustomUserAgentIsSentOnRequest() throws IOException {
        enqueueServerFile(mMockServer, GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework, mEndpointURL);
        HaloRequest request = givenAGetRequest(networkApi);
        Response response = request.execute();
        assertThat(response).isNotNull();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(request.buildOkRequest().header("User-Agent")).isNotNull();
        assertThat(request.buildOkRequest().header("User-Agent")).isNotEmpty();
    }
}
