package com.mobgen.halo.android.notifications.fixtures;

import com.mobgen.halo.android.testing.MockServer;

import java.io.IOException;

public class ServerFixtures {

    //notification open
    public static final String OPEN = "actionNotificationOpen.json";
    //notification dismiss
    public static final String DISMISS = "actionNotificationDismiss.json";
    //notification receipt
    public static final String RECEIPT = "actionNotificationReceipt.json";

    public static void enqueueServerFile(MockServer server, String file) throws IOException {
        server.enqueueFile(200, file);
    }

    public static void enqueueServerError(MockServer server, int code) {
        server.enqueue(code);
    }
}
