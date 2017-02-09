package com.mobgen.halo.android.sdk.core.management.models;


import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloModuleFieldTypeTest extends HaloRobolectricTest {
    HaloModuleFieldType mHaloModuleFieldType;

    @Before
    public void initialize() {
        mHaloModuleFieldType = new HaloModuleFieldType("myawesomeName", null, false, new Date(), new Date(), new Date(), "myid");
    }

    @Test
    public void thatCanCreateAModuleField(){
        assertThat(mHaloModuleFieldType).isNotNull();
        assertThat(mHaloModuleFieldType.getName()).isEqualTo("myawesomeName");
    }

    @Test
    public void thatCheckParcelModuleField() {
        HaloModuleFieldType newHaloModuleFieldType = TestUtils.testParcel(mHaloModuleFieldType, HaloModuleFieldType.CREATOR);
        assertThat(mHaloModuleFieldType.describeContents()).isEqualTo(0);
        assertThat(mHaloModuleFieldType.getName()).isEqualTo(newHaloModuleFieldType.getName());
    }
}
