package com.mobgen.halo.android.framework.api;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.toolbox.bus.Event;
import com.mobgen.halo.android.framework.toolbox.bus.EventId;
import com.mobgen.halo.android.framework.toolbox.bus.Subscriber;
import com.mobgen.halo.android.framework.toolbox.scheduler.PersistReceiver;

import java.util.HashMap;
import java.util.Map;

/**
 * The configuration for the halo framework so it can be configured properly and easily.
 */
public class HaloFramework {

    /**
     * The context of the framework.
     */
    private final Context mContext;
    /**
     * The network api.
     */
    private final HaloNetworkApi mNetworkApi;
    /**
     * The storage apis.
     */
    private final Map<String, HaloStorageApi> mStorages;
    /**
     * The json parser for the framework.
     */
    private final Parser.Factory mJsonParser;
    /**
     * The synchronization api.
     */
    private final HaloToolboxApi mSyncApi;
    /**
     * Sets the debug flag on the framework.
     */
    private boolean mIsDebug;
    /**
     * Sets the print log policy
     */
    private int mPrintPolicy;

    /**
     * Configures the halo framework based on the configuration parameters.
     *
     * @param configuration The configuration parameters.
     */
    private HaloFramework(@NonNull HaloConfig configuration) {
        mContext = configuration.getContext();

        //Setup the debug flag
        setDebugFlag(configuration.getIsDebug());
        setPrintLogToFilePolicy(configuration.printToFilePolicy());
        mJsonParser = configuration.getParser();

        mNetworkApi = HaloNetworkApi.newNetworkApi(this, configuration);
        mSyncApi = HaloToolboxApi.newSyncApi(this, configuration);
        mStorages = new HashMap<>(1);
    }


    /**
     * This method enables the Broadcast receiver registered in the AndroidManifest file.
     */
    public void enableBroadcastReceiver() {
        ComponentName receiver = new ComponentName(mContext, PersistReceiver.class);
        PackageManager pm = mContext.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }

    /**
     * This method disables the Broadcast receiver registered in the AndroidManifest file.
     */
    public void disableBroadcastReceiver() {
        ComponentName receiver = new ComponentName(mContext, PersistReceiver.class);
        PackageManager pm = mContext.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

    }

    /**
     * Creates the framework instance with a default configuration.
     *
     * @param context The context.
     * @param parser  The parser factory.
     * @return The framework instance.
     */
    @Api(1.1)
    public static HaloFramework create(@NonNull Context context, @NonNull Parser.Factory parser) {
        return create(
                HaloConfig.builder(context)
                        .setParser(parser)
        );
    }

    /**
     * Creates the framework based on the configuration.
     *
     * @param configurationBuilder The configuration builder.
     * @return The framework instance.
     */
    @Api(1.1)
    public static HaloFramework create(@NonNull HaloConfig.Builder configurationBuilder) {
        return new HaloFramework(configurationBuilder.build());
    }

    /**
     * Creates a storages and caches it in the framework.
     *
     * @param config The configuration for the storage.
     * @return The created storage api.
     */
    @Api(2.0)
    @NonNull
    public HaloStorageApi createStorage(@NonNull StorageConfig config) {
        HaloStorageApi api;
        if (!mStorages.containsKey(config.storageName())) {
            api = HaloStorageApi.newStorageApi(this, config);
            mStorages.put(config.storageName(), api);
        } else {
            api = mStorages.get(config.storageName());
        }
        return api;
    }

    /**
     * The storage api.
     *
     * @param storageName The storage id.
     * @return The Storage api if exists.
     */
    @Api(2.0)
    public HaloStorageApi storage(@NonNull String storageName) {
        return mStorages.get(storageName);
    }

    /**
     * The networking api.
     *
     * @return The networking api.
     */
    @Api(1.1)
    @NonNull
    public HaloNetworkApi network() {
        return mNetworkApi;
    }

    /**
     * The synchronization api.
     *
     * @return The request api.
     */
    @Api(1.1)
    @NonNull
    public HaloToolboxApi toolbox() {
        return mSyncApi;
    }

    /**
     * Provides the framework context.
     *
     * @return The framework context.
     */
    @Api(1.1)
    @NonNull
    public Context context() {
        return mContext;
    }

    /**
     * Emits an event using the synchronization event hub.
     *
     * @param event The event to emit.
     */
    @Api(1.3)
    public void emit(@Nullable Event event) {
        if (event != null) {
            mSyncApi.eventHub().emit(event);
        }
    }

    /**
     * Subscribes to the event hub with the id and a subscriber.
     *
     * @param subscriber The subscriber to attach.
     * @param id         The id.
     * @return The subscription created.
     */
    @Api(1.3)
    @CheckResult(suggest = "Subscription.unsubscribe() to avoid memory leaks")
    public ISubscription subscribe(@NonNull Subscriber subscriber, @NonNull EventId id) {
        return mSyncApi.eventHub().subscribe(subscriber, id);
    }

    /**
     * Sets the debug flag on the framework.
     *
     * @param debug The debug flag.
     */
    @Api(1.1)
    public void setDebugFlag(boolean debug) {
        mIsDebug = debug;
        Halog.printDebug(mIsDebug);
    }

    /**
     * Provides if the framework is in debug executionMode.
     *
     * @return True if in debug executionMode, false otherwise.
     */
    @Api(1.1)
    public boolean isInDebugMode() {
        return mIsDebug;
    }

    /**
     * Sets the print policy flag on the framework.
     *
     * @param printPolicy The print to file policy
     */
    @Api(2.2)
    public void setPrintLogToFilePolicy(int printPolicy) {
        mPrintPolicy = printPolicy;
    }

    /**
     * Provides the print log to file policy
     *
     * @return The print to file policy
     */
    @Api(2.2)
    public int printToFilePolicy() {
        return mPrintPolicy;
    }


    /**
     * Provides the json parser configured for the framework.
     *
     * @return The json parser.
     */
    @Api(2.0)
    @NonNull
    public Parser.Factory parser() {
        return mJsonParser;
    }
}
