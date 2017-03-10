package com.mobgen.halo.android.sdk.core.management.models;


import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloModuleFieldRuleTest extends HaloRobolectricTest {

    HaloModuleFieldRule mHaloModuleRule;

    @Before
    public void initialize() {
        mHaloModuleRule = new HaloModuleFieldRule("rule1",new ArrayList<String>(),"error");
    }

    @Test
    public void thatCanCreateAModuleField(){
        assertThat(mHaloModuleRule).isNotNull();
        assertThat(mHaloModuleRule.getRule()).isEqualTo("rule1");
    }

    @Test
    public void thatCheckParcelModuleField() {
        HaloModuleFieldRule newHaloModuleRule = TestUtils.testParcel(mHaloModuleRule, HaloModuleFieldRule.CREATOR);
        assertThat(mHaloModuleRule.describeContents()).isEqualTo(0);
        assertThat(mHaloModuleRule.getRule()).isEqualTo(newHaloModuleRule.getRule());
    }
    @Test
    public void thatPrintObjectToString() {
        HaloModuleFieldRule newHaloModuleRule = TestUtils.testParcel(mHaloModuleRule, HaloModuleFieldRule.CREATOR);
        assertThat(mHaloModuleRule.toString()).isNotNull();
    }


}
