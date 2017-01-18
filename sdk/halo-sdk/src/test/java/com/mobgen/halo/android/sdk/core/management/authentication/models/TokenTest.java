package com.mobgen.halo.android.sdk.core.management.authentication.models;

import com.mobgen.halo.android.sdk.core.management.models.Token;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.TestUtils;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class TokenTest extends HaloRobolectricTest {

    @Test
    public void thatCreateWithEmptyConstructor() {
        Token token = new Token();
        assertThat(token.getAccessToken()).isNull();
        assertThat(token.getExpiresIn()).isNull();
        assertThat(token.getTokenType()).isNull();
        assertThat(token.getRefreshToken()).isNull();
    }

    @Test
    public void thatConstructorIsOk() {
        Token token = new Token("token", "refresh", 1000L, "bearer");
        assertThat(token.getAccessToken()).isEqualTo("token");
        assertThat(token.getRefreshToken()).isEqualTo("refresh");
        assertThat((long) token.getExpiresIn()).isEqualTo(1000L);
        assertThat(token.getTokenType()).isEqualTo("bearer");
        assertThat(token.getAuthorization()).isEqualTo("bearer token");
    }

    @Test
    public void thatParcelIsOk() {
        Token token = new Token("token", "refresh", 1000L, "bearer");
        Token newToken = TestUtils.testParcel(token, Token.CREATOR);
        assertThat(newToken.getAccessToken()).isEqualTo(token.getAccessToken());
        assertThat(newToken.getRefreshToken()).isEqualTo(token.getRefreshToken());
        assertThat((long) newToken.getExpiresIn()).isEqualTo((long) token.getExpiresIn());
        assertThat(newToken.getTokenType()).isEqualTo(token.getTokenType());
        assertThat(newToken.getReceivedDate()).isEqualTo(token.getReceivedDate());
        assertThat(newToken.getAuthorization()).isEqualTo(token.getAuthorization());
        assertThat(token.describeContents()).isEqualTo(0);
        assertThat(Token.CREATOR.newArray(1).length).isEqualTo(1);
    }
}
