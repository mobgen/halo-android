package com.mobgen.halo.android.content.mock.fixtures;

import com.mobgen.halo.android.testing.MockServer;

import java.io.IOException;

public class ServerFixtures {
    //Search
    public static final String SEARCH_PAGINATED_RESPONSE = "searchPaginatedItems.json";
    public static final String SEARCH_NOT_PAGINATED_RESPONSE = "searchNotPaginatedItems.json";

    //Sync
    public static final String SYNC_CREATE_MODULE = "syncCreateModule.json";
    public static final String SYNC_UPDATE_MODULE = "syncUpdateModule.json";
    public static final String SYNC_UP_TO_DATE = "syncUpToDate.json";

    //content edit
    public static final String CONTENT_EDIT_API = "contentManipulation.json";
    public static final String CONTENT_BATCH_API = "batchManipulation.json";

    public static void enqueueServerFile(MockServer server, String file) throws IOException {
        server.enqueueFile(200, file);
    }

    public static void enqueueServerError(MockServer server, int code) {
        server.enqueue(code);
    }
}
