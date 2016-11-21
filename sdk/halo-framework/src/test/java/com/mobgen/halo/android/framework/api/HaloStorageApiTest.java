package com.mobgen.halo.android.framework.api;

import com.mobgen.halo.android.framework.mock.FrameworkMock;
import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static com.mobgen.halo.android.framework.mock.instrumentation.HaloFrameworkInstrument.givenAStorageConfig;
import static org.assertj.core.api.Java6Assertions.assertThat;


public class HaloStorageApiTest extends HaloRobolectricTest {

    private HaloFramework mFramework;
    private HaloStorageApi mStorage;

    @Before
    public void initialize() {
        mFramework = FrameworkMock.createSameThreadFramework("myTestEndpoint");
        mStorage =  mFramework.createStorage(givenAStorageConfig());
    }

    @After
    public void tearDown() {

    }

    @Test
    public void thatCanGetPreferences(){
        assertThat(mStorage.prefs()).isNotNull();
    }

    @Test
    public void thatCanGetcontext(){
        assertThat(mStorage.context()).isNotNull();
    }

    @Test
    public void thatGetFramework(){
        assertThat(mStorage.framework()).isEqualTo(mFramework);
    }
}