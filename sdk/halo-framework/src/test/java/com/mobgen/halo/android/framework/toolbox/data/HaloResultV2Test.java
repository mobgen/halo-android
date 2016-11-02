package com.mobgen.halo.android.framework.toolbox.data;

import com.mobgen.halo.android.testing.HaloRobolectricTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloResultV2Test extends HaloRobolectricTest {

    @Before
    public void initialize() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void thatCanCreateHaloResult(){
        Integer number = new Integer(1);
        HaloStatus status = HaloStatus.builder().dataLocal().build();
        HaloResultV2<Integer> haloResultV2 = new HaloResultV2<Integer>(status,number);
        assertThat(haloResultV2.data()).isEqualTo(1);
        assertThat(haloResultV2.status().isOk()).isTrue();
    }

    @Test
    public void thatCanCreateHaloResultFromAnotherResult(){
        Integer number = new Integer(1);
        HaloStatus status = HaloStatus.builder().dataLocal().build();
        HaloResultV2<Integer> haloResultV2Wrapper = new HaloResultV2<Integer>(status,number);
        HaloResultV2<Integer> haloResultV2 = new HaloResultV2<Integer>(haloResultV2Wrapper);
        assertThat(haloResultV2.data()).isEqualTo(1);
        assertThat(haloResultV2.status().isOk()).isTrue();
    }

    @Test
    public void thatCanCreateHaloResultWithAStatusBuilderFromAnotherBuilder(){
        Integer number = new Integer(1);
        HaloStatus.Builder statuswrapper = HaloStatus.builder().dataLocal();
        HaloStatus status =  HaloStatus.builder(statuswrapper).dataLocal().build();
        HaloResultV2<Integer> haloResultV2 = new HaloResultV2<Integer>(status,number);
        assertThat(haloResultV2.data()).isEqualTo(1);
        assertThat(haloResultV2.status().isOk()).isTrue();
    }

    @Test
    public void thatCanCreateHaloResultWithAStatusBuilderFromAnotherHaloStatus(){
        Integer number = new Integer(1);
        HaloStatus statuswrapper = HaloStatus.builder().dataLocal().build();
        HaloStatus status =  HaloStatus.builder(statuswrapper).dataLocal().build();
        HaloResultV2<Integer> haloResultV2 = new HaloResultV2<Integer>(status,number);
        assertThat(haloResultV2.data()).isEqualTo(1);
        assertThat(haloResultV2.status().isOk()).isTrue();
    }

}

