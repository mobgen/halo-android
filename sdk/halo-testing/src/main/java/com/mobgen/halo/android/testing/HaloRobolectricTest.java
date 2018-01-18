package com.mobgen.halo.android.testing;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * The base test that contains the configuration for the tests.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 25)
public abstract class HaloRobolectricTest {

    @Before
    public void onStart() throws Exception{
        //Intended to be overridden
    }

    @After
    public void onDestroy() throws Exception{
        //Intended to be overridden.
    }

}
