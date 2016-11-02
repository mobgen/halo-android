package com.mobgen.halo.android.translations.api;

import android.widget.TextView;

import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;
import com.mobgen.halo.android.translations.HaloTranslationsApi;
import com.mobgen.halo.android.translations.callbacks.TranslationsLoadListener;

import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;

import static com.mobgen.halo.android.translations.mock.fixtures.ServerFixtures.TRANSLATIONS_FIRST_SYNC_RESPONSE;
import static com.mobgen.halo.android.translations.mock.fixtures.ServerFixtures.TRANSLATIONS_NO_RESULT_RESPONSE;
import static com.mobgen.halo.android.translations.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.translations.mock.instrumentation.HaloMock.givenACustomHalo;
import static com.mobgen.halo.android.translations.mock.instrumentation.HaloMock.givenADelayedExecutorWithParserConfig;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloAsyncTranslationsTest extends HaloRobolectricTest {

    private static final String MODULE_NAME_FAKE = "myModuleName";

    private MockServer mMockServer;
    private Halo mAsyncHalo;

    @Override
    public void onStart() throws Exception {
        mMockServer = MockServer.create();
        mAsyncHalo = givenACustomHalo(givenADelayedExecutorWithParserConfig(mMockServer.start()));
    }

    @Override
    public void onDestroy() throws Exception {
        mAsyncHalo.uninstall();
        mMockServer.shutdown();
    }

    public HaloTranslationsApi.Builder translationsBuilder() {
        return HaloTranslationsApi.with(mAsyncHalo)
                .keyValue("key", "value")
                .locale(HaloLocale.ENGLISH_UNITED_STATES)
                .moduleName(MODULE_NAME_FAKE);
    }

    @Test
    public void thatMultipleTextsAreSetAsynchronously() throws IOException {
        //First sync needs two syncs
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        TextView view1 = new TextView(RuntimeEnvironment.application);
        TextView view2 = new TextView(RuntimeEnvironment.application);
        TextView view3 = new TextView(RuntimeEnvironment.application);
        HaloTranslationsApi translationsApi = translationsBuilder()
                .defaultText("Default")
                .build();

        translationsApi.load();
        translationsApi.textOn(view1, "first_screen");
        translationsApi.textOn(view2, "third_screen");
        translationsApi.textOn(view3, "second_screen");

        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();

        assertThat(view1.getText().toString()).isEqualTo("First");
        assertThat(view2.getText().toString()).isEqualTo("Third");
        assertThat(view3.getText().toString()).isEqualTo("Default");
    }

    @Test
    public void thatDefaultOnAsyncKeepsDefaultWhileLoading() throws IOException {
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        TextView view = new TextView(RuntimeEnvironment.application);
        HaloTranslationsApi translationsApi = translationsBuilder()
                .defaultText("Default")
                .provideDefaultOnAsync(true)
                .build();

        translationsApi.load();
        translationsApi.textOn(view, "first_screen");
        assertThat(view.getText().toString()).isEqualTo("Default");
        Robolectric.flushForegroundThreadScheduler();
        Robolectric.flushForegroundThreadScheduler();
        assertThat(view.getText().toString()).isEqualTo("First");
    }

    @Test
    public void thatCancelAvoidsCallbacks() throws IOException {
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        final CallbackFlag flag = new CallbackFlag();
        HaloTranslationsApi translationsApi = translationsBuilder()
                .defaultText("Default")
                .provideDefaultOnAsync(true)
                .build();

        translationsApi.load(new TranslationsLoadListener() {
            @Override
            public void onTranslationsLoaded() {
                flag.flagExecuted();
            }
        });
        translationsApi.cancel();
        Robolectric.flushForegroundThreadScheduler();
        assertThat(flag.isFlagged()).isFalse();
    }

    @Test
    public void thatARemovedCallbackIsNotCalled() throws IOException {
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        final CallbackFlag flag = new CallbackFlag();
        HaloTranslationsApi translationsApi = translationsBuilder()
                .defaultText("Default")
                .provideDefaultOnAsync(true)
                .build();

        TranslationsLoadListener listener = new TranslationsLoadListener() {
            @Override
            public void onTranslationsLoaded() {
                flag.flagExecuted();
            }
        };
        translationsApi.load(listener);
        translationsApi.removeLoadCallback(listener);
        Robolectric.flushForegroundThreadScheduler();
        assertThat(flag.isFlagged()).isFalse();
    }
}
