package com.mobgen.halo.android.framework.common.exceptions;

import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.mock.FrameworkMock;
import com.mobgen.halo.android.framework.network.client.HaloNetClient;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.mobgen.halo.android.framework.mock.instrumentation.HaloNetInstrument.givenNetClient;
import static com.mobgen.halo.android.testing.CallbackFlag.newCallbackFlag;
import static org.assertj.core.api.Java6Assertions.assertThat;


public class HaloExceptionTest extends HaloRobolectricTest {
    private CallbackFlag mCallbackFlag;
    private HaloFramework mFramework;
    private String mEndpointURL;

    @Before
    public void initialize() {
        mEndpointURL = "";
        mFramework = FrameworkMock.createSameThreadFramework(mEndpointURL);
        mCallbackFlag = newCallbackFlag();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void thatCanCreateAHaloConfigurationException(){
        HaloConfigurationException haloConfigurationException = new HaloConfigurationException("HaloConfigurationException",new Exception());
        assertThat(haloConfigurationException.getMessage()).contains("HaloConfigurationException");
    }

    @Test
    public void thatCanCreateAHaloParsingException(){
        HaloParsingException haloParsingException = new HaloParsingException("HaloParsingException",new Exception());
        assertThat(haloParsingException.getMessage()).contains("HaloParsingException");
    }

    @Test
    public void thatCanCreateAHaloSecurityException(){
        HaloSecurityException haloSecurityException = new HaloSecurityException("HalohaloSecurityException",new Exception());
        assertThat(haloSecurityException.getMessage()).contains("HalohaloSecurityException");
    }

    @Test
    public void thatCanCreateAHaloIntegrationException(){
        HaloIntegrationException haloIntegrationException = new HaloIntegrationException("HalohaloIntegrationException",new Exception());
        assertThat(haloIntegrationException.getMessage()).contains("HalohaloIntegrationException");
    }

    @Test
    public void thatCanCreateAHaloIntegrationExceptionWithCode(){
        HaloIntegrationException haloIntegrationException = new HaloIntegrationException("HalohaloIntegrationMessage",1234,"HalohaloIntegrationException",new Exception());
        assertThat(haloIntegrationException.getMessage()).contains("HalohaloIntegrationException");
        assertThat(haloIntegrationException.statusCode()).isEqualTo(1234);
        assertThat(haloIntegrationException.integrationMessage()).contains("HalohaloIntegrationMessage");
    }

    @Test
    public void thatCanCreateAHaloIntegrationExceptionWithoutException(){
        HaloIntegrationException haloIntegrationException = new HaloIntegrationException("HalohaloIntegrationException");
        assertThat(haloIntegrationException.getMessage()).contains("HalohaloIntegrationException");
    }
}
