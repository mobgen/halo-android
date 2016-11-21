package com.mobgen.halo.android.framework.api;

import com.mobgen.halo.android.framework.mock.FrameworkMock;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenNetClient;
import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenNetClientWithoutPinning;
import com.mobgen.halo.android.framework.network.client.endpoint.HaloEndpoint;
import com.mobgen.halo.android.framework.network.client.endpoint.HaloEndpointCluster;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;


public class HaloNetworkApiTest extends HaloRobolectricTest {

    private HaloFramework mFramework;
    private CallbackFlag mCallbackFlag;
    private HaloNetworkApi mNetworkApi;

    @Before
    public void initialize() {
        mFramework = FrameworkMock.createSameThreadFramework("myTestEndpoint");
        mNetworkApi = new HaloNetworkApi(mFramework,givenNetClient());
        mCallbackFlag = newCallbackFlag();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void thatRequestUrl(){
        mNetworkApi = new HaloNetworkApi(mFramework,givenNetClient());
        assertThat(mNetworkApi.requestUrl("1","get")).contains("HaloNetClientTest");
        assertThat(mNetworkApi.requestUrl("1","get")).contains("get");
    }

    @Test
    public void thatRequestUrlWithoutPinningSHA(){
        mNetworkApi = new HaloNetworkApi(mFramework,givenNetClientWithoutPinning());
        assertThat(mNetworkApi.requestUrl("1","get")).contains("HaloNetClientTest");
        assertThat(mNetworkApi.requestUrl("1","get")).contains("get");
    }

    @Test
    public void thatEndpointAreEqual(){
        HaloEndpoint endpoint = new HaloEndpoint("1","HaloNetClientTest","sha256/1234");
        HaloEndpointCluster endpointCluster = new HaloEndpointCluster(endpoint);
        assertThat(endpointCluster.getEndpoint("1").equals(endpoint)).isTrue();
    }

    @Test
    public void thatEndpointAreNotEqual(){
        HaloEndpoint endpoint = new HaloEndpoint("1","HaloNetClientTest","sha256/1234");
        HaloEndpointCluster endpointCluster = new HaloEndpointCluster(endpoint);
        assertThat(endpointCluster.getEndpoint("1").equals(1)).isFalse();
    }

    @Test
    public void thatGetNetClient(){
        assertThat(mNetworkApi.client()).isNotNull();
    }

    @Test
    public void thatGetContext(){
        assertThat(mNetworkApi.context()).isNotNull();
    }

    @Test
    public void thatGetFramework(){
        assertThat(mNetworkApi.framework()).isEqualTo(mFramework);
    }



}
