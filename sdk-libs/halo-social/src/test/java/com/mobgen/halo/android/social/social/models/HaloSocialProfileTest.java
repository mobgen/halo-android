//package com.mobgen.halo.android.social.social.models;
//
//import com.mobgen.halo.android.social.models.HaloSocialProfile;
//import com.mobgen.halo.android.testing.HaloRobolectricTest;
//import com.mobgen.halo.android.testing.TestUtils;
//
//import org.junit.Test;
//
//
//import static org.assertj.core.api.Java6Assertions.assertThat;
//
//
//public class HaloSocialProfileTest extends HaloRobolectricTest {
//
//    @Test
//    public void thatCanCreateAHaloSocialProfile(){
//        HaloSocialProfile haloSocialProfile = HaloSocialProfile.builder("halo-social-token")
//                .socialId("halo")
//                .socialName("halo")
//                .displayName("name surname")
//                .email("account@mobgen.com")
//                .name("name")
//                .photo("http://urltophoto.com")
//                .surname("surname")
//                .build();
//        assertThat(haloSocialProfile).isNotNull();
//        assertThat(haloSocialProfile.socialName()).isEqualTo("halo");
//        assertThat(haloSocialProfile.surname()).isEqualTo("surname");
//        assertThat(haloSocialProfile.email()).isEqualTo("account@mobgen.com");
//        assertThat(haloSocialProfile.socialId()).isEqualTo("halo");
//        assertThat(haloSocialProfile.displayName()).isEqualTo("name surname");
//        assertThat(haloSocialProfile.name()).isEqualTo("name");
//        assertThat(haloSocialProfile.photo()).isEqualTo("http://urltophoto.com");
//        assertThat(haloSocialProfile.socialToken()).isEqualTo("halo-social-token");
//    }
//
//    @Test
//    public void thatCheckParcelSocialProfile() {
//        HaloSocialProfile haloSocialProfile = HaloSocialProfile.builder("halo-social-token")
//                .socialId("halo")
//                .socialName("halo")
//                .displayName("name surname")
//                .email("account@mobgen.com")
//                .name("name")
//                .photo("http://urltophoto.com")
//                .surname("surname")
//                .build();
//        HaloSocialProfile newHaloSocialProfile = TestUtils.testParcel(haloSocialProfile, HaloSocialProfile.CREATOR);
//        assertThat(haloSocialProfile.describeContents()).isEqualTo(0);
//        assertThat(newHaloSocialProfile.email()).isEqualTo(haloSocialProfile.email());
//        assertThat(newHaloSocialProfile.name()).isEqualTo(haloSocialProfile.name());
//        assertThat(newHaloSocialProfile.socialId()).isEqualTo(haloSocialProfile.socialId());
//        assertThat(newHaloSocialProfile.photo()).isEqualTo(haloSocialProfile.photo());
//        assertThat(newHaloSocialProfile.surname()).isEqualTo(haloSocialProfile.surname());
//    }
//
//    @Test
//    public void thatCanSetTheSocialProviderName() {
//        HaloSocialProfile haloSocialProfile = HaloSocialProfile.builder("halo")
//                .socialId("halo")
//                .socialName("halo")
//                .displayName("name surname")
//                .email("account@mobgen.com")
//                .name("name")
//                .photo("http://urltophoto.com")
//                .surname("surname")
//                .build();
//        haloSocialProfile.setSocialName("haloSocial");
//        assertThat(haloSocialProfile.socialName()).isEqualTo("haloSocial");
//    }
//
//    @Test
//    public void thatPrintContentToString() {
//        HaloSocialProfile haloSocialProfile = HaloSocialProfile.builder("halo")
//                .socialId("halo")
//                .socialName("halo")
//                .displayName("name surname")
//                .email("account@mobgen.com")
//                .name("name")
//                .photo("http://urltophoto.com")
//                .surname("surname")
//                .build();
//        assertThat(haloSocialProfile.toString()).isNotNull();
//    }
//
//}
