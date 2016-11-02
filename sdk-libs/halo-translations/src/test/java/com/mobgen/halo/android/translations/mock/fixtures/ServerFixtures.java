package com.mobgen.halo.android.translations.mock.fixtures;

import com.mobgen.halo.android.testing.MockServer;

import java.io.IOException;

public class ServerFixtures {

    public static final String TRANSLATIONS_FIRST_SYNC_RESPONSE = "translationsFirstSync.json";
    public static final String TRANSLATIONS_NO_RESULT_RESPONSE = "translationsSync.json";
    public static final String TRANSLATIONS_FIRST_SYNC_RESPONSE_SPANISH = "translationsFirstSyncSpanish.json";
    public static final String TRANSLATIONS_NO_RESULT_SPANISH_RESPONSE = "translationsSyncSpanish.json";
    public static final String TRANSLATIONS_UPDATE_REMOVE_RESPONSE = "translationsRemoveSomeSync.json";
    public static final String TRANSLATIONS_NULL_VALUES_RESPONSE = "translationsSyncNullValues.json";

    public static void enqueueServerFile(MockServer server, String file) throws IOException {
        server.enqueueFile(200, file);
    }

    public static void enqueueServerError(MockServer server, int code) {
        server.enqueue(code);
    }
}
