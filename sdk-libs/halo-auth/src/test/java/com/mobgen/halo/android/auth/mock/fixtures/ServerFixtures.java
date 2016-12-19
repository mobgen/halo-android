package com.mobgen.halo.android.auth.mock.fixtures;

import com.mobgen.halo.android.testing.MockServer;

import java.io.IOException;

public class ServerFixtures {

    //login
    public static final String LOGIN_SUCESS = "login.json";
    //register
    public static final String REGISTER_SUCESS = "register.json";

    public static void enqueueServerFile(MockServer server, String file) throws IOException {
        server.enqueueFile(200, file);
    }

    public static void enqueueServerError(MockServer server, int code) {
        server.enqueue(code);
    }
}
