package com.mobgen.halo.android.social.social.models;

import com.mobgen.halo.android.social.models.HaloSocialProfile;
import com.mobgen.halo.android.social.models.HaloUserProfile;
import com.mobgen.halo.android.social.models.IdentifiedUser;
import com.mobgen.halo.android.social.models.Token;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class IdentifiedUserTest extends HaloRobolectricTest {

    @Test
    public void thatCanCreateAIdentifiedUser(){
        HaloUserProfile haloUserProfile = new HaloUserProfile("identified","name surname","name","surname","http://photo.com","account@mobgen.com");
        Token token = new Token("accessToken","refreshToken",14L,"haloType");
        IdentifiedUser identifiedUser = IdentifiedUser.builder(token,haloUserProfile)
                .build();
        assertThat(identifiedUser).isNotNull();
        assertThat(identifiedUser.getToken().getAccessToken()).isNotNull();
        assertThat(identifiedUser.getUser().getEmail()).isEqualTo("account@mobgen.com");
    }

    @Test
    public void thatCheckParcelIdentifiedUser() {
        HaloUserProfile haloUserProfile = new HaloUserProfile("identified","name surname","name","surname","http://photo.com","account@mobgen.com");
        Token token = new Token("accessToken","refreshToken",14L,"haloType");
        IdentifiedUser identifiedUser = IdentifiedUser.builder(token,haloUserProfile)
                .build();
        IdentifiedUser newIdentifiedUser = TestUtils.testParcel(identifiedUser, IdentifiedUser.CREATOR);
        assertThat(identifiedUser.describeContents()).isEqualTo(0);
        assertThat(identifiedUser.getToken().getAccessToken()).isEqualTo(newIdentifiedUser.getToken().getAccessToken());
        assertThat(identifiedUser.getUser().getEmail()).isEqualTo(newIdentifiedUser.getUser().getEmail());
    }

    @Test
    public void thatPrintContentToString() {
        HaloUserProfile haloUserProfile = new HaloUserProfile("identified","name surname","name","surname","http://photo.com","account@mobgen.com");
        Token token = new Token("accessToken","refreshToken",14L,"haloType");
        IdentifiedUser identifiedUser = new IdentifiedUser(token,haloUserProfile);
        assertThat(identifiedUser.toString()).isNotNull();
    }

}
