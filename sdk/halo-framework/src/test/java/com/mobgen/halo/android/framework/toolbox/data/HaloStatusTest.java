package com.mobgen.halo.android.framework.toolbox.data;


import com.mobgen.halo.android.framework.common.exceptions.HaloIntegrationException;
import com.mobgen.halo.android.framework.network.exceptions.HaloAuthenticationException;
import com.mobgen.halo.android.framework.network.exceptions.HaloConnectionException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNetParseException;
import com.mobgen.halo.android.framework.network.exceptions.HaloNotFoundException;
import com.mobgen.halo.android.framework.network.exceptions.HaloServerException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageGeneralException;
import com.mobgen.halo.android.framework.storage.exceptions.HaloStorageParseException;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloStatusTest extends HaloRobolectricTest {

    @Before
    public void initialize() {
    }

    @After
    public void tearDown() {

    }

    @Test
    public void thatCanModifyAHaloStatusAndHandleAHaloServerException(){
        HaloStatus status =  HaloStatus.builder().build();
        HaloStatus statusModified = status.modify().error(new HaloServerException("Could not reach server","BodyResquest",500)).build();
        assertThat(statusModified.dataStatus()).isEqualTo(HaloStatus.STATUS_INCONSISTENT);
        assertThat(statusModified.errorStatus()).isEqualTo(HaloStatus.STATUS_INTERNAL_SERVER_ERROR);
        assertThat(statusModified.isInternalServerError()).isTrue();
        assertThat(statusModified.isInconsistent()).isTrue();
        assertThat(statusModified.isNetworkError()).isTrue();
        assertThat(statusModified.getExceptionMessage()).contains("server");
        assertThat(statusModified.toString()).contains("inconsistent");
        assertThat(statusModified.toString()).contains("internal");
    }

    @Test
    public void thatCanHandleAHaloAuthenticationException(){
        HaloStatus status =  HaloStatus.builder().error(new HaloAuthenticationException("Could not authenticate")).build();
        assertThat(status.dataStatus()).isEqualTo(HaloStatus.STATUS_INCONSISTENT);
        assertThat(status.errorStatus()).isEqualTo(HaloStatus.STATUS_SECURITY_ERROR);
        assertThat(status.isSecurityError()).isTrue();
        assertThat(status.isInconsistent()).isTrue();
        assertThat(status.isNetworkError()).isTrue();
        assertThat(status.toString()).contains("inconsistent");
        assertThat(status.toString()).contains("security");
    }

    @Test
    public void thatCanHandleAHaloNotFoundException(){
        HaloStatus status =  HaloStatus.builder().error(new HaloNotFoundException("Could not found resource")).build();
        assertThat(status.dataStatus()).isEqualTo(HaloStatus.STATUS_INCONSISTENT);
        assertThat(status.errorStatus()).isEqualTo(HaloStatus.STATUS_NOT_FOUND_ERROR);
        assertThat(status.isNotFoundException()).isTrue();
        assertThat(status.isInconsistent()).isTrue();
        assertThat(status.isNetworkError()).isTrue();
        assertThat(status.toString()).contains("inconsistent");
        assertThat(status.toString()).contains("found");
    }

    @Test
    public void thatCanHandleAHaloConnectionException(){
        HaloStatus.Builder builder = HaloStatus.builder().error(new HaloConnectionException("Could not found resource", new Exception()));
        HaloStatus status =  builder.build();
        assertThat(status.dataStatus()).isEqualTo(HaloStatus.STATUS_INCONSISTENT);
        assertThat(status.errorStatus()).isEqualTo(HaloStatus.STATUS_CONNECTION_ERROR);
        assertThat(status.isNetworkError()).isTrue();
        assertThat(builder.isConnectionError()).isTrue();
        assertThat(status.toString()).contains("connection");
    }

    @Test
    public void thatCanHandleAHaloStorageGeneralException(){
        HaloStatus status =  HaloStatus.builder().error(new HaloStorageGeneralException("Storage is not working", new Exception())).build();
        assertThat(status.dataStatus()).isEqualTo(HaloStatus.STATUS_INCONSISTENT);
        assertThat(status.errorStatus()).isEqualTo(HaloStatus.STATUS_GENERAL_STORAGE_ERROR);
        assertThat(status.isGeneralStorageError()).isTrue();
        assertThat(status.isInconsistent()).isTrue();
        assertThat(status.isStorageError()).isTrue();
        assertThat(status.toString()).contains("inconsistent");
        assertThat(status.toString()).contains("storage");
    }

    @Test
    public void thatCanHandleAHaloNetParseException(){
        HaloStatus status =  HaloStatus.builder().error(new HaloNetParseException("Could no parse data from net", new Exception())).build();
        assertThat(status.dataStatus()).isEqualTo(HaloStatus.STATUS_INCONSISTENT);
        assertThat(status.errorStatus()).isEqualTo(HaloStatus.STATUS_NETWORK_PARSE_ERROR);
        assertThat(status.isNetworkParseError()).isTrue();
        assertThat(status.isInconsistent()).isTrue();
        assertThat(status.isNetworkParseError()).isTrue();
        assertThat(status.toString()).contains("inconsistent");
        assertThat(status.toString()).contains("parsing");
    }

    @Test
    public void thatCanHandleAHaloStorageParseException(){
        HaloStatus status =  HaloStatus.builder().error(new HaloStorageParseException("Could no parse data from storage", new Exception())).build();
        assertThat(status.dataStatus()).isEqualTo(HaloStatus.STATUS_INCONSISTENT);
        assertThat(status.errorStatus()).isEqualTo(HaloStatus.STATUS_STORAGE_PARSE_ERROR);
        assertThat(status.isStorageParseError()).isTrue();
        assertThat(status.isInconsistent()).isTrue();
        assertThat(status.isStorageError()).isTrue();
        assertThat(status.toString()).contains("inconsistent");
        assertThat(status.toString()).contains("parsing");
    }

    @Test
    public void thatCanHandleAHaloIntegrationException(){
        HaloStatus status =  HaloStatus.builder().error(new HaloIntegrationException("Integration exception", new Exception())).build();
        assertThat(status.dataStatus()).isEqualTo(HaloStatus.STATUS_INCONSISTENT);
        assertThat(status.errorStatus()).isEqualTo(HaloStatus.STATUS_INTEGRATION_ERROR);
        assertThat(status.isIntegrationError()).isTrue();
        assertThat(status.isInconsistent()).isTrue();
        assertThat(status.toString()).contains("inconsistent");
        assertThat(status.toString()).contains("integration");
    }

    @Test
    public void thatCanSetCancelStatus(){
        HaloStatus status =  HaloStatus.builder().cancel().build();
        assertThat(status.dataStatus()).isEqualTo(HaloStatus.STATUS_INCONSISTENT);
        assertThat(status.errorStatus()).isEqualTo(HaloStatus.STATUS_CANCELED);
        assertThat(status.isCanceled()).isTrue();
        assertThat(status.isInconsistent()).isTrue();
        assertThat(status.toString()).contains("inconsistent");
        assertThat(status.toString()).contains("canceled");
    }

    @Test
    public void thatConvertHaloStatusToString(){
        HaloStatus status =  HaloStatus.builder().build();
        assertThat(status.isFresh()).isTrue();
        assertThat(status.toString()).contains("fresh");
    }

    @Test
    public void thatCanHaveLocalStatus(){
        HaloStatus status =  HaloStatus.builder().dataLocal().build();
        assertThat(status.isLocal()).isTrue();
        assertThat(status.toString()).contains("local");
    }
}

