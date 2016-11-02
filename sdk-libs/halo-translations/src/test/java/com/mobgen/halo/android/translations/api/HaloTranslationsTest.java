package com.mobgen.halo.android.translations.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;
import com.mobgen.halo.android.testing.CallbackFlag;
import com.mobgen.halo.android.testing.HaloRobolectricTest;
import com.mobgen.halo.android.testing.MockServer;
import com.mobgen.halo.android.translations.HaloTranslationsApi;
import com.mobgen.halo.android.translations.callbacks.DefaultTextHandler;
import com.mobgen.halo.android.translations.callbacks.TextReadyListener;
import com.mobgen.halo.android.translations.callbacks.TranslationsErrorListener;
import com.mobgen.halo.android.translations.callbacks.TranslationsLoadListener;

import org.junit.Test;
import org.robolectric.RuntimeEnvironment;

import java.io.IOException;
import java.lang.reflect.Field;

import static com.mobgen.halo.android.translations.mock.fixtures.ServerFixtures.TRANSLATIONS_FIRST_SYNC_RESPONSE;
import static com.mobgen.halo.android.translations.mock.fixtures.ServerFixtures.TRANSLATIONS_FIRST_SYNC_RESPONSE_SPANISH;
import static com.mobgen.halo.android.translations.mock.fixtures.ServerFixtures.TRANSLATIONS_NO_RESULT_RESPONSE;
import static com.mobgen.halo.android.translations.mock.fixtures.ServerFixtures.TRANSLATIONS_NO_RESULT_SPANISH_RESPONSE;
import static com.mobgen.halo.android.translations.mock.fixtures.ServerFixtures.TRANSLATIONS_NULL_VALUES_RESPONSE;
import static com.mobgen.halo.android.translations.mock.fixtures.ServerFixtures.TRANSLATIONS_UPDATE_REMOVE_RESPONSE;
import static com.mobgen.halo.android.translations.mock.fixtures.ServerFixtures.enqueueServerError;
import static com.mobgen.halo.android.translations.mock.fixtures.ServerFixtures.enqueueServerFile;
import static com.mobgen.halo.android.translations.mock.instrumentation.HaloMock.givenADefaultHalo;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class HaloTranslationsTest extends HaloRobolectricTest {

    private static final String MODULE_NAME_FAKE = "myModuleName";

    private MockServer mMockServer;
    private Halo mHalo;

    @Override
    public void onStart() throws Exception {
        mMockServer = MockServer.create();
        mHalo = givenADefaultHalo(mMockServer.start());
    }

    @Override
    public void onDestroy() throws Exception {
        mHalo.uninstall();
        mMockServer.shutdown();
    }

    public HaloTranslationsApi.Builder translationsBuilder() {
        return HaloTranslationsApi.with(mHalo)
                .keyValue("key", "value")
                .locale(HaloLocale.ENGLISH_UNITED_STATES)
                .moduleName(MODULE_NAME_FAKE);
    }

    @Test(expected = NullPointerException.class)
    public void thatHaloCannotBeNull() {
        HaloTranslationsApi.with(null);
    }

    @Test(expected = NullPointerException.class)
    public void thatKeyValueCannotBeNull() {
        HaloTranslationsApi.with(mHalo)
                .locale(HaloLocale.ENGLISH_UNITED_STATES)
                .moduleName("id")
                .build();
    }

    @Test(expected = NullPointerException.class)
    public void thatLocaleCannotBeNull() {
        HaloTranslationsApi.with(mHalo)
                .keyValue("myKey", "myValue")
                .moduleName("id")
                .build();
    }

    @Test(expected = NullPointerException.class)
    public void thatModuleCannotBeNull() {
        HaloTranslationsApi.with(mHalo)
                .keyValue("myKey", "myValue")
                .locale(HaloLocale.ENGLISH_UNITED_STATES)
                .build();
    }

    @Test
    public void thatTranslationsCreatesCorrectly() {
        HaloTranslationsApi.Builder apiBuilder = translationsBuilder();
        assertThat(apiBuilder).isNotNull();
        HaloTranslationsApi api = apiBuilder.build();
        assertThat(api).isNotNull();
        assertThat(api.locale()).isEqualTo(HaloLocale.ENGLISH_UNITED_STATES);
        assertThat(api.moduleName()).isEqualTo(MODULE_NAME_FAKE);
    }

    @Test
    public void thatDefaultTextIsRetrievedWithNotContainedKey() {
        String defaultText = "My default text";
        HaloTranslationsApi translations = translationsBuilder().defaultText(defaultText).build();
        assertThat(translations.getText("myKey")).isEqualTo(defaultText);
        assertThat(translations.getDefaultText("myKey")).isEqualTo(defaultText);
    }

    @Test
    public void thatADefaultHandlerProvidesDefaultWhenNeeded() {
        HaloTranslationsApi translations = translationsBuilder().defaultText(new DefaultTextHandler() {
            @Nullable
            @Override
            public String provideDefaultText(@Nullable String key, boolean isLoading) {
                return "transformed_" + key;
            }
        }).build();
        assertThat(translations.getText("myKey")).isEqualTo("transformed_" + "myKey");
        assertThat(translations.getDefaultText("myKey")).isEqualTo("transformed_" + "myKey");
    }

    @Test
    public void thatOnLoadingDefaultLoadingTextIsProvided() throws NoSuchFieldException, IllegalAccessException {
        String defaultTranslation = "myDefaultText";
        String defaultLoadingTranslation = "myDefaultLoadingText";
        HaloTranslationsApi translationsApi = translationsBuilder()
                .defaultText(defaultTranslation)
                .defaultLoadingText(defaultLoadingTranslation)
                .build();

        assertThat(translationsApi.getDefaultText("myKey")).isEqualTo(defaultTranslation);
        //Mark as it was loading...
        Field field = translationsApi.getClass().getDeclaredField("mIsLoading");
        field.setAccessible(true);
        field.set(translationsApi, true);
        field.setAccessible(false);
        //Ensure that keeps being loading text
        assertThat(translationsApi.getDefaultText("myKey")).isEqualTo(defaultLoadingTranslation);
    }

    @Test
    public void thatEvenLoadingWithoutDefaultLoadingTextReturnsDefault() throws NoSuchFieldException, IllegalAccessException {
        String defaultTranslation = "myDefaultText";
        HaloTranslationsApi translationsApi = translationsBuilder()
                .defaultText(defaultTranslation)
                .build();

        assertThat(translationsApi.getDefaultText("myKey")).isEqualTo(defaultTranslation);
        //Mark as it was loading...
        Field field = translationsApi.getClass().getDeclaredField("mIsLoading");
        field.setAccessible(true);
        field.set(translationsApi, true);
        field.setAccessible(false);
        //Ensure that keeps being loading text
        assertThat(translationsApi.getDefaultText("myKey")).isEqualTo(defaultTranslation);
    }

    @Test
    public void thatTheTranslationsSyncWorksCorrectly() throws IOException {
        //First sync needs two syncs
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        HaloTranslationsApi translations = translationsBuilder().build();
        translations.load();
        assertThat(translations.getText("first_screen")).isEqualTo("First");
        assertThat(translations.getText("second_screen")).isNull();
        assertThat(translations.getText("third_screen")).isEqualTo("Third");
        assertThat(translations.getText("fake_key")).isNull();
        assertThat(translations.getInMemoryTranslations()).isNotEmpty().size().isEqualTo(3);
        assertThat(translations.getAllTranslations()).isNotEmpty().size().isEqualTo(3);
    }

    @Test
    public void thatClearingTranslationsBringsDefaultButLoadsAgain() throws IOException {
        //First sync needs two syncs
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        HaloTranslationsApi translations = translationsBuilder()
                .defaultText("default")
                .build();
        translations.load();
        assertThat(translations.getText("first_screen")).isEqualTo("First");
        translations.clearTranslations();
        assertThat(translations.getText("first_screen")).isEqualTo("default");
        translations.load();
        assertThat(translations.getText("first_screen")).isEqualTo("First");
    }

    @Test
    public void thatCachedTranslationsRemovesDatabaseInLocal() throws IOException {
        //First sync needs two syncs
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE_SPANISH);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_SPANISH_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        HaloTranslationsApi translations = translationsBuilder()
                .defaultText("default")
                .build();
        translations.load();
        assertThat(translations.getText("first_screen")).isEqualTo("First");
        translations.changeLocale(HaloLocale.SPANISH_SPAIN);
        assertThat(translations.getText("first_screen")).isEqualTo("Primero");
        translations.clearCachedTranslations().execute();
        assertThat(translations.getText("first_screen")).isEqualTo("default");
        translations.changeLocale(HaloLocale.ENGLISH_UNITED_STATES);
        assertThat(translations.getText("first_screen")).isEqualTo("First");
    }

    @Test
    public void thatChangingLocaleSwapsToOtherLocale() throws IOException {
        //First sync needs two syncs
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE_SPANISH);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_SPANISH_RESPONSE);
        HaloTranslationsApi translations = translationsBuilder().build();
        translations.load();
        assertThat(translations.getText("first_screen")).isEqualTo("First");
        translations.changeLocale(HaloLocale.SPANISH_SPAIN);
        assertThat(translations.getText("first_screen")).isEqualTo("Primero");
    }

    @Test
    public void thatResyncedWithMissingTranslationRemovesCurrent() throws IOException {
        //First sync needs two syncs
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_UPDATE_REMOVE_RESPONSE);
        HaloTranslationsApi translations = translationsBuilder().build();
        translations.load();
        assertThat(translations.getText("first_screen")).isEqualTo("First");
        assertThat(translations.getText("third_screen")).isEqualTo("Third");
        translations.load();
        assertThat(translations.getText("first_screen")).isEqualTo("First");
        assertThat(translations.getText("third_screen")).isNull();
    }

    @Test
    public void thatACallbackIsCalledForTheTranslation() throws IOException {
        //First sync needs two syncs
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        final CallbackFlag flag = new CallbackFlag();
        HaloTranslationsApi translationsApi = translationsBuilder().build();
        translationsApi.load(new TranslationsLoadListener() {
            @Override
            public void onTranslationsLoaded() {
                flag.flagExecuted();
            }
        });
        assertThat(flag.isFlagged()).isTrue();
        assertThat(flag.timesExecuted()).isEqualTo(1);
    }

    @Test
    public void thatAnErrorCallbackIsExecutedOnErrorLoading() throws IOException {
        //First sync needs two syncs
        enqueueServerError(mMockServer, 500);
        final CallbackFlag flag = new CallbackFlag();
        HaloTranslationsApi translationsApi = translationsBuilder().build();
        translationsApi.setErrorListener(new TranslationsErrorListener() {
            @Override
            public void onTranslationsError(@NonNull HaloStatus status) {
                flag.flagExecuted();
            }
        });
        translationsApi.load();
        assertThat(flag.isFlagged()).isTrue();
        assertThat(flag.timesExecuted()).isEqualTo(1);
    }

    @Test
    public void thatChangingToSameLocaleDoesNotLoad() throws IOException {
        //First sync needs two syncs
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        enqueueServerError(mMockServer, 500);
        final CallbackFlag flag = new CallbackFlag();
        HaloTranslationsApi translationsApi = translationsBuilder().build();
        translationsApi.load();
        translationsApi.changeLocale(HaloLocale.ENGLISH_UNITED_STATES);
        translationsApi.setErrorListener(new TranslationsErrorListener() {
            @Override
            public void onTranslationsError(@NonNull HaloStatus status) {
                flag.flagExecuted();
            }
        });
        assertThat(flag.isFlagged()).isFalse();
    }

    @Test
    public void thatGetTextAsyncProvidesTheTextWhenNotLoading() throws IOException {
        //First sync needs two syncs
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        HaloTranslationsApi translationsApi = translationsBuilder().build();

        translationsApi.load();
        translationsApi.getTextAsync("first_screen", new TextReadyListener() {
            @Override
            public void onTextReady(@Nullable String key, @Nullable String text) {
                assertThat(key).isEqualTo("first_screen");
                assertThat(text).isEqualTo("First");
            }
        });
    }

    @Test
    public void thatGettingAllTextsProvidesAllValuesOrDefault() throws IOException {
        //First sync needs two syncs
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        HaloTranslationsApi translationsApi = translationsBuilder().build();

        translationsApi.load();
        assertThat(translationsApi.getTexts("first_screen", "second_screen", "third_screen", "fake"))
                .containsSequence("First", null, "Third", null);
    }

    @Test
    public void thatTextIsSetIntoATextView() throws IOException {
        //First sync needs two syncs
        enqueueServerFile(mMockServer, TRANSLATIONS_FIRST_SYNC_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        HaloTranslationsApi translationsApi = translationsBuilder().build();
        TextView sampleView = new TextView(RuntimeEnvironment.application);

        translationsApi.load();
        translationsApi.textOn(sampleView, "first_screen");
        assertThat(sampleView.getText().toString()).isEqualTo("First");
    }

    @Test
    public void thatIncorrectKeyValueProducesError() throws IOException {
        //First sync needs two syncs
        enqueueServerFile(mMockServer, TRANSLATIONS_NULL_VALUES_RESPONSE);
        enqueueServerFile(mMockServer, TRANSLATIONS_NO_RESULT_RESPONSE);
        final CallbackFlag flag = new CallbackFlag();
        HaloTranslationsApi translationsApi = translationsBuilder()
                .keyValue("invalidKey", "invalidValue")
                .build();
        translationsApi.setErrorListener(new TranslationsErrorListener() {
            @Override
            public void onTranslationsError(@NonNull HaloStatus status) {
                flag.flagExecuted();
            }
        });
        translationsApi.load();
        assertThat(flag.isFlagged()).isTrue();
        assertThat(flag.timesExecuted()).isEqualTo(1);
    }
}
