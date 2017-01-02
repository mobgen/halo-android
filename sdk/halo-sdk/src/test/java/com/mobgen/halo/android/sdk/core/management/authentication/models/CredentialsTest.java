package com.mobgen.halo.android.sdk.core.management.authentication.models;

import com.mobgen.halo.android.sdk.core.management.models.Credentials;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class CredentialsTest extends HaloRobolectricTest {

    @Test
    public void thatConstructorIsOk() {
        Credentials credentials = Credentials.createClient("device", "password");
        assertThat(credentials.getUsername()).isEqualTo("device");
        assertThat(credentials.getPassword()).isEqualTo("password");
    }

    @Test
    public void thatParcelableCreatorIsOk() {
        Credentials credentials = Credentials.createClient("clientId", "clientSecret");
        Credentials parcelableCredentials = TestUtils.testParcel(credentials, Credentials.CREATOR);
        assertThat(parcelableCredentials.getUsername()).isEqualTo(credentials.getUsername());
        assertThat(parcelableCredentials.getPassword()).isEqualTo(credentials.getPassword());
        assertThat(parcelableCredentials.getLoginType()).isEqualTo(credentials.getLoginType());
        assertThat(credentials.describeContents()).isEqualTo(0);
    }

    @Test
    public void thatLoginClientType() {
        assertThat(Credentials.createClient("clientId", "clientSecret").getLoginType()).isEqualTo(Credentials.CLIENT_BASED_LOGIN);
    }

    @Test
    public void thatLoginUserType() {
        assertThat(Credentials.createUser("username", "password").getLoginType()).isEqualTo(Credentials.USER_BASED_LOGIN);
    }
}
