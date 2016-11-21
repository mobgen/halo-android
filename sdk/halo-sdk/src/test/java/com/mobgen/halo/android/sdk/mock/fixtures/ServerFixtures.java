package com.mobgen.halo.android.sdk.mock.fixtures;

import com.mobgen.halo.android.testing.MockServer;

import java.io.IOException;

public class ServerFixtures {
    //authenticate
    public static final String AUTHENTICATE = "authenticateToken.json";
    //segmentation tags
    public static final String ADD_SEGMENTATION_TAG = "addSegmentationTag.json";
    public static final String REMOVE_SEGMENTATION_TAG = "removeSegmentationTag.json";
    //modules
    public static final String GET_MODULES = "getModules.json";
    //sync device
    public static final String SYNC_DEVICE = "deviceResponse.json";
    public static final String GET_DEVICE = "deviceResponse.json";
    public static final String SEND_DEVICE = "deviceResponse.json";
    //version
    public static final String TEST_SERVER_VERSION = "currentVersionServer.json";
    public static final String GET_VALID_SERVER_VERSION = "getValidServerVersion.json";
    public static final String GET_OUTDATED_SERVER_VERSION = "getOutdatedServerVersion.json";
    //token
    public static final String REQUEST_TOKEN = "getToken.json";
    //middleware request
    public static final String MIDDLEWARE_REQUEST = "middlewareRequest.json";


    public static void enqueueServerFile(MockServer server, String file) throws IOException {
        server.enqueueFile(200, file);
    }

    public static void enqueueServerError(MockServer server, int code) {
        server.enqueue(code);
    }
}
