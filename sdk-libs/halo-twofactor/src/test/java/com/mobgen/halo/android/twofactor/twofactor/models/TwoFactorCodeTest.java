package com.mobgen.halo.android.twofactor.twofactor.models;

import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;
import com.mobgen.halo.android.twofactor.models.TwoFactorCode;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class TwoFactorCodeTest extends HaloRobolectricTest {

    @Test
    public void thatCanCreateARegister(){
        TwoFactorCode twoFactorCode = new TwoFactorCode("159753","FROM_SMS");
        assertThat(twoFactorCode).isNotNull();
        assertThat(twoFactorCode.getCode()).isEqualTo("159753");
        assertThat(twoFactorCode.getIssuer()).isEqualTo("FROM_SMS");
    }

    @Test
    public void thatCheckParcelRegister() {
        TwoFactorCode twoFactorCode = new TwoFactorCode("159753","FROM_SMS");
        TwoFactorCode newFactorCode = TestUtils.testParcel(twoFactorCode, TwoFactorCode.CREATOR);
        assertThat(twoFactorCode.describeContents()).isEqualTo(0);
        assertThat(newFactorCode.getCode()).isEqualTo(twoFactorCode.getCode());
        assertThat(newFactorCode.getIssuer()).isEqualTo(twoFactorCode.getIssuer());
    }

    @Test
    public void thatPrintContentToString() {
        TwoFactorCode twoFactorCode = new TwoFactorCode("159753","FROM_SMS");
        assertThat(twoFactorCode.toString()).isNotNull();
    }

}
