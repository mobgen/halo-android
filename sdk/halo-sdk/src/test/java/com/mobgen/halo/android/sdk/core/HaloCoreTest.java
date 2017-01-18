package com.mobgen.halo.android.sdk.core;

import com.mobgen.halo.android.sdk.BuildConfig;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.authentication.HaloAuthenticator;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.sdk.core.management.models.Session;
import com.mobgen.halo.android.sdk.core.management.models.Token;
import com.mobgen.halo.android.sdk.core.management.segmentation.DeviceManufacturerCollector;
import com.mobgen.halo.android.sdk.core.management.segmentation.DeviceModelCollector;
import com.mobgen.halo.android.sdk.core.management.segmentation.TagCollector;
import com.mobgen.halo.android.sdk.mock.HaloMock;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class HaloCoreTest extends HaloRobolectricTest {

    private Halo mHalo;

    @Before
    public void initialize() {
        mHalo = HaloMock.create();
    }

    @After
    public void tearDown() {
        mHalo.uninstall();
    }


    @Test
    public void thatCanConfigureCredentials() {
        Credentials credentials = Credentials.createClient("myClientId", "myClientSecret");
        HaloCore core = HaloMock.createCore(credentials);
        assertThat(core.credentials()).isNotNull();
        assertThat(core.credentials().getUsername()).isEqualTo("myClientId");
        assertThat(core.credentials().getPassword()).isEqualTo("myClientSecret");
    }

    @Test
    public void thatConfigureCredentialsInDebugMode() {
        mHalo.framework().setDebugFlag(true);
        HaloCore core = HaloMock.createCore(mHalo.framework(), Credentials.createClient("myClientIdDebug", "myClientSecretDebug"));
        assertThat(core.credentials()).isNotNull();
        assertThat(core.credentials().getUsername()).isEqualTo("myClientIdDebug");
        assertThat(core.credentials().getPassword()).isEqualTo("myClientSecretDebug");
    }

    @Test
    public void thatVersionIsCorrect() {
        HaloCore core = HaloMock.createCore(Credentials.createClient("myClient", "myPass"), null);
        assertThat(core.version()).isEqualTo(BuildConfig.HALO_SDK_VERSION);
    }

    @Test
    public void thatDebugModeIsEnabled() {
        HaloCore core = HaloMock.createCore(mHalo.framework(), Credentials.createClient("myClient", "myPass"));
        core.framework().setDebugFlag(true);
        assertThat(core.debug()).isTrue();
    }

    @Test(expected = Exception.class)
    public void thatCanCreateWithNullCredentials() {
        HaloCore core = HaloMock.createCore(mHalo.framework(), null);
        assertThat(core.credentials()).isNotNull();
    }

    @Test
    public void thatCanCreateNewCredentials() {
        Credentials credentials = Credentials.createClient("myClient", "myPass");
        HaloCore core = HaloMock.createCore(credentials, null);
        core.credentials(Credentials.createClient("newClient", "newSecret"));
        assertThat(core.credentials()).isNotEqualTo(credentials);
        assertThat(core.credentials());
        assertThat(credentials.getUsername().equals(core.credentials().getUsername())).isFalse();
        assertThat(credentials.getPassword().equals(core.credentials().getPassword())).isFalse();
        assertThat(core.credentials().getUsername()).isEqualTo("newClient");
        assertThat(core.credentials().getPassword()).isEqualTo("newSecret");
    }

    @Test
    public void thatCanFlushSession() {
        HaloCore core = HaloMock.createCore(Credentials.createClient("myClient", "myPass"), null);
        core.sessionManager().setSession(HaloAuthenticator.HALO_SESSION_NAME, new Session(new Token("token", "refresh", 1L, "type")));
        core.flushSession();
        assertThat(core.sessionManager().getSession(HaloAuthenticator.HALO_SESSION_NAME)).isNull();
    }

    @Test
    public void thatCanLogout() {
        HaloCore core = HaloMock.createCore(Credentials.createClient("myClient", "myPass"), null);
        core.sessionManager().setSession(HaloAuthenticator.HALO_SESSION_NAME, new Session(new Token("token", "refresh", 1L, "type")));
        core.logout();
        assertThat(core.sessionManager().getSession(HaloAuthenticator.HALO_SESSION_NAME)).isNull();
    }

    @Test
    public void thatDeviceIsNullIfNotCreated() {
        HaloCore core = HaloMock.createCore(Credentials.createClient("myClient", "myPass"), null);
        core.sessionManager().setSession(HaloAuthenticator.HALO_SESSION_NAME, new Session(new Token("token", "refresh", 1L, "type")));
        Device device = mHalo.manager().getDevice();
        assertThat(device).isNull();
    }

    public void thatCanGetTagCollectors() {
        HaloCore core = HaloMock.createCore(Credentials.createClient("myClient", "myPass"), null);
        assertThat(core.tagCollectors().size()).isEqualTo(0);
    }

    @Test
    public void thatSegmentationTagsCollectorsExists() {
        List<TagCollector> collectors = new ArrayList<>();
        collectors.add(new DeviceModelCollector());
        collectors.add(new DeviceManufacturerCollector());
        HaloCore core = HaloMock.createCore(Credentials.createClient("myClient", "myPass"), collectors);
        assertThat(core.tagCollectors().size()).isEqualTo(2);
    }

    @Test
    public void thatSegmentationTagsAreEmpty() {
        HaloCore core = HaloMock.createCore(Credentials.createClient("myClient", "myPass"), new ArrayList<TagCollector>());
        assertThat(core.segmentationTags().size()).isEqualTo(0);
    }

    @Test
    public void thatSegmentationTagsExists() {
        List<TagCollector> collectors = new ArrayList<>();
        collectors.add(new DeviceModelCollector());
        collectors.add(new DeviceManufacturerCollector());
        HaloCore core = HaloMock.createCore(Credentials.createClient("myClient", "myPass"), collectors);
        assertThat(core.segmentationTags().size()).isEqualTo(2);
    }

    @Test
    public void thatCanCreateCore() {
        HaloCore core = HaloMock.createCore(Credentials.createClient("myClient", "myPass"), null);
        assertThat(core.framework()).isNotNull();
    }
}
