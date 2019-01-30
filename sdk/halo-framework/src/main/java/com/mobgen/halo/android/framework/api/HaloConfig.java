package com.mobgen.halo.android.framework.api;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.helpers.builder.IBuilder;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.helpers.logger.PrintLog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.endpoint.HaloEndpoint;
import com.mobgen.halo.android.framework.network.client.endpoint.HaloEndpointCluster;
import com.mobgen.halo.android.framework.network.client.response.Parser;
import com.mobgen.halo.android.framework.storage.preference.HaloPreferencesStorage;
import com.mobgen.halo.android.framework.toolbox.bus.EventBus;
import com.mobgen.halo.android.framework.toolbox.bus.HaloEventBus;
import com.mobgen.halo.android.framework.toolbox.scheduler.PersistReceiver;
import com.mobgen.halo.android.framework.toolbox.threading.DefaultThreadManager;
import com.mobgen.halo.android.framework.toolbox.threading.HaloThreadManager;
import com.mobgen.halo.android.framework.toolbox.scheduler.HaloJobScheduler;

import okhttp3.OkHttpClient;

import static com.mobgen.halo.android.framework.api.StorageConfig.DEFAULT_STORAGE_NAME;

/**
 * HALO Framework configuration.
 */
public class HaloConfig {

    /**
     * Name to store the channel notification service.
     */
    public static String SERVICE_NOTIFICATION_CHANNEL = "SERVICE_NOTIFICATION_CHANNEL";

    /**
     * Name to store the icon of the notificaion when service startup
     */
    public static String SERVICE_NOTIFICATION_ICON = "SERVICE_NOTIFICATION_ICON";

    /**
     * The builder for the configuration.
     */
    @NonNull
    private final Builder mBuilder;

    /**
     * Builder to construct it.
     *
     * @param builder The builder.
     */
    private HaloConfig(@NonNull Builder builder) {
        mBuilder = builder;
    }

    /**
     * Provides the context.
     *
     * @return The application context.
     */
    @Api(1.1)
    @NonNull
    public Context getContext() {
        return mBuilder.mContext;
    }

    /**
     * Provides the toolbox threadPolicy.
     *
     * @return The synchronization threadPolicy.
     */
    @Api(1.1)
    @NonNull
    public HaloThreadManager getSyncQueue() {
        return mBuilder.mThreadManager;
    }

    /**
     * Provides the event hub for the framework.
     *
     * @return The event hub.
     */
    @Api(1.3)
    @NonNull
    public EventBus getEventHub() {
        return mBuilder.mEventHub;
    }

    /**
     * Provides the instance of the job scheduler.
     *
     * @return The job scheduler.
     */
    @Api(2.0)
    @NonNull
    public HaloJobScheduler jobScheduler() {
        return mBuilder.mJobScheduler;
    }

    /**
     * Provides the endpoint cluster.
     *
     * @return The endpoint cluster.
     */
    @Api(1.1)
    @NonNull
    public HaloEndpointCluster getEndpointCluster() {
        return mBuilder.mNetworkEndpointCluster;
    }

    /**
     * Provides the okhttp client builder.
     *
     * @return The okhttp client builder.
     */
    @Api(1.1)
    @NonNull
    public OkHttpClient.Builder getOkHttpBuilder() {
        return mBuilder.mOkHttpClientBuilder;
    }

    /**
     * Provides the network response parser.
     *
     * @return The response parser.
     */
    @Api(2.0)
    @NonNull
    public Parser.Factory getParser() {
        return mBuilder.mParser;
    }

    /**
     * Provides the debug flag.
     *
     * @return True if it is debug, false otherwise.
     */
    @Api(1.1)
    public boolean getIsDebug() {
        return mBuilder.mIsDebug;
    }

    /**
     * Provides the KitKat certificate enabling flag.
     *
     * @return True if wants to disable, false otherwise.
     */
    @Api(2.7)
    public boolean getDisableKitKatCertificate() {
        return mBuilder.mDisableKitKatCertificate;
    }

    /**
     * Provides the print log to file policy.
     *
     * @return The policy value.
     */
    @Api(2.2)
    public int printToFilePolicy() {
        return mBuilder.mPrintPolicy;
    }

    /**
     * Get the channel notification name.
     *
     * @return The channel name.
     */
    @NonNull
    @Api(2.4)
    public String notificationChannelName() {
        return mBuilder.notificationChannelName;
    }

    /**
     * Creates a new configuration with the given context.
     *
     * @param context The context.
     * @return The configuration created.
     */
    @Api(1.1)
    @NonNull
    public static HaloConfig.Builder builder(@NonNull Context context) {
        return new Builder(context);
    }

    /**
     * The builder class for the configuration.
     */
    public static class Builder implements IBuilder<HaloConfig> {

        /**
         * The application context.
         */
        @NonNull
        private Context mContext;
        /**
         * Sets the debug flag on the framework.
         */
        private boolean mIsDebug;
        /**
         * Sets the KitKat certificate addition flag on the framework.
         */
        private boolean mDisableKitKatCertificate;
        /**
         * The print log to file policy
         */
        private int mPrintPolicy;
        /**
         * The response parser.
         */
        private Parser.Factory mParser;

        //SYNC
        /**
         * The synchronization thread manager.
         */
        private HaloThreadManager mThreadManager;
        /**
         * The event hub.
         */
        private EventBus mEventHub;
        /**
         * The job scheduler.
         */
        private HaloJobScheduler mJobScheduler;

        //NETWORK_ONLY
        /**
         * The endpoint cluster that contains all the endpoint ids.
         */
        private HaloEndpointCluster mNetworkEndpointCluster;
        /**
         * The ok http client builder for the framework.
         */
        private OkHttpClient.Builder mOkHttpClientBuilder;

        /**
         * Handle if a service should launchg after device boot
         */
        private boolean shouldLaunchService = false;

        /**
         * Channel notification name for foreground services.
         */
        private String notificationChannelName = "Foreground service";

        /**
         * The notification icon.
         */
        @DrawableRes
        private Integer notificationIcon;

        /**
         * Constructor for the builder that takes the context.
         *
         * @param context The context.
         */
        private Builder(@NonNull Context context) {
            AssertionUtils.notNull(context, "context");
            mContext = context.getApplicationContext();
            mNetworkEndpointCluster = new HaloEndpointCluster();
        }

        /**
         * Sets the debug flag.
         *
         * @param debug The debug flag.
         * @return The current builder.
         */
        @Api(1.1)
        @NonNull
        public Builder setDebug(boolean debug) {
            mIsDebug = debug;
            return this;
        }

        /**
         * Sets the KitKat certificate flag.
         *
         * @param disableCertificate The certificate flag.
         * @return The current builder.
         */
        @Api(2.7)
        @NonNull
        public Builder setDisableKitKatCertificate(boolean disableCertificate) {
            mDisableKitKatCertificate = disableCertificate;
            return this;
        }

        /**
         * Sets the print to file policy.
         *
         * @param printPolicy The print to file policy.
         * @return The current builder.
         */
        @Api(2.2)
        @NonNull
        public Builder printToFilePolicy(@PrintLog.Policy int printPolicy) {
            mPrintPolicy = printPolicy;
            return this;
        }

        /**
         * Sets the current toolbox threadPolicy that will be used by the synchronization api.
         *
         * @param syncQueue The toolbox threadPolicy.
         * @return The current builder.
         */
        @Api(1.1)
        @NonNull
        public Builder threadManager(@Nullable HaloThreadManager syncQueue) {
            mThreadManager = syncQueue;
            return this;
        }

        /**
         * Overrides the job scheduler.
         *
         * @param jobScheduler The job scheduler.
         * @return The current builder.
         */
        @Api(2.0)
        @NonNull
        public Builder jobScheduler(@NonNull HaloJobScheduler jobScheduler) {
            mJobScheduler = jobScheduler;
            return this;
        }

        /**
         * Adds an endpoint to the framework.
         *
         * @param endpoint The endpoint to add.
         * @return The current builder.
         */
        @Api(1.1)
        @NonNull
        public Builder addEndpoint(@NonNull HaloEndpoint endpoint) {
            mNetworkEndpointCluster.registerEndpoint(endpoint);
            return this;
        }

        /**
         * Sets the okhttp client.
         *
         * @param okhttp The client.
         * @return The current builder.
         */
        @Api(1.1)
        @NonNull
        public Builder setOkClient(@Nullable OkHttpClient.Builder okhttp) {
            mOkHttpClientBuilder = okhttp;
            return this;
        }

        /**
         * Sets the response parser.
         *
         * @param parserFactory The json parser.
         * @return The builder.
         */
        @Api(2.0)
        @NonNull
        public Builder setParser(@NonNull Parser.Factory parserFactory) {
            mParser = parserFactory;
            return this;
        }

        /**
         * Enable the service startup on boot
         *
         * @return The builder.
         */
        @Api(2.5)
        @NonNull
        @TargetApi(26)
        public Builder enableServiceOnBoot() {
            shouldLaunchService = true;
            return this;
        }

        /**
         * Set the notification channel name.
         *
         * @param channelName The channel name
         * @param icon        The channel name icon
         * @return The builder.
         */
        @Api(2.5)
        @NonNull
        @TargetApi(26)
        public Builder channelServiceNotification(@NonNull String channelName, @DrawableRes int icon) {
            notificationChannelName = channelName;
            notificationIcon = icon;
            return this;
        }

        /**
         * This method disables/enable the Broadcast Boot Receiver registered in the AndroidManifest file.
         *
         * @param state The state of the component service
         */
        private void serviceManifestComponentState(boolean state) {
            ComponentName receiver = new ComponentName(mContext, PersistReceiver.class);
            PackageManager pm = mContext.getPackageManager();
            int newState = (state ?
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
            pm.setComponentEnabledSetting(receiver, newState,
                    PackageManager.DONT_KILL_APP);
        }

        @NonNull
        @Override
        public HaloConfig build() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //disable service on boot
                serviceManifestComponentState(shouldLaunchService);

                //save notification service channel
                HaloPreferencesStorage preferences = new HaloPreferencesStorage(mContext, DEFAULT_STORAGE_NAME);
                preferences.edit().putString(SERVICE_NOTIFICATION_CHANNEL, notificationChannelName).commit();
                if (notificationIcon != null) {
                    preferences.edit().putInt(SERVICE_NOTIFICATION_ICON, notificationIcon).commit();
                }
            }

            //Networking
            if (mOkHttpClientBuilder == null) {
                mOkHttpClientBuilder = new OkHttpClient.Builder();
            }

            //Sync
            if (mThreadManager == null) {
                mThreadManager = new DefaultThreadManager();
            }
            if (mEventHub == null) {
                mEventHub = HaloEventBus.create(mContext);
            }
            if (mJobScheduler == null) {
                mJobScheduler = new HaloJobScheduler(mContext, mThreadManager);
            }

            //Warn in case of no parser created
            if (mParser == null) {
                Halog.w(getClass(), "There is not parser instance available. Make sure you setup it using the parser(Factory) call.");
            }
            return new HaloConfig(this);
        }
    }
}
