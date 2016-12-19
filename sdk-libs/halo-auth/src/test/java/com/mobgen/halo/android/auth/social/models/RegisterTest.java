package com.mobgen.halo.android.auth.social.models;

import com.mobgen.halo.android.auth.models.HaloAuthProfile;
import com.mobgen.halo.android.auth.models.HaloUserProfile;
import com.mobgen.halo.android.auth.models.Register;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class RegisterTest extends HaloRobolectricTest {

    @Test
    public void thatCanCreateARegister(){
        HaloAuthProfile haloAuthProfile = new HaloAuthProfile("account@mobgen.com","myverysecurepass","alias-device");
        HaloUserProfile haloUserProfile = new HaloUserProfile("identified","name surname","name","surname","http://photo.com","account@mobgen.com");
        Register register = new Register(haloAuthProfile,haloUserProfile);
        assertThat(register).isNotNull();
        assertThat(register.getHaloAuthProfile()).isEqualTo(haloAuthProfile);
        assertThat(register.getUserProfile()).isEqualTo(haloUserProfile);
    }

    @Test
    public void thatCanCreateARegisterWithBuilder(){
        HaloAuthProfile haloAuthProfile = new HaloAuthProfile("account@mobgen.com","myverysecurepass","alias-device");
        HaloUserProfile haloUserProfile = new HaloUserProfile("identified","name surname","name","surname","http://photo.com","account@mobgen.com");
        Register.Builder builder = Register.builder(haloAuthProfile,haloUserProfile);
        Register register = builder.build();
        assertThat(register).isNotNull();
        assertThat(register.getHaloAuthProfile()).isEqualTo(haloAuthProfile);
        assertThat(register.getUserProfile()).isEqualTo(haloUserProfile);
    }

    @Test
    public void thatCheckParcelRegister() {
        HaloAuthProfile haloAuthProfile = new HaloAuthProfile("account@mobgen.com","myverysecurepass","alias-device");
        HaloUserProfile haloUserProfile = new HaloUserProfile("identified","name surname","name","surname","http://photo.com","account@mobgen.com");
        Register register = new Register(haloAuthProfile,haloUserProfile);
        Register newRegister = TestUtils.testParcel(register, Register.CREATOR);
        assertThat(register.describeContents()).isEqualTo(0);
        assertThat(newRegister.getHaloAuthProfile().getAlias()).isEqualTo(register.getHaloAuthProfile().getAlias());
        assertThat(newRegister.getUserProfile().getDisplayName()).isEqualTo(register.getUserProfile().getDisplayName());
    }

    @Test
    public void thatPrintContentToString() {
        HaloAuthProfile haloAuthProfile = new HaloAuthProfile("account@mobgen.com","myverysecurepass","alias-device");
        HaloUserProfile haloUserProfile = new HaloUserProfile("identified","name surname","name","surname","http://photo.com","account@mobgen.com");
        Register register = new Register(haloAuthProfile,haloUserProfile);
        assertThat(register.toString()).isNotNull();
    }

}
