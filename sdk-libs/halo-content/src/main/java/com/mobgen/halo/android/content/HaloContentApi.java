package com.mobgen.halo.android.content;

import android.database.Cursor;
import android.support.annotation.CheckResult;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.mobgen.halo.android.content.generated.GeneratedHaloDatabase;
import com.mobgen.halo.android.content.models.HaloContentInstance;
import com.mobgen.halo.android.content.models.HaloSyncLog;
import com.mobgen.halo.android.content.models.Paginated;
import com.mobgen.halo.android.content.models.SearchQuery;
import com.mobgen.halo.android.content.models.SyncQuery;
import com.mobgen.halo.android.content.search.ClearSearchInstancesInteractor;
import com.mobgen.halo.android.content.search.ContentSearchLocalDatasource;
import com.mobgen.halo.android.content.search.ContentSearchRemoteDatasource;
import com.mobgen.halo.android.content.search.ContentSearchRepository;
import com.mobgen.halo.android.content.search.Cursor2ClassSearchConverterFactory;
import com.mobgen.halo.android.content.search.Cursor2ContentInstanceSearchConverter;
import com.mobgen.halo.android.content.search.SearchInteractor;
import com.mobgen.halo.android.content.selectors.HaloContentSelectorFactory;
import com.mobgen.halo.android.content.spec.HaloContentContract;
import com.mobgen.halo.android.content.spec.HaloContentMigration2$0$0;
import com.mobgen.halo.android.content.sync.ContentSyncLocalDatasource;
import com.mobgen.halo.android.content.sync.ContentSyncRemoteDatasource;
import com.mobgen.halo.android.content.sync.ContentSyncRepository;
import com.mobgen.halo.android.content.sync.Cursor2ClassSyncConverterFactory;
import com.mobgen.halo.android.content.sync.Cursor2ContentInstanceSyncConverter;
import com.mobgen.halo.android.content.sync.Cursor2SyncLogListConverter;
import com.mobgen.halo.android.content.sync.ModuleSyncHelper;
import com.mobgen.halo.android.content.sync.SyncDataProviders;
import com.mobgen.halo.android.content.sync.SyncModuleSchedule;
import com.mobgen.halo.android.framework.api.HaloStorageApi;
import com.mobgen.halo.android.framework.api.StorageConfig;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.storage.database.HaloDatabaseErrorHandler;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.EventId;
import com.mobgen.halo.android.framework.toolbox.bus.Subscriber;
import com.mobgen.halo.android.framework.toolbox.data.Data;
import com.mobgen.halo.android.framework.toolbox.threading.Threading;
import com.mobgen.halo.android.framework.toolbox.data.HaloStatus;
import com.mobgen.halo.android.framework.toolbox.scheduler.Job;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloPluginApi;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;
import com.mobgen.halo.android.sdk.core.selectors.HaloSelectorFactory;
import com.mobgen.halo.android.sdk.core.selectors.SelectorRaw2Unparse;
import com.mobgen.halo.android.sdk.core.threading.HaloInteractorExecutor;
import com.mobgen.halo.android.sdk.core.threading.InteractorExecutionCallback;

import java.util.List;


/**
 * The content API is the facade for the Content HALO SDK. Importing
 * this library will need a valid HALO instance configured with
 * the credentials.
 * The HALO Content SDK allows the user to retrieve instances
 * from the HALO Backend in two main ways:
 * <ol>
 * <li>Search</li>
 * <li>Sync</li>
 * </ol>
 * Creating an instance of the Content API is really simple once you have
 * your HALO running. Just write the following line:
 * <pre><code>
 * HaloContentApi contentApi = HaloContentApi.with(halo);
 * </code></pre>
 * In addition you can provide a default locale to include it in all
 * the queries even if you didn't provide it in the search/sync.
 * <p>
 * <h2>Search</h2>
 * The <b>search</b> api takes the module instances and provides the information
 * stored in the backend based on a query. You can define many params in this
 * query such as the module name, the module ids, user segmented tags,
 * ttl and much more. You can also define a custom search query to retrieve
 * selected instances based on equality criteria. Take the following
 * query as an example:
 * <pre><code>
 * SearchQuery.builder()
 *     .moduleName("My module")
 *     .beginSearch()
 *     .eq("name", "sample")
 *     .and()
 *     .gt("amount", 10)
 *     .end();
 * </code></pre>
 * Once you have the query you can request the information by calling
 * the search api:
 * <pre><code>
 * contentApi.search(Data.Network, query)
 *     .asContent(MyCustomClass.class)
 *     .execute(new CallbackV2<List<MyCustomClass>>() {
 *          public void onFinish(@NonNull HaloResultV2<List<MyCustomClass>> result) {
 *              //Here you can handle the result.status() and result.data()
 *          }
 *     });
 * </code></pre>
 * <p>
 * As you can see there is a param called Data that can take 3 different
 * values:
 * <ul>
 * <li>NETWORK_ONLY: Just performs a network query to retrieve the content.</li>
 * <li>STORAGE_ONLY: Provides the cached content for the given query. It uses as id
 * the searchTag param of the query or a hash if this tag is not provided.</li>
 * <li>NETWORK_AND_STORAGE: Performs a network call and caches the result into
 * the local storage.</li>
 * </ul>
 * <p>
 * <h2>Sync</h2>
 * The <b>sync</b> operation is focused on performance for a huge amount of
 * data. This api allows to bring in background and cache all the instances
 * of a given element and sync them only with the status that has changed
 * in time.
 * <p>
 * In the sync process requesting the sync and listening for updates
 * are different thus we can say it is composed of three steps.
 * <ol>
 * <li>Start listening for updates</li>
 * <li>Request a sync of a given module</li>
 * <li>Request the data synced</li>
 * </ol>
 * <p>
 * <h3>Start listening for updates</h3>
 * You can subscribe from anywhere in the app to syncs of a given module. Use
 * the following API to request that sync:
 * <pre><code>
 * ISubscription subscription = contentApi.subscribeToSync("myModuleId", listener);
 * </code></pre>
 * <p>
 * Once you are done listening for this updates you can unsubscribe from it
 * by calling:
 * <pre><code>
 * subscription.unsubscribe();
 * </code></pre>
 * <p>
 * <h3>Request a sync of a given module</h3>
 * To request a sync you have to select the module to sync and fromCursor a
 * {@link SyncQuery}. With this query you can call the content api instance
 * and it will perform the synchronization.
 * <pre><code>
 * SyncQuery query = SyncQuery.fromCursor("myModuleId", Threading.POOL_QUEUE_POLICY);
 * contentApi.sync(query, true);
 * </code></pre>
 * <p>
 * The second param of the sync allows you to mark the sync as immediate or leave it to
 * wait for an internet connection to be ready.
 * <p>
 * <h3>Request the data synced</h3>
 * Once the sync has finished correctly, you can request the content
 * by calling {@link #getSyncInstances(String)}. It works in a similar
 * way as the search, but only accepts a moduleId to ask for the instances
 * and searching in this data is not allowed yet.
 */
public class HaloContentApi extends HaloPluginApi {

    /**
     * Synchronization event id that is used to notify across all the
     * listeners that are subscribed into the framework for a given module.
     */
    @Keep
    @Api(2.0)
    public static final String SYNC_FINISHED_EVENT = ":halo:event:sync_finished:";

    /**
     * Every instance of the api allows to have a default locale that will be
     * used as a default for all the queries if no locale is provided. It can
     * be null.
     */
    @Nullable
    @HaloLocale.LocaleDefinition
    private final String mLocale;

    /**
     * Internal search repository.
     */
    @NonNull
    private ContentSearchRepository mContentSearchRepository;

    /**
     * Internal sync repository.
     */
    @NonNull
    private ContentSyncRepository mContentSyncRepository;

    /**
     * Internal private constructor for the halo plugin.
     *
     * @param halo                    The halo instance.
     * @param contentSearchRepository The internal content search repository.
     * @param contentSyncRepository   The internal content sync repository.
     * @param locale                  The locale of the content.
     */
    private HaloContentApi(@NonNull Halo halo, @NonNull ContentSearchRepository contentSearchRepository, @NonNull ContentSyncRepository contentSyncRepository, @Nullable @HaloLocale.LocaleDefinition String locale) {
        super(halo);
        AssertionUtils.notNull(contentSearchRepository, "contentSearchRepository");
        AssertionUtils.notNull(contentSyncRepository, "contentSyncRepository");
        mContentSearchRepository = contentSearchRepository;
        mContentSyncRepository = contentSyncRepository;
        mLocale = locale;
        SyncQuery.create("myModuleId", Threading.POOL_QUEUE_POLICY);
    }

    /**
     * Creates the internal search repository.
     *
     * @param halo    The halo instance.
     * @param storage The storage instance.
     * @return The content search repository created.
     */
    @NonNull
    private static ContentSearchRepository createContentSearchRepository(@NonNull Halo halo, @NonNull HaloStorageApi storage) {
        return new ContentSearchRepository(
                new ContentSearchRemoteDatasource(halo.framework().network()),
                new ContentSearchLocalDatasource(storage)
        );
    }

    /**
     * Create Halo annotated classes tables
     *
     * @param halo                  The halo instance.
     * @param generatedHaloDatabase The generated model database
     */
    @NonNull
    private static void createAutoGeneratedTables(@NonNull final Halo halo, @NonNull final GeneratedHaloDatabase generatedHaloDatabase) {
        AssertionUtils.notNull(generatedHaloDatabase, "generatedHaloDatabase");
        halo.framework().toolbox().queue().enqueue(Threading.SINGLE_QUEUE_POLICY, new Runnable() {
            @Override
            public void run() {
                //create database tables from codegen
                generatedHaloDatabase.updateDatabaseWithAutoGeneratedModels(halo.framework().storage(HaloContentContract.HALO_CONTENT_STORAGE).db().getDatabase());
            }
        });
    }

    /**
     * Creates the internal sync repository.
     *
     * @param halo    The halo instance.
     * @param storage The storage instance.
     * @return The content sync repository.
     */
    @NonNull
    private static ContentSyncRepository createContentSyncRepository(@NonNull Halo halo, @NonNull HaloStorageApi storage) {
        return new ContentSyncRepository(
                new ContentSyncRemoteDatasource(halo.framework().network()),
                new ContentSyncLocalDatasource(storage)
        );
    }

    /**
     * Creates the content api instance with the reference to a yet created HALO
     * instance. This factory uses no default locale. Refer to {@link #with(Halo, String)}
     * to provide a default locale.
     * <p>
     * You must keep this instance as singleton or as local, since
     * its creation should not be so expensive. Anyway we recommend to keep one
     * and use it across the whole app injecting it whether it is useful.
     *
     * @param halo The halo instance.
     * @return A new created content instance.
     */
    @Keep
    @Api(2.0)
    @NonNull
    public static HaloContentApi with(@NonNull Halo halo) {
        return with(halo, null);
    }

    /**
     * Creates the content api instance with the reference to a yet created HALO
     * instance. This factory uses a default locale that can also be null to avoid
     * using one by default.
     * <p>
     * You must keep this instance as singleton or as local, since
     * its creation should not be so expensive. Anyway we recommend to keep one
     * and use it across the whole app injecting it whether it is useful.
     *
     * @param halo   The halo instance.
     * @param locale The locale that will be used in all the requests where locale param is
     *               available. If the request asks for another locale this other locale
     *               will be used. None if it is null.
     * @return A new created content instance.
     */
    @Keep
    @NonNull
    @Api(2.0)
    public static HaloContentApi with(@NonNull Halo halo, @Nullable @HaloLocale.LocaleDefinition String locale) {
        HaloStorageApi storage = createStorage(halo);
        return new HaloContentApi(halo,
                createContentSearchRepository(halo, storage),
                createContentSyncRepository(halo, storage),
                locale);
    }

    /**
     * Creates the content api instance with the reference to a yet created HALO
     * instance. This factory uses a default locale that can also be null to avoid
     * using one by default.
     * <p>
     * You must set a generated class if you are using the halo annotations in your models.
     * <p>
     * You must keep this instance as singleton or as local, since
     * its creation should not be so expensive. Anyway we recommend to keep one
     * and use it across the whole app injecting it whether it is useful.
     *
     * @param halo                  The halo instance.
     * @param locale                The locale that will be used in all the requests where locale param is
     *                              available. If the request asks for another locale this other locale
     *                              will be used. None if it is null.
     * @param generatedHaloDatabase The halo generated database by codegen.
     * @return A new created content instance.
     */
    @Keep
    @NonNull
    @Api(2.3)
    public static HaloContentApi with(@NonNull Halo halo, @Nullable @HaloLocale.LocaleDefinition String locale, @NonNull GeneratedHaloDatabase generatedHaloDatabase) {
        HaloStorageApi storage = createStorage(halo);
        createAutoGeneratedTables(halo, generatedHaloDatabase);
        return new HaloContentApi(halo,
                createContentSearchRepository(halo, storage),
                createContentSyncRepository(halo, storage),
                locale);
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
                .storageName(HaloContentContract.HALO_CONTENT_STORAGE)
                .databaseVersion(HaloContentContract.CURRENT_VERSION)
                .errorHandler(new HaloDatabaseErrorHandler())
                .addMigrations(
                        new HaloContentMigration2$0$0())
                .build()
        );
    }

    /**
     * Performs the query to retrieve data from the backend. You can specify many different params
     * to ensure the backend provides you with all the instances you expect. There is also
     * available a helper class {@link com.mobgen.halo.android.content.search.SearchQueryBuilderFactory}
     * which contains some of the typical queries. Refer the following example
     * for a call to this method:
     * <pre><code>
     * //Create the api instance
     * HaloContentApi api = HaloContentApi.with(halo);
     *
     * //The query to retrieve the content
     * SearchQuery query = SearchQuery.builder()
     *      .moduleName("myModule")
     *      .beginSearch()
     *          .eq("name", "sample")
     *      .end()
     *      .build();
     *
     * //Perform the search
     * api.search(Data.NETWORK_AND_STORAGE, query)
     *      .asContent(MyClass.class)
     *      .execute(callback);
     * </code></pre>
     *
     * @param mode  The mode used to execute that query. Possible values:
     *              <ul>
     *              <li>NETWORK_AND_STORAGE</li>
     *              <li>NETWORK_ONLY</li>
     *              <li>STORAGE_ONLY</li>
     *              </ul>
     * @param query The query done with {@link SearchQuery}.
     * @return The wrapper that contains the action that will take place and allows the user
     * to select the way to retrieve the data. It allows it as {@link HaloContentInstance}, as
     * a Cursor or as a custom class implementation. This class needs to be
     * annotated with LoganSquare annotations.
     * See <a href="https://github.com/bluelinelabs/LoganSquare">LoganSquare</a> documentation
     * for more information on how to custom parse data.
     */
    @Keep
    @NonNull
    @Api(2.0)
    @CheckResult(suggest = "You may want to call asContent(), asContent(.class) or asRaw() to get the information")
    public HaloContentSelectorFactory<Paginated<HaloContentInstance>, Cursor> search(@Data.Policy int mode, @NonNull final SearchQuery query) {
        AssertionUtils.notNull(query, "query");
        if (query.getLocale() == null) {
            query.setLocale(mLocale);
        }
        return new HaloContentSelectorFactory<>(
                halo(),
                new SearchInteractor(mContentSearchRepository, query),
                new Cursor2ContentInstanceSearchConverter(query, halo().framework().parser(), halo().framework().storage(HaloContentContract.HALO_CONTENT_STORAGE)),
                new Cursor2ClassSearchConverterFactory(halo().framework().parser()),
                new InteractorExecutionCallback() {
                    @Override
                    public void onPreExecute() {
                        if (query.isSegmentedWithDevice()) {
                            query.setDevice(halo().manager().getDevice());
                        }
                    }
                },
                mode,
                "search");
    }


    /**
     * Clears all the search instances cached.
     *
     * @return The executor to run the clear.
     */
    @Keep
    @Api(2.0)
    @NonNull
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<Void> clearSearchInstances() {
        return new HaloInteractorExecutor<>(
                halo(),
                "Clear search instances",
                new ClearSearchInstancesInteractor(mContentSearchRepository)
        );
    }

    /**
     * Subscribes to the sync process from whenever part in the app
     * and allows you to receive events once a sync process finishes. As
     * a result of the event you receive a {@link HaloSyncLog} with the number
     * of insertions, deletions and updates as well as the status of this sync.
     * With this log you can request the data changed again to display it or
     * not to do anything if there were no changes.
     *
     * @param listener   The subscriber that will be add to the subscription.
     * @param moduleName The module you are subscribing to.
     * @return Provides a subscription that can be used to release any reference by calling
     * {@link ISubscription#unsubscribe()}.
     */
    @Keep
    @NonNull
    @Api(2.0)
    @CheckResult(suggest = "You should keep a reference to the subscription to call unsubscribe when you are done.")
    public ISubscription subscribeToSync(@NonNull String moduleName, @NonNull final HaloSyncListener listener) {
        AssertionUtils.notNull(moduleName, "moduleName");
        AssertionUtils.notNull(listener, "listener");
        return framework().subscribe(new Subscriber() {
            @Override
            public void onEventReceived(@NonNull Event event) {
                if (event.getData() != null) {
                    Pair<HaloStatus, HaloSyncLog> result = ModuleSyncHelper.debundleizeSync(event.getData());
                    listener.onSyncFinished(result.first, result.second);
                }
            }
        }, EventId.create(SYNC_FINISHED_EVENT + moduleName));
    }

    /**
     * Syncs a module using the id and an optional locale. It also allows you to specify where this
     * synchronization will be executed in terms of threading. You can use {@link #subscribeToSync(String, HaloSyncListener)}
     * to listen for the sync result and also {@link #getSyncInstances(String)} to retrieve the synced
     * instances for the given module.
     * <p>
     * Every sync process generates a log, the ones that can be accessed via {@link #getSyncLog(String)}.
     *
     * @param syncQuery Options for the synchronization.
     * @param immediate True if you want to force it now even if there is no network connection. This will allow
     *                  you to receive a response as soon as possible. Otherwise this sync will be cached
     *                  until you have a strong connection to perform it.
     */
    @Keep
    @Api(2.0)
    public void sync(@NonNull final SyncQuery syncQuery, boolean immediate) {
        AssertionUtils.notNull(syncQuery.getModuleName(), "moduleName");
        //Sets the default locale
        if (syncQuery.getLocale() == null) {
            syncQuery.setLocale(mLocale);
        }
        Job.Builder job = Job.builder(new SyncModuleSchedule(halo(), mContentSyncRepository, syncQuery))
                .persist(!immediate)
                .thread(syncQuery.getThreadingMode());
        if (immediate) {
            job.needsNetwork(Job.NETWORK_TYPE_ANY);
        }
        halo().framework().toolbox().schedule(job.build());
    }

    /**
     * Provides the synchronization log for the given module or the full log in case of the module
     * param is null. With this logs you can check how the sync where going and also
     * report bugs that may clarify more information related to a failed sync process.
     *
     * @param moduleName The module name to filter the logs or null if you want to get all
     *                   the sync log instances.
     * @return The selection factory. This selection allows you to get the logs as a cursor to
     * avoid waisting so much memory with a parsing that may not be needed. Use asRaw or asContent
     * to get the proper data type needed.
     */
    @Keep
    @NonNull
    @Api(2.0)
    @CheckResult(suggest = "You may want to call asContent() or asRaw() to get the information")
    public HaloSelectorFactory<List<HaloSyncLog>, Cursor> getSyncLog(@Nullable String moduleName) {
        return new HaloSelectorFactory<>(
                halo(),
                SyncDataProviders.syncLogInteractor(mContentSyncRepository, moduleName),
                new Cursor2SyncLogListConverter(),
                null,
                Data.STORAGE_ONLY,
                "getSyncLog"
        );
    }

    /**
     * Provides the instances available in local for the module provided. The module
     * must be provided. Those instances can be requested as {@link HaloContentInstance}, as Cursor
     * or as a custom class. Look at the following snippet for more details:
     * <pre><code>
     *     api.getSyncInstances("myModule")
     *          .asContent(MyCustomClass.class)
     *          .execute(callback);
     * </code></pre>
     *
     * @param moduleName The module name to request.
     * @return The wrapper for the action that allows to request the content in different ways.
     */
    @Keep
    @NonNull
    @Api(2.0)
    @CheckResult(suggest = "You may want to call asContent(), asContent(.class) or asRaw() to get the information")
    public HaloContentSelectorFactory<List<HaloContentInstance>, Cursor> getSyncInstances(@NonNull String moduleName) {
        AssertionUtils.notNull(moduleName, "moduleName");
        return new HaloContentSelectorFactory<>(
                halo(),
                SyncDataProviders.syncedInstancesInteractor(mContentSyncRepository, moduleName),
                new Cursor2ContentInstanceSyncConverter(),
                new Cursor2ClassSyncConverterFactory(halo().framework().parser()),
                null,
                Data.STORAGE_ONLY,
                "getSyncedInstances in module " + moduleName
        );
    }

    /**
     * Provides the action to clear all the instances for a given module. This action
     * can be configured in a thread and the result in the callback contains
     * no data but the status of the removal. Check <code>result.status()</code> if you
     * want to know if it is in a valid state.
     *
     * @param moduleName The module name to clear.
     * @return The selector.
     */
    @Keep
    @NonNull
    @Api(2.0)
    @CheckResult(suggest = "You may want to call execute() to run the task")
    public HaloInteractorExecutor<Void> clearSyncInstances(@NonNull String moduleName) {
        AssertionUtils.notNull(moduleName, "moduleName");
        return new HaloInteractorExecutor<>(
                halo(),
                "clearSyncInstances for " + moduleName,
                new SelectorRaw2Unparse<>(
                        SyncDataProviders.clearSyncedInstancesInteractor(mContentSyncRepository, moduleName),
                        Data.STORAGE_ONLY)
        );
    }

    /**
     * The synchronization process listener that listens for the event received
     * when a sync process finishes.
     */
    @Keep
    public interface HaloSyncListener {

        /**
         * Notifies when the sync process has finished so the user can perform any action.
         *
         * @param status The status of the data received.
         * @param log    The log of the synchronization.
         */
        @Keep
        @Api(2.0)
        void onSyncFinished(@NonNull HaloStatus status, @Nullable HaloSyncLog log);
    }
}
