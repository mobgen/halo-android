package com.mobgen.halo.android.social.social.models;

import com.mobgen.halo.android.social.models.HaloUserProfile;
import com.mobgen.halo.android.social.models.Token;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;


public class TokenTest extends HaloRobolectricTest {

    @Test
    public void thatCanCreateAToken(){
        Token token = new Token("accessToken","refreshToken",14L,"haloType");
        assertThat(token).isNotNull();
        assertThat(token.getAccessToken()).isEqualTo("accessToken");
        assertThat(token.getAuthorization()).isEqualTo("haloType accessToken");
        assertThat(token.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(token.getTokenType()).isEqualTo("haloType");
        assertThat(token.getReceivedDate()).isNotNull();
        assertThat(token.getExpiresIn()).isEqualTo(14L);
    }

    @Test
    public void thatCheckParcelToken() {
        Token token = new Token("accessToken","refreshToken",14L,"haloType");
        Token newToken = TestUtils.testParcel(token, Token.CREATOR);
        assertThat(token.getAccessToken()).isEqualTo(newToken.getAccessToken());
        assertThat(token.getAuthorization()).isEqualTo(newToken.getAuthorization());
        assertThat(token.getRefreshToken()).isEqualTo(newToken.getRefreshToken());
        assertThat(token.getTokenType()).isEqualTo(newToken.getTokenType());
        assertThat(token.getReceivedDate()).isNotNull();
        assertThat(token.getExpiresIn()).isEqualTo(newToken.getExpiresIn());
    }

    @Test
    public void thatPrintContentToString() {
        Token token = new Token("accessToken","refreshToken",14L,"haloType");
        assertThat(token.toString()).isNotNull();
    }

}
