package com.mobgen.halo.android.sdk.core.management.models;


import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloModuleFieldTest extends HaloRobolectricTest {
    HaloModuleField mHaloModuleField;

    @Before
    public void initialize() {
        HaloModuleFieldType moduleFieldType = new HaloModuleFieldType("myawesomeName", new ArrayList<HaloModuleFieldRule>(), false, new Date(), new Date(), new Date(), "myid");
        mHaloModuleField = new HaloModuleField(moduleFieldType, "awesome module", "awesome field", "format" , "description", 1034, new Date(), new Date(), new Date(), "myid");
    }

    @Test
    public void thatCanCreateAModuleField(){
        assertThat(mHaloModuleField).isNotNull();
        assertThat(mHaloModuleField.getName()).isEqualTo("awesome field");
    }

    @Test
    public void thatCheckParcelModuleField() {
        HaloModuleField newHaloModuleField = TestUtils.testParcel(mHaloModuleField, HaloModuleField.CREATOR);
        assertThat(mHaloModuleField.describeContents()).isEqualTo(0);
        assertThat(mHaloModuleField.getName()).isEqualTo(newHaloModuleField.getName());
    }

    @Test
    public void thatPrintObjectToString() {
        HaloModuleField newHaloModuleField = TestUtils.testParcel(mHaloModuleField, HaloModuleField.CREATOR);
        assertThat(mHaloModuleField.toString()).isNotNull();
    }
}
