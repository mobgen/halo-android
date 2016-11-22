package com.mobgen.halo.android.sdk.core.internal.storage;

import com.mobgen.halo.android.testing.HaloJUnitTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

public class HaloStorageContractTest extends HaloJUnitTest {

    @Test
    public void thatEnsureIsPrivate() throws Exception {
        TestUtils.testPrivateConstructor(HaloManagerContract.class);
    }
}
