package com.mobgen.halo.android.testing;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

/**
 * Base runner for junit tests
 */
@RunWith(BlockJUnit4ClassRunner.class)
public class HaloJUnitTest {

    @Before
    public void onStart() throws Exception{
        //Intended to be overridden
    }

    @After
    public void onDestroy() throws Exception{
        //Intended to be overridden.
    }
}
