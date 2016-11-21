package com.mobgen.halo.android.social.social.models;

import com.mobgen.halo.android.social.models.HaloUserProfile;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class HaloUserProfileTest extends HaloRobolectricTest {

    @Test
    public void thatCanCreateAHaloUserProfile(){
        HaloUserProfile haloUserProfile = new HaloUserProfile("identified","name surname","name","surname","http://photo.com","account@mobgen.com");
        assertThat(haloUserProfile).isNotNull();
        assertThat(haloUserProfile.getDisplayName()).isEqualTo("name surname");
        assertThat(haloUserProfile.getName()).isEqualTo("name");
        assertThat(haloUserProfile.getSurname()).isEqualTo("surname");
        assertThat(haloUserProfile.getPhoto()).isEqualTo("http://photo.com");
        assertThat(haloUserProfile.getEmail()).isEqualTo("account@mobgen.com");
    }

    @Test
    public void thatCheckParcelUserProfile() {
        HaloUserProfile haloUserProfile = new HaloUserProfile("identified","name surname","name","surname","http://photo.com","account@mobgen.com");
        HaloUserProfile newHaloUserProfile = TestUtils.testParcel(haloUserProfile, HaloUserProfile.CREATOR);
        assertThat(haloUserProfile.describeContents()).isEqualTo(0);
        assertThat(haloUserProfile.getDisplayName()).isEqualTo(newHaloUserProfile.getDisplayName());
        assertThat(haloUserProfile.getName()).isEqualTo(newHaloUserProfile.getName());
        assertThat(haloUserProfile.getSurname()).isEqualTo(newHaloUserProfile.getSurname());
        assertThat(haloUserProfile.getPhoto()).isEqualTo(newHaloUserProfile.getPhoto());
        assertThat(haloUserProfile.getEmail()).isEqualTo(newHaloUserProfile.getEmail());
    }

    @Test
    public void thatPrintContentToString() {
        HaloUserProfile haloUserProfile = new HaloUserProfile(null,"name surname","name","surname","http://photo.com","account@mobgen.com");
        assertThat(haloUserProfile.toString()).isNotNull();
    }

}
