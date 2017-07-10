package com.mobgen.locationpoc.ui;

import android.content.Context;
import android.support.annotation.NonNull;

import com.mobgen.locationpoc.BuildConfig;
import com.mobgen.halo.android.auth.HaloAuthApi;
import com.mobgen.halo.android.auth.models.HaloAuthProfile;
import com.mobgen.halo.android.auth.models.IdentifiedUser;
import com.mobgen.halo.android.auth.providers.SocialNotAvailableException;
import com.mobgen.halo.android.framework.common.helpers.logger.PrintLog;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloApplication;
import com.mobgen.halo.android.sdk.core.management.HaloManagerApi;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;


/**
 * The halo application that contains the Halo initialization and other framework initializes just to make it easy to
 * debug and check.
 */
public class MobgenHaloApplication extends HaloApplication {

    private static HaloAuthApi mAuthApi;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Creates a halo installer based on the preferences flag.
     *
     * @param context     The application context.
     * @return The installer created.
     */
    @NonNull
    public static Halo.Installer createHaloInstaller(Context context) {
        String environmentUrl = "https://halo-new-int.mobgen.com";
        return Halo.installer(context).environment(environmentUrl);
    }

    @SuppressWarnings("all")
    @Override
    public Halo.Installer onCreateInstaller() {
        return createHaloInstaller(this);
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
        return installer
                .debug(BuildConfig.DEBUG)
                .printLogToFile(PrintLog.SINGLE_FILE_POLICY)
                .enableDefaultTags(BuildConfig.BUILD_TYPE.equals("debug"));
    }

    @NonNull
    @Override
    public Halo onHaloCreated(@NonNull Halo halo) {

         mAuthApi = HaloAuthApi.with(halo)
                .recoveryPolicy(HaloAuthApi.RECOVERY_ALWAYS)
                .storeCredentials("halo.account.demoloc")
                .withHalo()
                .build();

        return super.onHaloCreated(halo);
    }

    public static HaloAuthApi getAuth(){
        return mAuthApi;
    }


}
