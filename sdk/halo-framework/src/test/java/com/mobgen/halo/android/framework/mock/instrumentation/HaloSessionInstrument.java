package com.mobgen.halo.android.framework.mock.instrumentation;

import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.network.sessions.HaloSession;

import java.util.Date;
import java.util.concurrent.TimeUnit;


import android.support.annotation.NonNull;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.network.sessions.HaloSession;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Session that contains the device information related to the authentication. If there is a session
 * it means there is a token active, once this token starts giving authentication errors it means
 * the session is not valid anymore and a new authentication request must be done.
 *
 */
public class HaloSessionInstrument extends HaloSession{


        @NonNull
        public String getSessionAuthentication() {
            return "SessionAuthenticationToken";
        }

        @Override
        public boolean isSessionExpired() {
            return false;
        }

        @Override
        public boolean mayBeServerExpired() {
            return false;
        }

        @NonNull
        @Override
        public String getRefreshToken() {
            return "RefreshAuthToken";
        }

}
