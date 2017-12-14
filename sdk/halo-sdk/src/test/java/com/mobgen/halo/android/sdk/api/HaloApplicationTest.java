package com.mobgen.halo.android.sdk.api;


import com.mobgen.halo.android.framework.common.exceptions.HaloConfigurationException;
import com.mobgen.halo.android.sdk.BuildConfig;
import com.mobgen.halo.android.sdk.mock.HaloApplicationMock;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static com.mobgen.halo.android.sdk.mock.HaloMock.givenAHaloInstaller;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, application = HaloApplicationMock.class, sdk = 23)
public class HaloApplicationTest extends HaloRobolectricTest {

    private HaloApplicationMock mMockApplication;

    @Before
    public void initialize() {
        mMockApplication = (HaloApplicationMock) RuntimeEnvironment.application;
    }

    @After
    public void tearDown() {
        Halo.instance().uninstall();
    }

    @Test
    public void thatHaloInstallationIsCreated() {
        assertThat(Halo.instance().context()).isNotNull();
        assertThat(mMockApplication.isHaloCreated()).isTrue();
        assertThat(mMockApplication.isInstallerCreated()).isTrue();
    }

    @Test
    public void thatOnlyOneHaloInstanceIsInstalled() {

        try {
            givenAHaloInstaller().install();
        } catch (HaloConfigurationException e) {
            assertThat(Halo.isInitialized()).isTrue();
        }
    }
}
