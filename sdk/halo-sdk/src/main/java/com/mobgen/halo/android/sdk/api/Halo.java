package com.mobgen.halo.android.sdk.api;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.mobgen.halo.android.framework.api.HaloConfig;
import com.mobgen.halo.android.framework.api.HaloFramework;
import com.mobgen.halo.android.framework.common.annotations.Api;
import com.mobgen.halo.android.framework.common.exceptions.HaloConfigurationException;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.common.utils.AssertionUtils;
import com.mobgen.halo.android.framework.network.client.endpoint.HaloEndpoint;
import com.mobgen.halo.android.framework.network.sessions.HaloSessionManager;
import com.mobgen.halo.android.sdk.core.HaloCore;
import com.mobgen.halo.android.sdk.core.internal.network.HaloNetworkConstants;
import com.mobgen.halo.android.sdk.core.internal.parser.LoganSquareParserFactory;
import com.mobgen.halo.android.sdk.core.internal.startup.ReadyChecker;
import com.mobgen.halo.android.sdk.core.internal.startup.StartupManager;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.HaloReadyListener;
import com.mobgen.halo.android.sdk.core.internal.startup.processes.StartupProcess;
import com.mobgen.halo.android.sdk.core.internal.startup.processes.SyncDeviceStartupProcess;
import com.mobgen.halo.android.sdk.core.internal.startup.processes.VersionCheckStartupProcess;
import com.mobgen.halo.android.sdk.core.management.HaloManagerApi;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;
import com.mobgen.halo.android.sdk.core.management.segmentation.DefaultCollectorFactory;
import com.mobgen.halo.android.sdk.core.management.segmentation.TagCollector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;


/**
 * This is the entry point to act against the HALO SDK. This sdk allows many operations against the
 * HALO backend.
 */
@Keep
public class Halo {

    /**
     * Instance of halo.
     */
    private static Halo sHalo;
    /**
     * The framework instance.
     */
    private HaloFramework mFramework;
    /**
     * The core.
     */
    private HaloCore mCore;
    /**
     * Ready checker.
     */
    private ReadyChecker mReadyChecker;

    /**
     * Creates the halo instance with all its possibilities.
     *
     * @param configurationBuilder The configuration builder for the framework.
     * @param sessionManager       The session manager.
     * @param credentials          The credentials to authenticate. It will take the default ones
     *                             provided in the gradle plugin configuration if available.
     * @param tagCollectors        The tag collectors to provide information of the current device.
     * @param processes            The startup processes that will be run during the installation of halo.
     */
    Halo(@NonNull HaloConfig.Builder configurationBuilder,
         @NonNull HaloSessionManager sessionManager,
         @Nullable Credentials credentials,
         @Nullable List<TagCollector> tagCollectors,
         @Nullable StartupProcess[] processes) {
        AssertionUtils.notNull(configurationBuilder, "configurationBuilder");
        AssertionUtils.notNull(sessionManager, "sessionManager");

        //Check only one instance
        if (isInitialized()) {
            throw new HaloConfigurationException("You can only have one instance of Halo at the same time. Call uninstall in that one to create another one.");
        }

        //Assign current instance
        sHalo = this;

        //Create the core
        mFramework = HaloFramework.create(configurationBuilder);
        mCore = new HaloCore(mFramework,
                HaloManagerApi.with(this),
                sessionManager,
                credentials,
                tagCollectors);

        //clear log file to store logs if its in print log mode
        Halog.setupPrintLogToFile(mFramework);

        //Print the halo logo
        printHaloFootPrint();

        //Startup initialization
        StartupManager startupManager = new StartupManager(this);
        mReadyChecker = new ReadyChecker(startupManager);
        startupManager.execute(processes);
    }

    /**
     * Prints in the log the halo footprint.
     */
    /***
     *    __/\\\________/\\\______/\\\\\\\\\______/\\\____________________/\\\\\______
     *     _\/\\\_______\/\\\____/\\\\\\\\\\\\\___\/\\\__________________/\\\///\\\____
     *      _\/\\\_______\/\\\___/\\\/////////\\\__\/\\\________________/\\\/__\///\\\__
     *       _\/\\\\\\\\\\\\\\\__\/\\\_______\/\\\__\/\\\_______________/\\\______\//\\\_
     *        _\/\\\/////////\\\__\/\\\\\\\\\\\\\\\__\/\\\______________\/\\\_______\/\\\_
     *         _\/\\\_______\/\\\__\/\\\/////////\\\__\/\\\______________\//\\\______/\\\__
     *          _\/\\\_______\/\\\__\/\\\_______\/\\\__\/\\\_______________\///\\\__/\\\____
     *           _\/\\\_______\/\\\__\/\\\_______\/\\\__\/\\\\\\\\\\\\\\\_____\///\\\\\/_____
     *            _\///________\///___\///________\///___\///////////////________\/////_______
     */
    private void printHaloFootPrint() {
        String footprint = "\n /************************************ HALO IS STARTING ************************************/\n" +
                " /*    __/\\\\\\________/\\\\\\______/\\\\\\\\\\\\\\\\\\______/\\\\\\____________________/\\\\\\\\\\______        */\n" +
                " /*     _\\/\\\\\\_______\\/\\\\\\____/\\\\\\\\\\\\\\\\\\\\\\\\\\___\\/\\\\\\__________________/\\\\\\///\\\\\\____       */\n" +
                " /*      _\\/\\\\\\_______\\/\\\\\\___/\\\\\\/////////\\\\\\__\\/\\\\\\________________/\\\\\\/__\\///\\\\\\__      */\n" +
                " /*       _\\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\__\\/\\\\\\_______\\/\\\\\\__\\/\\\\\\_______________/\\\\\\______\\//\\\\\\_     */\n" +
                " /*        _\\/\\\\\\/////////\\\\\\__\\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\__\\/\\\\\\______________\\/\\\\\\_______\\/\\\\\\_    */\n" +
                " /*         _\\/\\\\\\_______\\/\\\\\\__\\/\\\\\\/////////\\\\\\__\\/\\\\\\______________\\//\\\\\\______/\\\\\\__   */\n" +
                " /*          _\\/\\\\\\_______\\/\\\\\\__\\/\\\\\\_______\\/\\\\\\__\\/\\\\\\_______________\\///\\\\\\__/\\\\\\____  */\n" +
                " /*           _\\/\\\\\\_______\\/\\\\\\__\\/\\\\\\_______\\/\\\\\\__\\/\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\_____\\///\\\\\\\\\\/_____ */\n" +
                " /*            _\\///________\\///___\\///________\\///___\\///////////////________\\/////_______*/\n" +
                " /******************************************************************************************/\n";
        Halog.d(getClass(),footprint);
    }

    /**
     * Ensures that the instance of halo is not uninstalled by checking the halo singleton.
     */
    private static void ensureExists() {
        if (sHalo == null) {
            throw new HaloConfigurationException("The halo instance is uninstalled. Please create another one.");
        }
    }

    /**
     * Uninstalls this instance of Halo making it unusable.
     */
    @Keep
    @Api(1.3)
    public void uninstall() {
        if (isInitialized()) {
            sHalo = null;
            mCore = null;
            mReadyChecker = null;
        }
    }

    /**
     * Provides the singleton instance of halo.
     *
     * @return The halo singleton instance.
     */
    @Keep
    @Api(1.0)
    @NonNull
    public static Halo instance() {
        ensureExists();
        return sHalo;
    }

    /**
     * Provides the core instance using the static instance of halo.
     *
     * @return The core instance.
     */
    @Keep
    @Api(1.3)
    @NonNull
    public static HaloCore core() {
        return instance().getCore();
    }

    /**
     * Tells if there is an instance of Halo.
     *
     * @return True if there is an instance. False otherwise.
     */
    @Keep
    @Api(1.0)
    public static boolean isInitialized() {
        return sHalo != null;
    }

    /**
     * The installer method that simplifies the process.
     *
     * @param context The context.
     * @return The installer instance.
     */
    @Keep
    @Api(1.0)
    @NonNull
    public static Installer installer(@NonNull Context context) {
        //Ensure we take the application instance, not any activity
        return new Installer(context);
    }

    /**
     * Checks if halo is ready or waits listening for it.
     *
     * @param listener The listener that checks if halo is ready.
     */
    @Keep
    @Api(2.0)
    public void ready(@NonNull final HaloReadyListener listener) {
        ensureExists();
        try {
            mReadyChecker.checkReady(listener);
        } catch (InterruptedException e) {
            Halog.e(getClass(), "A thread has been interrupted abruptly", e);
            listener.onHaloReady();
        }
    }

    /**
     * Provides the application context.
     *
     * @return The context.
     */
    @Keep
    @Api(1.0)
    @NonNull
    public Context context() {
        return framework().context();
    }

    /**
     * Provides the framework instance.
     *
     * @return The framework instance.
     */
    @Keep
    @NonNull
    @Api(1.1)
    public HaloFramework framework() {
        return mFramework;
    }

    /**
     * Provides the core instance.
     *
     * @return The core instance.
     */
    @Keep
    @Api(1.0)
    @NonNull
    public HaloCore getCore() {
        ensureExists();
        return mCore;
    }

    /**
     * Provides the manager Api.
     *
     * @return
     */
    @Keep
    @Api(2.0)
    @NonNull
    public HaloManagerApi manager() {
        return getCore().manager();
    }

    /**
     * Halo installer class to install the Halo instance and initialize all the data.
     */
    @Keep
    public static class Installer {

        /**
         * The configuration builder for the halo framework.
         */
        private HaloConfig.Builder mConfigurationBuilder;

        /**
         * Current credentials.
         */
        private Credentials mCredentials;

        /**
         * Tag collector to add some tags on startup.
         */
        private List<TagCollector> mTagCollectors;

        /**
         * The endpoint for halo.
         */
        private HaloEndpoint mEndpoint;

        /**
         * Final processes.
         */
        private StartupProcess[] mEndStartupProcesses;

        /**
         * Disables the pinning.
         */
        private boolean mDisablePinning;

        /**
         * Constructor for the installer.
         *
         * @param ctx The context of this installer.
         */
        @Keep
        Installer(@NonNull Context ctx) {
            //Add a cache with the needed size
            int memClass = ((ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
            int cacheSize = 1024 * 1024 * memClass / 8;
            long timeout = 1;
            File cacheDir = new File(ctx.getApplicationInfo().dataDir, "cache");

            //This is using the default builder that will be used for normal execution
            mConfigurationBuilder = HaloConfig.builder(ctx)
                    .setParser(LoganSquareParserFactory.create())
                    .setOkClient(new OkHttpClient.Builder()
                            .cache(new Cache(cacheDir, cacheSize))
                            .connectTimeout(timeout, TimeUnit.MINUTES)
                            .readTimeout(timeout, TimeUnit.MINUTES)
                            .writeTimeout(timeout, TimeUnit.MINUTES));

            mTagCollectors = new ArrayList<>(1);
            environment(HaloNetworkConstants.HALO_PROD_ENDPOINT_URL);
        }

        /**
         * The builder component that created this installer.
         *
         * @return The builder component.
         */
        @Api(2.0)
        @NonNull
        public HaloConfig.Builder config() {
            return mConfigurationBuilder;
        }

        /**
         * Sets the client credentials to allow authentication of a HALO application.
         *
         * @param credentials The credentials.
         * @return The current installer.
         */
        @Api(2.0)
        @NonNull
        public Installer credentials(@NonNull Credentials credentials) {
            mCredentials = credentials;
            return this;
        }

        /**
         * Enables or disables the debug state. It allows the framework to select which is the correct development
         * value.
         *
         * @param enabled True to enable it, false otherwise.
         * @return The current installer.
         */
        @Api(2.0)
        @NonNull
        public Installer debug(Boolean enabled) {
            mConfigurationBuilder.setDebug(enabled);
            return this;
        }

        /**
         * Set the print to file policy.
         * @param printToFilePolicy The policy to store files.
         * @return The current installer.
         */
        @Api(2.2)
        @NonNull
        public Installer printLogToFile(int printToFilePolicy) {
            mConfigurationBuilder.printToFilePolicy(printToFilePolicy);
            return this;
        }

        /**
         * Sets the processes that will be executed before any other action related to
         * HALO.
         *
         * @param processes The processes.
         * @return The installer.
         */
        @Api(2.0)
        @NonNull
        public Installer endProcesses(@Nullable StartupProcess... processes) {
            mEndStartupProcesses = processes;
            return this;
        }

        /**
         * Registers a tag to be collected on the application startup.
         *
         * @param tag The tag that will be sent.
         * @return The current installer.
         */
        @Api(2.0)
        @NonNull
        public Installer addTagCollector(@NonNull TagCollector tag) {
            mTagCollectors.add(tag);
            return this;
        }

        /**
         * Enables the default tags generated by halo. Now they are disabled by
         * default.
         *
         * @param isTestingDevice Tells if it is a testing device.
         * @return The current installer.
         */
        @Api(2.0)
        @NonNull
        public Installer enableDefaultTags(boolean isTestingDevice) {
            mTagCollectors.addAll(DefaultCollectorFactory.getDefaultTags(isTestingDevice));
            return this;
        }

        /**
         * Sets the endpoint for halo.
         *
         * @param endpoint The endpoint.
         * @return The current installer.
         */
        @NonNull
        @Api(2.0)
        public Installer environment(@NonNull String endpoint) {
            environment(endpoint,
                    HaloNetworkConstants.HALO_SHA_PINNING,
                    HaloNetworkConstants.HALO_SHA_PINNING_CERT2017);
            return this;
        }

        /**
         * Sets the endpoint for halo.
         *
         * @param endpoint The endpoint.
         * @param shaPin   The pinning for sha.
         * @return The current installer.
         */
        @NonNull
        @Api(2.0)
        public Installer environment(@NonNull String endpoint, @NonNull String... shaPin) {
            mEndpoint = new HaloEndpoint(HaloNetworkConstants.HALO_ENDPOINT_ID, endpoint, shaPin);
            return this;
        }

        /**
         * Disables the ssl pinning in the HALO SDK.
         * @return The current installer.
         */
        @Api(2.0)
        @NonNull
        public Installer disablePinning(){
            mDisablePinning = true;
            return this;
        }

        /**
         * Builds the instance of HALO.
         *
         * @return The instance of HALO.
         */
        @Api(1.0)
        @NonNull
        public Halo install() {
            if(mDisablePinning){
                mEndpoint.disablePinning();
            }

            //Add the final endpoint to halo
            mConfigurationBuilder.addEndpoint(mEndpoint);

            //Create the startup processes
            StartupProcess[] processes = new StartupProcess[]{
                    new VersionCheckStartupProcess(),
                    new SyncDeviceStartupProcess()
            };

            //Final processes to execute
            List<StartupProcess> finalProcesses = new ArrayList<>();
            finalProcesses.addAll(Arrays.asList(processes));
            if (mEndStartupProcesses != null) {
                finalProcesses.addAll(Arrays.asList(mEndStartupProcesses));
            }

            return install(mConfigurationBuilder,
                    new HaloSessionManager(),
                    mCredentials,
                    mTagCollectors,
                    finalProcesses.toArray(new StartupProcess[finalProcesses.size()]));
        }

        /**
         * Installs the halo framework with custom parameters.
         *
         * @param configurationBuilder The configuration builder.
         * @param credentials          The credentials.
         * @param tagCollectors        The tag collectors.
         * @param processes            The processes to execute in the beginning.
         * @return The halo instance.
         */
        @NonNull
        @Api(2.0)
        public Halo install(@NonNull HaloConfig.Builder configurationBuilder,
                            @NonNull HaloSessionManager sessionManager,
                            @Nullable Credentials credentials,
                            @Nullable List<TagCollector> tagCollectors,
                            @Nullable StartupProcess[] processes) {
            return new Halo(configurationBuilder,
                    sessionManager,
                    credentials,
                    tagCollectors,
                    processes);
        }
    }
}
