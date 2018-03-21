package com.mobgen.halo.android.app.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.facebook.stetho.Stetho;
import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.FontAwesomeModule;
import com.mobgen.halo.android.app.BuildConfig;
import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.generated.GeneratedDatabaseFromModel;
import com.mobgen.halo.android.app.model.chat.ChatMessage;
import com.mobgen.halo.android.app.module.ConfigurationModule;
import com.mobgen.halo.android.app.notifications.DeeplinkDecorator;
import com.mobgen.halo.android.app.notifications.SilentNotificationDispatcher;
import com.mobgen.halo.android.app.ui.settings.SettingsActivity;
import com.mobgen.halo.android.auth.HaloAuthApi;
import com.mobgen.halo.android.content.HaloContentApi;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.framework.common.helpers.logger.PrintLog;
import com.mobgen.halo.android.framework.common.helpers.subscription.ISubscription;
import com.mobgen.halo.android.notifications.HaloNotificationsApi;
import com.mobgen.halo.android.notifications.callbacks.HaloNotificationEventListener;
import com.mobgen.halo.android.notifications.models.HaloPushEvent;
import com.mobgen.halo.android.notifications.services.NotificationIdGenerator;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloApplication;
import com.mobgen.halo.android.sdk.core.internal.storage.HaloManagerContract;
import com.mobgen.halo.android.sdk.core.management.segmentation.HaloLocale;
import com.mobgen.halo.android.translations.HaloTranslationsApi;
import com.mobgen.halo.android.twofactor.HaloTwoFactorApi;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The halo application that contains the Halo initialization and other framework initializes just to make it easy to
 * debug and check.
 */
public class MobgenHaloApplication extends HaloApplication {


    /**
     * Annotation name for the environment definition.
     */
    @IntDef({CUSTOM, LOCAL, QA, INT, STAGE, PROD, UAT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Environment {
    }

    /**
     * Local environment definition.
     */
    public static final int CUSTOM = 0;

    /**
     * Local environment definition.
     */
    public static final int LOCAL = 1;

    /**
     * QA environment definition.
     */
    public static final int QA = 2;
    /**
     * Int environment definition.
     */
    public static final int INT = 3;
    /**
     * Stage environment definition.
     */
    public static final int STAGE = 4;
    /**
     * Production environment definition.
     */
    public static final int PROD = 5;
    /**
     * UAT environment definition.
     */
    public static final int UAT = 6;

    /**
     * Translations api.
     */
    private static HaloTranslationsApi mTranslationsApi;
    /**
     * Notifications api.
     */
    private static HaloNotificationsApi mNotificationsApi;
    /**
     * Social api.
     */
    private static HaloAuthApi mAuthApi;
    /**
     * Silent listen notifications
     */
    private static ISubscription mSilentHaloNotificationListener;
    /**
     * Two factor authentication api
     */
    private static HaloTwoFactorApi mTwoFactorApi;

    @Override
    public void onCreate() {

        super.onCreate();

        //Icons
        Iconify.with(new FontAwesomeModule());

        //strict vm policy
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedClosableObjects()
                .detectFileUriExposure()
                .detectLeakedSqlLiteObjects()
                .detectActivityLeaks()
                .detectLeakedRegistrationObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());

        if (BuildConfig.BUILD_TYPE.contains("debug")) {
            //Facebook stetho
            Stetho.initialize(Stetho.newInitializerBuilder(this)
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                    .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                    .build());
        }

        //Crittercism
        if (BuildConfig.BUILD_TYPE.equals("release")) {
            Crittercism.initialize(this, "55a613ba4be3830b003a2650");
        }
    }

    /**
     * Creates a halo installer based on the preferences flag.
     *
     * @param context     The application context.
     * @param environment The environment for halo.
     * @return The installer created.
     */
    @NonNull
    public static Halo.Installer createHaloInstaller(Context context, @Environment int environment) {
        String environmentUrl;
        switch (environment) {
            case CUSTOM:
                SharedPreferences preferences = context.getSharedPreferences(HaloManagerContract.HALO_MANAGER_STORAGE, MODE_PRIVATE);
                environmentUrl = preferences.getString(SettingsActivity.PREFERENCES_HALO_ENVIRONMENT_CUSTOM_URL, "https://halo-int.mobgen.com");
                break;
            case LOCAL:
                environmentUrl = "http://halo-local.mobgen.com";
                break;
            case STAGE:
                environmentUrl = "https://halo-stage.mobgen.com";
                break;
            case INT:
                environmentUrl = "https://halo-int.mobgen.com";
                break;
            case QA:
                environmentUrl = "https://halo-qa.mobgen.com";
                break;
            case UAT:
                environmentUrl = "https://halo-uat.mobgen.com";
                break;
            case PROD:
            default: //Default environment is QA
                return Halo.installer(context);
        }
        return Halo.installer(context).environment(environmentUrl);
    }

    @SuppressWarnings("all")
    @Override
    public Halo.Installer onCreateInstaller() {
        return createHaloInstaller(this, getEnvironment());
    }

    private int getEnvironment() {
        SharedPreferences preferences = getSharedPreferences(HaloManagerContract.HALO_MANAGER_STORAGE, MODE_PRIVATE);
        Integer env;
        try {
            env = preferences.getInt(SettingsActivity.PREFERENCES_HALO_ENVIRONMENT, PROD);
        } catch (ClassCastException e) {
            preferences.edit().remove(SettingsActivity.PREFERENCES_HALO_ENVIRONMENT).apply();
            env = PROD;
        }
        return env;
    }

    /**
     * Lets configure the instance of HALO for our application.
     *
     * @param installer The installer.
     * @return The installer configured.
     */
    @NonNull
    @Override
    public Halo.Installer beforeInstallHalo(@NonNull Halo.Installer installer) {
        //TODO silent notifications
        return installer
                .debug(BuildConfig.DEBUG)
                .printLogToFile(PrintLog.SINGLE_FILE_POLICY)
                .enableServiceOnBoot()
                .channelServiceNotification("My awesome name", R.drawable.myicon)
                .enableDefaultTags(BuildConfig.BUILD_TYPE.equals("debug"))
                .endProcesses(new ConfigurationModule());
    }

    @NonNull
    @Override
    public Halo onHaloCreated(@NonNull final Halo halo) {
        //translations
        if (mTranslationsApi != null) {
            mTranslationsApi.cancel();
        }
        mTranslationsApi = HaloTranslationsApi.with(halo)
                .locale(HaloLocale.ENGLISH_UNITED_STATES)
                .keyValue("key", "value")
                .moduleName("Demo translations")
                .defaultText("No translation found")
                .defaultLoadingText("" +
                        "Translating...")
                .provideDefaultOnAsync(true)
                .build();
        mTranslationsApi.load();
        //social
        if (mAuthApi != null) {
            mAuthApi.release();
            mAuthApi = null;
        }
        mAuthApi = HaloAuthApi.with(halo)
                .recoveryPolicy(HaloAuthApi.RECOVERY_ALWAYS)
                .storeCredentials("halo.account.demoapp")
                .withHalo()
                .withFacebook()
                .withGoogle()
                .build();
        //Generated content api
        HaloContentApi.with(MobgenHaloApplication.halo(), null, new GeneratedDatabaseFromModel());
        //notifications
        if (mNotificationsApi != null) {
            mNotificationsApi.release();
            mNotificationsApi = null;
            mSilentHaloNotificationListener.unsubscribe();
            mSilentHaloNotificationListener = null;
        }
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel("NOTIFICATION_CHANNEL_ID", "My awesome channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setShowBadge(false);
            mNotificationsApi = HaloNotificationsApi.with(halo).notificationChannel(channel);
        } else {
            mNotificationsApi = HaloNotificationsApi.with(halo);
        }
        mNotificationsApi.enablePushEvents(new HaloNotificationEventListener() {
            @Override
            public void onEventReceived(@Nullable HaloPushEvent haloPushEvent) {
                if (haloPushEvent != null) {
                    Toast.makeText(halo.context(), "The push notification action was: " + haloPushEvent.getAction(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mSilentHaloNotificationListener = mNotificationsApi.listenSilentNotifications(new SilentNotificationDispatcher());
        mNotificationsApi.setNotificationDecorator(new DeeplinkDecorator(this));
        mNotificationsApi.customIdGenerator(new NotificationIdGenerator() {
            @Override
            public int getNextNotificationId(@NonNull Bundle data, int currentId) {
                Object custom = data.get("custom");
                if (custom != null) {
                    try {
                        ChatMessage chatMessage = ChatMessage.deserialize(custom.toString(), Halo.instance().framework().parser());
                        if (chatMessage.getMessage() != null) {
                            return chatMessage.getAlias().hashCode();
                        } else {
                            return currentId;
                        }
                    } catch (HaloParsingException e) {
                        return currentId;
                    }
                } else {
                    return currentId;
                }
            }
        });

        if (mTwoFactorApi != null) {
            mTwoFactorApi.release();
            mTwoFactorApi = null;
        }
        mTwoFactorApi = HaloTwoFactorApi.with(halo)
                .smsProvider("6505551212")
                .withNotifications(mNotificationsApi)
                .withSMS()
                .build();

        return super.onHaloCreated(halo);
    }

    public static HaloTranslationsApi getTranslationsApi() {
        return mTranslationsApi;
    }

    public static HaloAuthApi getHaloAuthApi() {
        return mAuthApi;
    }

    public static HaloTwoFactorApi getTwoFactorApi() {
        return mTwoFactorApi;
    }
}
