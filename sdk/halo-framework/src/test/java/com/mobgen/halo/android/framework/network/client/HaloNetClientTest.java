package com.mobgen.halo.android.framework.network.client;


import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.mock.FrameworkMock;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.framework.network.client.response.TypeReference;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import static com.mobgen.halo.android.framework.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenNetClient;
import static com.mobgen.halo.android.framework.mock.FrameworkMock.givenOkHttpBuilder;
import static com.mobgen.halo.android.framework.mock.FrameworkMock.givenAHaloNetWorkApi;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAGetRequest;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAGetRequestTyped;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAPostRequestWithStringBody;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAPostRequestWithJSONObjectBody;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAPostRequestWithJSONArrayBody;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAPostRequestWithFormBody;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAPostRequestWithFileBody;

import static com.mobgen.halo.android.framework.mock.fixtures.ServerFixtures.GET_TEST_ITEM;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;

import okhttp3.Response;

import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class HaloNetClientTest extends HaloRobolectricTest {

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
    public void thatMakeARequest() throws IOException {
        enqueueServerFile(mMockServer,GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAGetRequest(networkApi);
        Response response = mHaloNetClient.request(request);
        assertThat(response).isNotNull();
        assertThat(response.isSuccessful()).isTrue();
    }

    @Test
    public void thatMakeARequestWithClass() throws IOException, HaloParsingException, JSONException {
        enqueueServerFile(mMockServer,GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAGetRequestTyped(networkApi);
        String response = mHaloNetClient.request(request,String.class);
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo("ExampleResponseTestFromNetWork");
    }

    @Test
    public void thatMakeARequestWithTyped() throws IOException, HaloParsingException, JSONException {
        enqueueServerFile(mMockServer,GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAGetRequestTyped(networkApi);
        String response = mHaloNetClient.request(request, new TypeReference<String>(){});
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo("ExampleResponseTestFromNetWork");
    }

    @Test
    public void thatOverrideOkHttp(){
        mHaloNetClient.overrideOk(givenOkHttpBuilder());
        assertThat(mHaloNetClient.ok()).isNotNull();
    }

    @Test
    public void thatReturnTheContext(){
        assertThat(mHaloNetClient.context()).isEqualTo(RuntimeEnvironment.application);
    }

    @Test
    public void thatCanPostARequestWithStringBody() throws IOException {
        enqueueServerFile(mMockServer,GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAPostRequestWithStringBody(networkApi);
        String response = mHaloNetClient.request(request,String.class);
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo("ExampleResponseTestFromNetWork");
    }

    @Test
    public void thatCanPostARequestWithFormBody() throws IOException {
        enqueueServerFile(mMockServer,GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAPostRequestWithFormBody(networkApi);
        String response = mHaloNetClient.request(request,String.class);
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo("ExampleResponseTestFromNetWork");
    }

    @Test
    public void thatCanPostARequestWithFileBody() throws IOException {
        enqueueServerFile(mMockServer,GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAPostRequestWithFileBody(networkApi,mHaloNetClient.context());
        String response = mHaloNetClient.request(request,String.class);
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo("ExampleResponseTestFromNetWork");
    }

    @Test
    public void thatCanPostARequestWithJSONObjectBody() throws IOException, JSONException {
        enqueueServerFile(mMockServer,GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAPostRequestWithJSONObjectBody(networkApi);
        String response = mHaloNetClient.request(request,String.class);
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo("ExampleResponseTestFromNetWork");
    }

    @Test
    public void thatCanPostARequestWithJSONArrayBody() throws IOException, JSONException {
        enqueueServerFile(mMockServer,GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAPostRequestWithJSONArrayBody(networkApi);
        String response = mHaloNetClient.request(request,String.class);
        assertThat(response).isNotNull();
        assertThat(response).isEqualTo("ExampleResponseTestFromNetWork");
    }

}
