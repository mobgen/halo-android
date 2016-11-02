package com.mobgen.halo.android.framework.network.interceptors;


import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.mock.FrameworkMock;
import com.mobgen.halo.android.framework.network.client.HaloNetClient;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Response;

import static com.mobgen.halo.android.framework.mock.FrameworkMock.givenAHaloNetWorkApi;
import static com.mobgen.halo.android.framework.mock.fixtures.ServerFixtures.GET_TEST_ITEM;
import static com.mobgen.halo.android.framework.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAGetRequest;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAGetRequestWithParamsAndSessionAndBody;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenNetClient;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloInterceptorTest extends HaloRobolectricTest {

    private CallbackFlag mCallbackFlag;
    private HaloNetClient mHaloNetClient;
    private HaloFramework mFramework;
    private MockServer mMockServer;
    private String mEndpointURL;

    @Before
    public void initialize() throws IOException {
        mMockServer = MockServer.create();
        mEndpointURL = mMockServer.start();
        mFramework = FrameworkMock.createSameThreadFramework(mEndpointURL);
        mCallbackFlag = newCallbackFlag();
        mHaloNetClient = givenNetClient();
    }

    @After
    public void tearDown() throws IOException {
        mMockServer.shutdown();
    }

    @Test
    public void thatInterceptToProfiler() throws IOException {
        enqueueServerFile(mMockServer,GET_TEST_ITEM);
        HaloProfilerInterceptor profilerInterceptor = new HaloProfilerInterceptor();
        OkHttpClient.Builder builder = mHaloNetClient.ok().newBuilder()
                .addInterceptor(profilerInterceptor);
        mHaloNetClient.overrideOk(builder);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAGetRequest(networkApi);
        Response response = mHaloNetClient.request(request);
        assertThat(response).isNotNull();
        assertThat(builder.interceptors().size()).isGreaterThan(0);
    }

    @Test
    public void thatInterceptToLog() throws IOException {
        enqueueServerFile(mMockServer,GET_TEST_ITEM);
        HaloLogInterceptor logInterceptor = new HaloLogInterceptor();
        OkHttpClient.Builder builder = mHaloNetClient.ok().newBuilder()
                .addInterceptor(logInterceptor);
        mHaloNetClient.overrideOk(builder);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAGetRequest(networkApi);
        Response response = mHaloNetClient.request(request);
        assertThat(response).isNotNull();
        assertThat(builder.interceptors().size()).isGreaterThan(0);
    }

    @Test
    public void thatARequestInterceptFromCurl() throws IOException {
        enqueueServerFile(mMockServer,GET_TEST_ITEM);
        HaloCurlInterceptor curlInterceptor = new HaloCurlInterceptor();
        OkHttpClient.Builder builder = mHaloNetClient.ok().newBuilder()
                .addInterceptor(curlInterceptor);
        mHaloNetClient.overrideOk(builder);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAGetRequest(networkApi);
        Response response = mHaloNetClient.request(request);
        assertThat(response).isNotNull();
        assertThat(builder.interceptors().size()).isGreaterThan(0);
    }

    @Test
    public void thatARequestWithBodyAndHeaderInterceptFromCurl() throws IOException, JSONException {
        enqueueServerFile(mMockServer,GET_TEST_ITEM);
        HaloCurlInterceptor curlInterceptor = new HaloCurlInterceptor();
        OkHttpClient.Builder builder = mHaloNetClient.ok().newBuilder()
                .addInterceptor(curlInterceptor);
        mHaloNetClient.overrideOk(builder);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAGetRequestWithParamsAndSessionAndBody(networkApi);
        Response response = mHaloNetClient.request(request);
        assertThat(response).isNotNull();
        assertThat(builder.interceptors().size()).isGreaterThan(0);
    }

}
