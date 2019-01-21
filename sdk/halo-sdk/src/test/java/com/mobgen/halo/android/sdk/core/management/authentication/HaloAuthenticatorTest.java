package com.mobgen.halo.android.sdk.core.management.authentication;

import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.network.sessions.HaloSessionManager;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;
import com.mobgen.halo.android.sdk.core.management.models.Session;
import com.mobgen.halo.android.sdk.core.management.models.Token;
import com.mobgen.halo.android.sdk.mock.HaloMock;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloGetRawShadow;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.annotation.Config;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;

import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.AUTHENTICATE;
import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.enqueueServerError;
import static com.mobgen.halo.android.sdk.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloAuthenticatorIntrument.givenA200Response;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloAuthenticatorIntrument.givenA401ClientTokenResponse;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloAuthenticatorIntrument.givenA401Response;
import static com.mobgen.halo.android.sdk.mock.instrumentation.HaloAuthenticatorIntrument.givenA401UserTokenResponse;
import static com.mobgen.halo.android.sdk.mock.instrumentation.TokenInstruments.givenACustomTypeToken;
import static com.mobgen.halo.android.sdk.mock.instrumentation.TokenInstruments.givenAExpiredToken;
import static com.mobgen.halo.android.sdk.mock.instrumentation.TokenInstruments.givenARefreshToken;
import static com.mobgen.halo.android.sdk.mock.instrumentation.TokenInstruments.givenAToken;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Config(shadows = {HaloGetRawShadow.class})
public class HaloAuthenticatorTest extends HaloRobolectricTest {

    private HaloSessionManager mSessionManager;
    private HaloFramework mFramework;
    private HaloAuthenticator mAuthenticator;
    private Halo mHalo;
    private MockServer mMockServer;
    private CallbackFlag mCallbackFlag;

    @Before
    public void initialize() throws IOException {
        mSessionManager = new HaloSessionManager();
        Credentials credentials = Credentials.createClient("clientId", "clientSecret");
        mMockServer = MockServer.create();
        mHalo = HaloMock.create(mMockServer.start());
        mFramework = mHalo.framework();
        mAuthenticator = new HaloAuthenticator(mFramework, mHalo.getCore().manager(), mSessionManager, credentials);
    }

    @After
    public void tearDown() throws IOException {
        mHalo.uninstall();
        mMockServer.shutdown();
    }

    @Test
    public void thatSessionIsExpired() {
        Token expiredToken = givenAExpiredToken();
        Session session = new Session(expiredToken);
        assertThat(session.isSessionExpired()).isTrue();
    }

    @Test
    public void thatExpiredTokenRefreshAfterARequest() throws IOException {
        enqueueServerFile(mMockServer, AUTHENTICATE);
        Response response = givenA200Response();
        Token expiredToken = givenAExpiredToken();
        Session session = new Session(expiredToken);
        mSessionManager.setSession(HaloAuthenticator.HALO_SESSION_NAME, session);
        Request newRequest = mAuthenticator.authenticate(null, response);
        assertThat(newRequest).isNotNull();
        assertThat(newRequest.header("Authorization")).contains("Bearer LSD4fw4NLBTESfF18tTVEST6q9vsaLW8tMfEGLJa");
    }

    @Test
    public void thatARefreshTokenRenewSession() throws IOException {
        Response response = givenA200Response();
        Token token = givenARefreshToken();
        Session newSession = new Session(token);
        mSessionManager.setSession(HaloAuthenticator.HALO_SESSION_NAME, newSession);
        Request newRequest = mAuthenticator.authenticate(null, response);
        assertThat(newRequest).isNotNull();
        assertThat(newRequest.header("Authorization")).isEqualTo(token.getAuthorization());
    }

    @Test
    public void thatCredentialsExist() {
        assertThat(mAuthenticator.getCredentials()).isNotNull();
    }

    @Test
    public void thatRenewTokenIsCalledIfSessionIsNotSet() throws IOException {
        enqueueServerError(mMockServer, 401);
        enqueueServerFile(mMockServer, AUTHENTICATE);
        Response response = givenA401Response();
        Request newRequest = mAuthenticator.authenticate(null, response);
        assertThat(newRequest).isNull();
        assertThat(mAuthenticator.getCredentials()).isNotNull();
    }

    @Test
    public void thatCanChangeCredentials() {
        Credentials credentials = Credentials.createClient("newClient", "newSecret");
        mAuthenticator.setCredentials(credentials);
        assertThat(mAuthenticator.getCredentials()).isNotNull();
        assertThat(mAuthenticator.getCredentials().getUsername()).isEqualTo("newClient");
        assertThat(mAuthenticator.getCredentials().getPassword()).isEqualTo("newSecret");
    }

    @Test
    public void thatCanSetANewSession() {
        mSessionManager.setSession(HaloAuthenticator.HALO_SESSION_NAME, new Session(givenAToken()));
        assertThat(mSessionManager.getSession(HaloAuthenticator.HALO_SESSION_NAME)).isNotNull();
    }

    @Test
    public void thatCanFlushASession() {
        mSessionManager.setSession(HaloAuthenticator.HALO_SESSION_NAME, new Session(givenAToken()));
        mAuthenticator.flushSession();
        assertThat(mSessionManager.getSession(HaloAuthenticator.HALO_SESSION_NAME)).isNull();
    }

    @Test
    public void thatSessionHeaderInterceptionOccurs() throws IOException {
        Session session = new Session(givenACustomTypeToken());
        final String authenticationHeader = session.getSessionAuthentication();
        mSessionManager.setSession(HaloAuthenticator.HALO_SESSION_NAME, session);
        Interceptor interceptor = mFramework.network().client().ok().interceptors().get(0);
        Interceptor.Chain chain = mock(Interceptor.Chain.class);
        when(chain.request()).thenReturn(new Request.Builder().url("http://google.com").build());
        when(chain.proceed(any(Request.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                assertThat(((Request) invocation.getArguments()[0]).headers("Authorization").get(0)).isEqualTo(authenticationHeader);
                return null;
            }
        });
        interceptor.intercept(chain);
    }

    @Test
    public void thatSessionHeaderInterceptsNoSession() throws IOException {
        mSessionManager.flushSession(HaloAuthenticator.HALO_SESSION_NAME);
        Interceptor.Chain chain = mock(Interceptor.Chain.class);
        Interceptor interceptor = mFramework.network().client().ok().interceptors().get(0);
        when(chain.request()).thenReturn(new Request.Builder().url("http://google.com").build());
        when(chain.proceed(any(Request.class))).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                assertThat(((Request) invocation.getArguments()[0]).headers("Authorization").isEmpty()).isTrue();
                return null;
            }
        });
        interceptor.intercept(chain);
    }

    @Test
    public void thatCanAuthenticateNewTokenSessionWhenRefreshToken() throws IOException {
        Token token = givenARefreshToken();
        Response response = givenA200Response();
        Session session = new Session(token);
        mSessionManager.setSession(HaloAuthenticator.HALO_SESSION_NAME, session);
        Request newRequest = mAuthenticator.authenticate(null, response);
        assertThat(newRequest).isNotNull();
        assertThat(newRequest.header("Authorization")).isEqualTo(token.getAuthorization());
    }

    @Test
    public void thatCanAuthenticateANewTokenAndSessionIfSessionIsExpired() throws IOException {
        enqueueServerError(mMockServer, 401);
        Token token = givenAToken();
        Session newSession = new Session(token);
        Response response = givenA200Response();
        mSessionManager.setSession(HaloAuthenticator.HALO_SESSION_NAME, newSession);
        Request newRequest = mAuthenticator.authenticate(null, response);
        assertThat(newRequest).isNotNull();
        assertThat(newRequest.header("Authorization")).isEqualTo(token.getAuthorization());
    }

    @Test
    public void thatRenewTokenIsNotCalledToAvoidLoopWithClientToken() throws IOException {
        enqueueServerError(mMockServer, 401);
        Response response = givenA401ClientTokenResponse();
        Request newRequest = mAuthenticator.authenticate(null, response);
        assertThat(newRequest).isNull();
    }

    @Test
    public void thatRenewTokenIsNotCalledToAvoidLoopWithUserToken() throws IOException {
        enqueueServerError(mMockServer, 401);
        Response response = givenA401UserTokenResponse();
        Request newRequest = mAuthenticator.authenticate(null, response);
        assertThat(newRequest).isNull();
    }

    @Test(expected = NullPointerException.class)
    public void thatCannotAuthenticateWithNoCredentials() throws IOException {
        mAuthenticator.setCredentials(null);
        Request newRequest = mAuthenticator.authenticate(null, new Response.Builder()
                .request(new Request.Builder().url("http://google.com").build())
                .protocol(Protocol.HTTP_1_1)
                .code(200).build());
        assertThat(newRequest).isNull();
        assertThat(mAuthenticator.getCredentials()).isNull();
    }
}
