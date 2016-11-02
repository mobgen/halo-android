package com.mobgen.halo.android.translations;

import android.database.Cursor;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.widget.TextView;

import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.content.models.HaloSyncLog;
import com.mobgen.halo.android.content.models.SyncQuery;
import com.mobgen.halo.android.content.spec.HaloContentContract;
import com.mobgen.halo.android.framework.api.HaloStorageApi;
import com.mobgen.halo.android.framework.api.StorageConfig;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.helpers.callbacks.StrongCallbackCluster;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.storage.database.HaloDatabaseErrorHandler;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloPluginApi;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;
import com.mobgen.halo.android.sdk.core.selectors.HaloSelectorFactory;
import com.mobgen.halo.android.sdk.core.selectors.SelectorRaw2Unparse;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;
import com.mobgen.halo.android.sdk.core.threading.ICancellable;
import com.mobgen.halo.android.sdk.core.threading.InteractorExecutionCallback;
import com.mobgen.halo.android.translations.callbacks.DefaultTextHandler;
import com.mobgen.halo.android.translations.callbacks.TextReadyListener;
import com.mobgen.halo.android.translations.callbacks.TranslationsErrorListener;
import com.mobgen.halo.android.translations.callbacks.TranslationsLoadListener;
import com.mobgen.halo.android.translations.repository.Cursor2MapTranslationConverter;
import com.mobgen.halo.android.translations.repository.TranslationsLocalDatasource;
import com.mobgen.halo.android.translations.repository.TranslationsRepository;
import com.mobgen.halo.android.translations.spec.HaloTranslationsContract;
import com.mobgen.halo.android.translations.spec.TranslationsMigration2$0$0;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The HALO Translations SDK allows you to use a HALO module as a translations module. This means
 * you can be able to translate the whole app texts using halo instances.
 * To do so you must create a HALO module with two fields minimum, one with type text and another
 * wit type localized text. One of them will serve as a key to access the text, and the other will
 * serve as the value localized.
 * <p>
 * You can create a new instance of the module by doing the following.
 * <pre><code>
 * HaloTranslationsApi api = HaloTranslationsApi.with(halo)
 *      .moduleName("name of my module")
 *      .defaultText("Pending to be localized")
 *      .build();
 * api.load(true);
 * api.textOn(myTextView, "custom_key");
 * </code></pre>
 * <p>
 * The previous code initializes the translations api with the name of the module and
 * a default text. The default text will be displayed in case of a missing key is requested.
 * You must call the {@link #load()} method to sync the current translations with the ones present
 * in the server.
 * <p>
 * The library also provides some helpful functions to change the localized texts on TextView's
 * making you to forget about synchronization between the view and the translations loaded.
 */
public final class HaloTranslationsApi extends HaloPluginApi implements HaloContentApi.HaloSyncListener {

    /**
     * Map with all the translations for the provided configurations.
     */
    @NonNull
    private Map<String, String> mTranslationsMap;

    /**
     * Keys that contains the name of the key and the name of the value of the general content
     * that will be taken to generate the translations api.
     */
    @NonNull
    private Pair<String, String> mNameKeys;

    /**
     * The default value that will be provided in case of the key does not exist or
     * is not available yet.
     */
    @NonNull
    private DefaultTextHandler mDefaultTextHandler;

    /**
     * The module name used to load the texts.
     */
    @NonNull
    private String mModuleName;

    /**
     * The locale that will be used in this translation.
     */
    @NonNull
    @HaloLocale.LocaleDefinition
    private String mLocale;

    /**
     * Checks if the texts has been loaded.
     */
    private boolean mIsLoading;

    /**
     * Determines if during async the default value should be provided.
     */
    private boolean mProvideDefaultOnAsync;

    /**
     * Subscription to the synchronization process.
     */
    @Nullable
    private ISubscription mSyncSubscription;

    /**
     * Callbacks for the localized texts.
     */
    @NonNull
    private StrongCallbackCluster<Runnable> mTextCallbacks;

    /**
     * Translations load listeners.
     */
    @NonNull
    private StrongCallbackCluster<TranslationsLoadListener> mTranslationsLoadListeners;

    /**
     * The translations error listener.
     */
    @Nullable
    private TranslationsErrorListener mErrorListener;

    /**
     * The translations interaction storage.
     */
    @NonNull
    private TranslationsRepository mTranslationsRepository;

    /**
     * The content api to refer for the requests.
     */
    @NonNull
    private HaloContentApi mContentApi;

    /**
     * The fetch request.
     */
    @Nullable
    private ICancellable mFetchRequest;

    /**
     * The constructor with the translations.
     *
     * @param contentApi          The content api.
     * @param translationsStorage The translations storage.
     * @param defaultTextHandler  The text handler.
     * @param builder             The builder to fromCursor this translations api.
     */
    private HaloTranslationsApi(@NonNull HaloContentApi contentApi, @NonNull TranslationsRepository translationsStorage, @NonNull Builder builder, @NonNull DefaultTextHandler defaultTextHandler) {
        super(builder.mHalo);
        mTranslationsMap = new HashMap<>();
        mNameKeys = builder.mKeys;
        mLocale = builder.mLocale;
        mModuleName = builder.mModuleName;
        mProvideDefaultOnAsync = builder.mProvideDefaultOnAsync;
        mDefaultTextHandler = defaultTextHandler;
        mTranslationsRepository = translationsStorage;
        mContentApi = contentApi;
        mTextCallbacks = new StrongCallbackCluster<Runnable>() {
            @Override
            public void notifyCallback(Runnable callback, Object... args) {
                callback.run();
            }
        };
        mTranslationsLoadListeners = new StrongCallbackCluster<TranslationsLoadListener>() {
            @Override
            public void notifyCallback(TranslationsLoadListener callback, Object... args) {
                callback.onTranslationsLoaded();
            }
        };
    }

    /**
     * Creates the builder using an already configured instance of Halo.
     *
     * @param halo The halo instance.
     * @return The current builder.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public static Builder with(@NonNull Halo halo) {
        AssertionUtils.notNull(halo, "halo");
        return new Builder(halo);
    }

    /**
     * Performs the fetch to load the translations from the configured module.
     * This method will mark the {@link #isLoading()} as true until it finishes.
     * If the loading ends with a good state the texts will be loaded, otherwise
     * an error will be reported on the listener set in {@link #setErrorListener(TranslationsErrorListener)}.
     */
    @Keep
    @Api(2.0)
    public void load() {
        load(null);
    }

    /**
     * Performs the fetch to load the translations from the configured module.
     * This method will mark the {@link #isLoading()} as true until it finishes.
     * If the loading ends with a good state the texts will be loaded, otherwise
     * an error will be reported on the listener set in {@link #setErrorListener(TranslationsErrorListener)}.
     *
     * @param listener  A listener to be notified when the text loading has been finished
     *                  correctly.
     */
    @Keep
    @Api(2.0)
    public synchronized void load(@Nullable TranslationsLoadListener listener) {
        if (listener != null) {
            mTranslationsLoadListeners.addCallback(listener);
        }
        if (!isLoading()) {
            mIsLoading = true;
            Halog.d(HaloTranslationsApi.this.getClass(), "Loading translations for module " + mModuleName);
            mSyncSubscription = mContentApi.subscribeToSync(mModuleName, HaloTranslationsApi.this);
            mContentApi.sync(SyncQuery.create(mModuleName, mLocale, Threading.SINGLE_QUEUE_POLICY), true);
        }
    }

    /**
     * Called when the synchronization finishes, even with no internet connection.
     *
     * @param status The status of the sync.
     * @param log    The log with the result of the sync.
     */
    @Override
    public void onSyncFinished(@NonNull HaloStatus status, @Nullable HaloSyncLog log) {
        unsubscribeSync();
        Halog.d(getClass(), "Translations sync event received for module " + mModuleName);

        //Only resync if there are some changes
        boolean mustResync = false;

        //Process the sync in case of no error
        Long lastSync = mTranslationsRepository.getLastSyncTimestamp(mModuleName);
        if (status.isOk() && log != null) {
            mustResync = log.didSomethingChange() || lastSync == null;
        }

        if (status.isOk()) {
            processSyncedInfo(mustResync);
        } else {
            mIsLoading = false;
            notifyIfError(status);
        }
    }

    /**
     * Provides the default text value given provided in the builder.
     * It uses the default {@link DefaultTextHandler} or the one overriden
     * in configuration.
     *
     * @param key The key that is linked to the text.
     * @return The default text for the given key.
     */
    @Keep
    @Api(2.0)
    @Nullable
    public String getDefaultText(@Nullable String key) {
        return mDefaultTextHandler.provideDefaultText(key, mIsLoading);
    }

    /**
     * Changes to a different locale. It the locale provided is the same
     * as the current one no action will be performed. In case of a new locale
     * the texts for this locale will be loaded into memory and the previous ones
     * will be cleared from the current instance.
     *
     * @param locale The locale to be changed to.
     */
    @Keep
    @Api(2.0)
    public void changeLocale(@HaloLocale.LocaleDefinition String locale) {
        changeLocale(locale, null);
    }

    /**
     * Changes to a different locale. It the locale provided is the same
     * as the current one no action will be performed. In case of a new locale
     * the texts for this locale will be loaded into memory and the previous ones
     * will be cleared from the current instance.
     *
     * @param locale The locale to be changed to.
     * @param listener The listener to be notified when everything is loaded. Keep in mind that if
     *                 the locale provided is the same no callback will be triggered.
     */
    @Keep
    @Api(2.0)
    public void changeLocale(@HaloLocale.LocaleDefinition String locale, @Nullable TranslationsLoadListener listener) {
        AssertionUtils.notNull(locale, "locale");
        if (!mLocale.equals(locale)) {
            cancel();
            clearAll();
            mLocale = locale;
            load(listener);
        }
    }

    /**
     * Clears all the in memory translations.
     */
    @Keep
    @Api(2.0)
    public void clearTranslations() {
        mTranslationsMap.clear();
    }

    /**
     * Clears all the TextView callbacks and the loading listener callbacks.
     */
    @Keep
    @Api(2.0)
    public void clearCallbacks() {
        mTextCallbacks.clear();
        mTranslationsLoadListeners.clear();
    }

    /**
     * Clears all the callbacks stored by this instance. You can call this if you are
     * not sure if all the listeners were released.
     */
    @Keep
    @Api(2.0)
    public void clearAll() {
        clearTranslations();
        clearCallbacks();
    }

    /**
     * Clears the in memory and the cached in local translations.
     * Note this method only provides the executor, so you can configure the thread
     * on which this is executed. Call execute to ensure this clearing is executed.
     * <pre><code>
     * api.clearCachedTranslations()
     *      .execute();
     * </code></pre>
     *
     *
     * @return A thread manager executor for the request to clear cached translations.
     */
    @Keep
    @Api(2.0)
    public HaloInteractorExecutor<Void> clearCachedTranslations() {
        return new HaloInteractorExecutor<Void>(
                halo(),
                "Clear the synced translations",
                new SelectorRaw2Unparse<>(
                        new TranslationsRepository.ClearTranslationsDataProvider(mTranslationsRepository, mModuleName),
                        Data.STORAGE_ONLY),
                new InteractorExecutionCallback() {
                    @Override
                    public void onPreExecute() {
                        synchronized (HaloTranslationsApi.this) {
                            clearTranslations();
                        }
                    }
                }
        );
    }

    /**
     * Cancels the current loading task.
     */
    @Keep
    @Api(2.0)
    public void cancel() {
        mIsLoading = false;
        if (mFetchRequest != null) {
            mFetchRequest.cancel();
            mFetchRequest = null;
        }
        unsubscribeSync();
        mTextCallbacks.clear();
        mTranslationsLoadListeners.clear();
    }

    /**
     * Removes the loading callback provided if it is available.
     *
     * @param callback The callback to remove.
     */
    @Keep
    @Api(2.0)
    public void removeLoadCallback(@Nullable TranslationsLoadListener callback) {
        mTranslationsLoadListeners.removeCallback(callback);
    }

    /**
     * Provides if translations are being loaded. You cannot call load multiple
     * times, only one load process will be executed.
     *
     * @return True if it is still loading, false otherwise.
     */
    @Keep
    @Api(2.0)
    public boolean isLoading() {
        return mIsLoading;
    }

    /**
     * Provides the text for the given key.
     *
     * @param key The key of the translation.
     * @return The text with the given key or the default value if it could not be found.
     */
    @Keep
    @Api(2.0)
    @Nullable
    public String getText(@Nullable String key) {
        String value = null;
        if (key != null) {
            value = mTranslationsMap.get(key);
            if (value == null) {
                value = getDefaultText(key);
            }
        }
        return value;
    }

    /**
     * Provides a list of translations for the given the keys.
     *
     * @param keys The keys.
     * @return The list of translations.
     */
    @Keep
    @NonNull
    @Api(2.0)
    public List<String> getTexts(@NonNull String... keys) {
        AssertionUtils.notNull(keys, "keys");
        List<String> translations = new ArrayList<>();
        for (String key : keys) {
            translations.add(getText(key));
        }
        return translations;
    }

    /**
     * Provides the list with all the texts.
     *
     * @return The list with all texts.
     */
    @Keep
    @NonNull
    @Api(2.0)
    public List<String> getAllTranslations() {
        return new ArrayList<>(mTranslationsMap.values());
    }

    /**
     * Provides a copy of the in memory cache.
     *
     * @return The in memory cache.
     */
    @Keep
    @NonNull
    @Api(2.0)
    public Map<String, String> getInMemoryTranslations() {
        return new HashMap<>(mTranslationsMap);
    }

    /**
     * Ensures the last load execution has finished before providing the text.
     *
     * @param key      The key to load the translation.
     * @param listener The listener to be notified with the translation.
     */
    @Keep
    @Api(2.0)
    public void getTextAsync(@Nullable final String key, @NonNull final TextReadyListener listener) {
        AssertionUtils.notNull(listener, "listener == null");
        if (!isLoading()) {
            listener.onTextReady(key, getText(key));
        } else {
            mTextCallbacks.addCallback(new Runnable() {
                @Override
                public void run() {
                    listener.onTextReady(key, getText(key));
                }
            });
            //Provides the default text while async.
            if (mProvideDefaultOnAsync) {
                listener.onTextReady(key, getText(key));
            }
        }
    }

    /**
     * Helper method that allows to load a translation in a given view. This view
     * is kept as a weak reference to add the translation without worrying of the view
     * life cycle.
     *
     * @param textView The text view where the text will be set.
     * @param key      The key of the translation.
     */
    @Keep
    @Api(2.0)
    public void textOn(@NonNull TextView textView, @Nullable String key) {
        AssertionUtils.notNull(textView, "textView");
        final WeakReference<TextView> weakText = new WeakReference<>(textView);
        getTextAsync(key, new TextReadyListener() {
            @Override
            public void onTextReady(@Nullable String key, @Nullable final String text) {
                postOnText(weakText, text);
            }
        });
    }

    /**
     * Provides the current locale configured.
     *
     * @return The current locale.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public String locale() {
        return mLocale;
    }

    /**
     * Provides the module name.
     *
     * @return The module name.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public String moduleName() {
        return mModuleName;
    }

    /**
     * Sets an error listener. It can only be set once, another set removes the previous
     * error listener.
     *
     * @param errorListener The error listener.
     */
    @Keep
    @Api(2.0)
    public void setErrorListener(@Nullable TranslationsErrorListener errorListener) {
        mErrorListener = errorListener;
    }

    /**
     * Takes all the data available from the sync and processes it into the table for local
     * synchronization.
     */
    private void processSyncedInfo(boolean mustResyncLocal) {
        mIsLoading = true;
        HaloStorageApi contentStorageApi = framework().storage(HaloContentContract.HALO_CONTENT_STORAGE);
        HaloSelectorFactory<Map<String, String>, Cursor> factory = new HaloSelectorFactory<>(
                halo(),
                new TranslationsRepository.SyncTranslationsInteractor(mTranslationsRepository, contentStorageApi, mModuleName, mLocale, mNameKeys.first, mNameKeys.second, mustResyncLocal),
                new Cursor2MapTranslationConverter(),
                null,
                Data.STORAGE_ONLY,
                "Sync the translations with the content table");

        //Execute the request as content parsed
        mFetchRequest = factory.asContent()
                .threadPolicy(Threading.POOL_QUEUE_POLICY)
                .execute(new CallbackV2<Map<String, String>>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<Map<String, String>> result) {
                        synchronized (HaloTranslationsApi.this) {
                            Halog.d(HaloTranslationsApi.this.getClass(), "Sync post process finished. Providing strings");
                            mIsLoading = false;
                            mFetchRequest = null;
                            Map<String, String> translations;
                            if ((translations = result.data()) != null) {
                                mTranslationsMap.clear();
                                mTranslationsMap.putAll(translations);
                            }
                            mTextCallbacks.notifyCallbacks();
                            mTranslationsLoadListeners.notifyCallbacks();
                            mTextCallbacks.clear();
                            mTranslationsLoadListeners.clear();
                            notifyIfError(result.status());
                        }
                    }
                });
    }

    /**
     * Unsubscribes from the synchronization item.
     */
    private void unsubscribeSync() {
        if (mSyncSubscription != null) {
            mSyncSubscription.unsubscribe();
            mSyncSubscription = null;
        }
    }

    /**
     * Notifies the error listener if there is an error.
     *
     * @param status The status.
     */
    private void notifyIfError(@NonNull HaloStatus status) {
        if (status.isError() && mErrorListener != null) {
            mErrorListener.onTranslationsError(status);
        }
    }

    /**
     * Post on the text view if it is available.
     *
     * @param weakTextView The weak reference to the text view.
     * @param text         The text parameter.
     */
    private void postOnText(@NonNull final WeakReference<TextView> weakTextView, @Nullable final String text) {
        TextView textView = weakTextView.get();
        if (textView != null) {
            textView.post(new Runnable() {
                @Override
                public void run() {
                    //If the text view is still available in posting
                    TextView textView = weakTextView.get();
                    if (textView != null) {
                        textView.setText(text);
                    }
                }
            });
        }
    }

    /**
     * Builder to bring the translations from the API.
     */
    @Keep
    public static class Builder implements IBuilder<HaloTranslationsApi> {

        /**
         * Halo instance.
         */
        @NonNull
        private Halo mHalo;

        /**
         * The default text handler.
         */
        @Nullable
        private DefaultTextHandler mDefaultTextHandler;

        /**
         * The default value that will be provided in case of the key does not exist or
         * is not available yet.
         */
        @Nullable
        private String mDefaultValue;

        /**
         * The text that will be shown while the item is being loaded.
         */
        @Nullable
        private String mLoadingText;

        /**
         * The module id used to load the texts.
         */
        private String mModuleName;

        /**
         * The locale that will be used in this translation.
         */
        @HaloLocale.LocaleDefinition
        private String mLocale;

        /**
         * Keys that contains the name of the key and the name of the value of the general content
         * that will be taken to generate the translations api.
         */
        private Pair<String, String> mKeys;
        /**
         * Parameter to provide the default value while it is loading the
         * real ones.
         */
        private boolean mProvideDefaultOnAsync;

        /**
         * Private constructor for the builder to do properly the translations.
         *
         * @param halo The halo instance.
         */
        protected Builder(@NonNull Halo halo) {
            AssertionUtils.notNull(halo, "halo");
            mHalo = halo;
        }

        /**
         * Adds the key value and name value for the general content instance of HALO.
         *
         * @param keyName   The key field name.
         * @param valueName The value field name.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder keyValue(@NonNull String keyName, @NonNull String valueName) {
            AssertionUtils.notNull(keyName, "keyName == null");
            AssertionUtils.notNull(valueName, "valueName == null");
            mKeys = new Pair<>(keyName, valueName);
            return this;
        }

        /**
         * Sets the default text value that will be provided in case the translations is not loaded yet.
         *
         * @param text The text to provide.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder defaultText(@Nullable String text) {
            mDefaultValue = text;
            return this;
        }

        /**
         * Provides the default text while this item is loading.
         *
         * @param loadingText The loading text.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder defaultLoadingText(@Nullable String loadingText) {
            mLoadingText = loadingText;
            return this;
        }

        /**
         * While loading the values it will call the text ready listener with the
         * default value meanwhile the real one is being loaded.
         *
         * @param shouldProvide Provides the value.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder provideDefaultOnAsync(boolean shouldProvide) {
            mProvideDefaultOnAsync = shouldProvide;
            return this;
        }

        /**
         * Handler to provide default texts based on some other parameter
         * than the default text itself. This allows you to put different default texts
         * depending on the key.
         *
         * @param handler The callback handler.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder defaultText(@Nullable DefaultTextHandler handler) {
            mDefaultTextHandler = handler;
            return this;
        }

        /**
         * Defines a locale to bring the translations.
         *
         * @param locale the locale to be used.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder locale(@NonNull @HaloLocale.LocaleDefinition String locale) {
            AssertionUtils.notNull(locale, "locale");
            mLocale = locale;
            return this;
        }

        /**
         * Provides the module id used to load the texts.
         *
         * @param moduleName The module name.
         * @return The current builder.
         */
        @Keep
        @Api(2.0)
        @NonNull
        public Builder moduleName(@NonNull String moduleName) {
            AssertionUtils.notNull(moduleName, "moduleName");
            mModuleName = moduleName;
            return this;
        }

        /**
         * Creates a new storage api if it is not already available. This way we can
         * keep controlled all the database instances.
         *
         * @param halo The HALO instance.
         * @return The storage api created or the instance stored in the framework.
         */
        @NonNull
        private static HaloStorageApi createStorage(@NonNull Halo halo) {
            return halo.framework().createStorage(StorageConfig.builder()
                    .storageName(HaloTranslationsContract.HALO_TRANSLATIONS_STORAGE)
                    .databaseVersion(HaloTranslationsContract.CURRENT_VERSION)
                    .errorHandler(new HaloDatabaseErrorHandler())
                    .addMigrations(
                            new TranslationsMigration2$0$0())
                    .build()
            );
        }

        @Keep
        @Api(2.0)
        @NonNull
        @Override
        public HaloTranslationsApi build() {
            AssertionUtils.notNull(mKeys, "keyValue");
            AssertionUtils.notNull(mLocale, "locale");
            AssertionUtils.notNull(mModuleName, "moduleName");
            if (mDefaultTextHandler == null) {
                mDefaultTextHandler = new DefaultTextHandler() {
                    @Override
                    public String provideDefaultText(@Nullable String key, boolean isLoading) {
                        return isLoading && mLoadingText != null ? mLoadingText : mDefaultValue;
                    }
                };
            }
            HaloStorageApi storageApi = createStorage(mHalo);
            HaloContentApi contentApi = HaloContentApi.with(mHalo);
            return new HaloTranslationsApi(
                    contentApi,
                    new TranslationsRepository(new TranslationsLocalDatasource(storageApi)),
                    this,
                    mDefaultTextHandler);
        }
    }
}