package com.mobgen.halo.android.framework.mock.fixtures;

import com.mobgen.halo.android.testing.MockServer;

import java.io.IOException;

public class ServerFixtures {

    //test items
    public static final String GET_TEST_ITEM = "testItem.json";

    public static void enqueueServerFile(MockServer server, String file) throws IOException {
        server.enqueueFile(200, file);
    }

    public static void enqueueServerError(MockServer server, int code) {
        server.enqueue(code);
    }
}
