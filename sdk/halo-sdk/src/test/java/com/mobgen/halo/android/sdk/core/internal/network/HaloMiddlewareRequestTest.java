package com.mobgen.halo.android.sdk.core.internal.network;

import com.mobgen.halo.android.framework.network.client.body.HaloBodyFactory;
import com.mobgen.halo.android.framework.network.client.body.HaloMediaType;
import com.mobgen.halo.android.framework.network.client.request.HaloRequestMethod;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.mock.HaloMock;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloGetRawShadow;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Response;

import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.MIDDLEWARE_REQUEST;
import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

@Config(shadows = {HaloGetRawShadow.class})
public class HaloMiddlewareRequestTest extends HaloRobolectricTest {

    private MockServer mMockServer;
    private Halo mHalo;
    private CallbackFlag mCallbackFlag;

    @Before
    public void initialize() throws IOException {
        mCallbackFlag = newCallbackFlag();
        mMockServer = MockServer.create();
        mHalo = HaloMock.create(mMockServer.start());
    }

    @After
    public void tearDown() throws IOException {
        mHalo.uninstall();
        mMockServer.shutdown();
    }

    @Test
    public void thatMiddlewareGetRequestIsSuccesfull() throws IOException {
        enqueueServerFile(mMockServer, MIDDLEWARE_REQUEST);
        Object tag = mock(Object.class);
        HaloMiddlewareRequest middlewareRequest = new HaloMiddlewareRequest.Builder(mHalo.getCore().framework().network())
                .hasProxy(false)
                .header("customHeaderName", "customHeaderValue")
                .middleware("companyName", "companyDataType")
                .method(HaloRequestMethod.GET)
                .tag(tag)
                .url("getData")
                .build();
        Response response = middlewareRequest.execute();
        assertThat(response).isNotNull();
        assertThat(response.isSuccessful()).isTrue();
    }

    @Test
    public void thatMiddlewarePostRequestIsSuccesfull() throws IOException {
        enqueueServerFile(mMockServer, MIDDLEWARE_REQUEST);
        HaloMiddlewareRequest middlewareRequest = new HaloMiddlewareRequest.Builder(mHalo.getCore().framework().network())
                .hasProxy(true)
                .header("customHeaderName", "customHeaderValue")
                .middleware("companyName", "companyDataType")
                .body(HaloBodyFactory.stringBody(HaloMediaType.TEXT_PLAIN, "testPlainString"))
                .method(HaloRequestMethod.POST)
                .url("sendData")
                .build();

        Response response = middlewareRequest.execute();
        assertThat(response).isNotNull();
        assertThat(response.isSuccessful()).isTrue();
    }

    @Test
    public void thatMiddlewarePutRequestIsSuccesfull() throws IOException {
        enqueueServerFile(mMockServer, MIDDLEWARE_REQUEST);
        HaloMiddlewareRequest.Builder haloMiddlewareBuilder = HaloMiddlewareRequest.builder(mHalo.getCore().framework().network());
        HaloMiddlewareRequest middlewareRequest = haloMiddlewareBuilder.middleware("", "")
                .hasProxy(true)
                .header("customHeaderName", "customHeaderValue")
                .middleware("companyName", "companyDataType")
                .body(HaloBodyFactory.stringBody(HaloMediaType.TEXT_PLAIN, "testPlainString"))
                .method(HaloRequestMethod.PUT)
                .url("updateData", new HashMap<String, String>())
                .build();
        Response response = middlewareRequest.execute();
        assertThat(response).isNotNull();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(middlewareRequest.url()).isNotNull();

    }
}
