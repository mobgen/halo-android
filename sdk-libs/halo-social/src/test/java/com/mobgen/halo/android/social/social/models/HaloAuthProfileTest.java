package com.mobgen.halo.android.social.social.models;

import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class HaloAuthProfileTest extends HaloRobolectricTest {

    @Test
    public void thatCanCreateAHaloAuthProfile(){
        HaloAuthProfile haloAuthProfile = new HaloAuthProfile("account@mobgen.com","myverysecurepass","alias-device");
        assertThat(haloAuthProfile).isNotNull();
        assertThat(haloAuthProfile.getPassword()).isEqualTo("myverysecurepass");
        assertThat(haloAuthProfile.getAlias()).isEqualTo("alias-device");
        assertThat(haloAuthProfile.getEmail()).isEqualTo("account@mobgen.com");
    }

    @Test
    public void thatCheckParcelAuthProfile() {
        HaloAuthProfile haloAuthProfile = new HaloAuthProfile("account@mobgen.com","myverysecurepass","alias-device");
        HaloAuthProfile newhaloAuthProfile = TestUtils.testParcel(haloAuthProfile, HaloAuthProfile.CREATOR);
        assertThat(haloAuthProfile.describeContents()).isEqualTo(0);
        assertThat(haloAuthProfile.getAlias()).isEqualTo(newhaloAuthProfile.getAlias());
        assertThat(haloAuthProfile.getPassword()).isEqualTo(newhaloAuthProfile.getPassword());
        assertThat(haloAuthProfile.getEmail()).isEqualTo(newhaloAuthProfile.getEmail());
    }

    @Test
    public void thatPrintContentToString() {
        HaloAuthProfile haloAuthProfile = new HaloAuthProfile("account@mobgen.com","myverysecurepass","alias-device");
        assertThat(haloAuthProfile.toString()).isNotNull();
    }

}
