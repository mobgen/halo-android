package com.mobgen.halo.android.framework.network.exceptions;

import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.api.HaloNetworkApi;
import com.mobgen.halo.android.framework.mock.FrameworkMock;
import com.mobgen.halo.android.framework.network.client.HaloNetClient;
import com.mobgen.halo.android.framework.network.client.request.HaloRequest;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.mobgen.halo.android.framework.mock.FrameworkMock.givenAHaloNetWorkApi;
import static com.mobgen.halo.android.framework.mock.fixtures.ServerFixtures.GET_TEST_ITEM;
import static com.mobgen.halo.android.framework.mock.fixtures.ServerFixtures.enqueueServerError;
import static com.mobgen.halo.android.framework.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAGetRequest;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenAGetRequestTyped;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenNetClient;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloNetworkExceptionResolverTest extends HaloRobolectricTest {
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
    public void thatHandleNotConnectivity() throws IOException {
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAGetRequest(networkApi);
        HaloNetworkExceptionResolver haloNetworkExceptionResolver = new HaloNetworkExceptionResolver();
        HaloNetException haloNetException = haloNetworkExceptionResolver.resolve(new HaloConnectionException("No connectivity",new Exception()),request.buildOkRequest(),false);
        assertThat(haloNetException).isNotNull();
        assertThat(haloNetException.getMessage()).contains("not connectivity");
    }

    @Test
    public void thatHandleUnknownExceptionWhenConnectivityIsAvailable() throws IOException {
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAGetRequest(networkApi);
        HaloNetworkExceptionResolver haloNetworkExceptionResolver = new HaloNetworkExceptionResolver();
        HaloNetException haloUnknownServerException = haloNetworkExceptionResolver.resolve(new HaloUnknownServerException("Unknown exception",new Exception()),request.buildOkRequest(),true);
        assertThat(haloUnknownServerException).isNotNull();
        assertThat(haloUnknownServerException.getMessage()).contains("unknown exception");
    }

    @Test
    public void thatHandleUnknownExceptionsWhenConnectivityIsAvailable() throws IOException {
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAGetRequest(networkApi);
        HaloNetworkExceptionResolver haloNetworkExceptionResolver = new HaloNetworkExceptionResolver();
        HaloNetException haloUnknownServerException = haloNetworkExceptionResolver.resolve(new HaloUnknownServerException("Unknown exception"),request.buildOkRequest(),true);
        assertThat(haloUnknownServerException).isNotNull();
        assertThat(haloUnknownServerException.getMessage()).contains("unknown exception");
    }

    @Test
    public void thatHandleNotFoundExceptionIsThrown() throws IOException {
        enqueueServerError(mMockServer,404);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAGetRequest(networkApi);
        HaloNetworkExceptionResolver haloNetworkExceptionResolver = new HaloNetworkExceptionResolver();
        try{
            haloNetworkExceptionResolver.resolve(request.execute());
        }
        catch (HaloNotFoundException haloNotFoundException){
            assertThat(haloNotFoundException).isNotNull();
            assertThat(haloNotFoundException.getMessage()).contains("404");
        }
    }

    @Test
    public void thatAuthenticationExceptionIsThrown() throws IOException {
        enqueueServerError(mMockServer,401);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAGetRequest(networkApi);
        HaloNetworkExceptionResolver haloNetworkExceptionResolver = new HaloNetworkExceptionResolver();
        try{
            haloNetworkExceptionResolver.resolve(request.execute());
        }
        catch (HaloAuthenticationException haloAuthenticationException){
            assertThat(haloAuthenticationException).isNotNull();
            assertThat(haloAuthenticationException.getMessage()).contains("401");
        }
    }

    @Test
    public void thatHaloServerExceptionIsThrown() throws IOException {
        enqueueServerError(mMockServer,500);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAGetRequest(networkApi);
        HaloNetworkExceptionResolver haloNetworkExceptionResolver = new HaloNetworkExceptionResolver();
        try{
            haloNetworkExceptionResolver.resolve(request.execute());
        }
        catch (HaloServerException haloServerException){
            assertThat(haloServerException).isNotNull();
            assertThat(haloServerException.getMessage()).contains("500");
            assertThat(haloServerException.getErrorCode()).isEqualTo(500);
            assertThat(haloServerException.toString()).contains("500");
            assertThat(haloServerException.getBody()).isEqualTo("");
        }
    }

    @Test
    public void thatHaloUnknownServerExceptionIsThrown() throws IOException {
        enqueueServerError(mMockServer,301);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAGetRequest(networkApi);
        HaloNetworkExceptionResolver haloNetworkExceptionResolver = new HaloNetworkExceptionResolver();
        try{
            haloNetworkExceptionResolver.resolve(request.execute());
        }
        catch (HaloUnknownServerException haloUnknownServerException){
            assertThat(haloUnknownServerException).isNotNull();
            assertThat(haloUnknownServerException.getMessage()).contains("301");
        }
    }

    @Test
    public void thatHaloParseNetExceptionIsThrown() throws IOException {
        enqueueServerFile(mMockServer,GET_TEST_ITEM);
        HaloNetworkApi networkApi = givenAHaloNetWorkApi(mFramework,mEndpointURL);
        HaloRequest request = givenAGetRequestTyped(networkApi);
        try{
            Integer response = mHaloNetClient.request(request,Integer.class);
        }
        catch (HaloNetParseException haloNetParseException){
            assertThat(haloNetParseException).isNotNull();
            assertThat(haloNetParseException.getMessage()).contains("Error parsing");
        }
    }
}
