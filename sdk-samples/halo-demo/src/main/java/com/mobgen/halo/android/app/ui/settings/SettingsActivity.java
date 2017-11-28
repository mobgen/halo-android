package com.mobgen.halo.android.app.ui.settings;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mobgen.halo.android.app.BuildConfig;
import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.modules.partial.ModulesActivity;
import com.mobgen.halo.android.app.ui.views.HaloTextView;
import com.mobgen.halo.android.content.spec.HaloContentContract;
import com.mobgen.halo.android.framework.api.HaloStorageApi;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.api.HaloApplication;
import com.mobgen.halo.android.sdk.core.management.models.Credentials;
import com.mobgen.halo.android.sdk.core.management.models.Device;
import com.mobgen.halo.android.translations.spec.HaloTranslationsContract;

import java.io.IOException;

/**
 * The settings activity for halo.
 */
public class SettingsActivity extends MobgenHaloActivity implements View.OnClickListener {

    /**
     * Preferences name for this the current halo environment.
     */
    public static final String PREFERENCES_HALO_ENVIRONMENT = "environment";

    /**
     * Preferences name for this the current halo environment.
     */
    public static final String PREFERENCES_HALO_ENVIRONMENT_CUSTOM_URL = "custom_environment_url";

    private SettingsViewHolder mViewHolder;

    private AlertDialog mPreviousDialog;

    private TextView userAlias;

    private TextView notificationToken;

    private Context mContext;

    /**
     * Starts this activity.
     *
     * @param context The context to start this activity.
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_environment);

        mContext = this;

        mViewHolder = new SettingsViewHolder(getWindow().getDecorView());

        userAlias = (TextView) findViewById(R.id.tv_user_alias);
        notificationToken = (TextView) findViewById(R.id.tv_gcm);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        userAlias.setOnClickListener(this);
        notificationToken.setOnClickListener(this);
    }

    @Override
    public void onPresenterInitialized() {
        super.onPresenterInitialized();
        //The String for the environment
        final String[] environmentNames = new String[]{
                getString(R.string.custom_environment),
                getString(R.string.local_environment),
                getString(R.string.qa_environment),
                getString(R.string.int_environment),
                getString(R.string.stage_environment),
                getString(R.string.production_environment),
                getString(R.string.uat_environment)};

        //Set the HALO env text
        final HaloStorageApi storage = Halo.instance().getCore().manager().storage();
        final HaloStorageApi storageContent = Halo.instance().framework().storage(HaloContentContract.HALO_CONTENT_STORAGE);
        final HaloStorageApi storageTranslation = Halo.instance().framework().storage(HaloTranslationsContract.HALO_TRANSLATIONS_STORAGE);
        @MobgenHaloApplication.Environment final Integer env = storage.prefs().getInteger("environment", MobgenHaloApplication.PROD);
        if (env != null && env < environmentNames.length) {
            mViewHolder.mCurrentEnvironment.setText(environmentNames[env]);
        }

        //Reload HALO with the dialog
        mViewHolder.mEnvironmentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(SettingsActivity.this, android.R.layout.select_dialog_singlechoice, environmentNames);
                int selectedItem = env != null ? env : 0;
                View headerView = getLayoutInflater().inflate(R.layout.dialog_prefrences_title, null);
                final HaloTextView hintTextCustomUrl = (HaloTextView) headerView.findViewById(R.id.tv_custom_server_hint);
                final EditText customUrl = (EditText) headerView.findViewById(R.id.et_custom_server);
                customUrl.setText(storage.prefs().getString(PREFERENCES_HALO_ENVIRONMENT_CUSTOM_URL, customUrl.getText().toString()));
                customUrl.setVisibility(View.GONE);
                hintTextCustomUrl.setVisibility(View.GONE);
                mPreviousDialog = new AlertDialog.Builder(SettingsActivity.this)
                        .setSingleChoiceItems(arrayAdapter, selectedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, @MobgenHaloApplication.Environment int env1) {
                                storage.prefs().edit().clear().apply();
                                if (hintTextCustomUrl.getVisibility() == View.GONE && env1 == MobgenHaloApplication.CUSTOM) {
                                    customUrl.setVisibility(View.VISIBLE);
                                    hintTextCustomUrl.setVisibility(View.VISIBLE);
                                    mPreviousDialog.getListView().setItemChecked(0, false);
                                } else {
                                    if (env1 == MobgenHaloApplication.CUSTOM) {
                                        storage.prefs().edit().putString(PREFERENCES_HALO_ENVIRONMENT_CUSTOM_URL, customUrl.getText().toString()).commit();
                                    }

                                    if (hintTextCustomUrl.getVisibility() == View.GONE) {
                                        customUrl.setVisibility(View.GONE);
                                        hintTextCustomUrl.setVisibility(View.GONE);
                                    }

                                    storage.prefs().edit().putInt(PREFERENCES_HALO_ENVIRONMENT, env1).commit();
                                    //Remove the databases for the current environment
                                    storage.db().deleteDatabase();
                                    if (storageContent != null) {
                                        storageContent.db().deleteDatabase();
                                        storageContent.prefs().edit().clear().commit();
                                    }
                                    if (storageTranslation != null) {
                                        storageTranslation.db().deleteDatabase();
                                        storageTranslation.prefs().edit().clear().commit();
                                    }
                                    //delete instance on firebase afte changing environment
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                FirebaseInstanceId.getInstance().deleteInstanceId();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                    //uninstall halo
                                    Halo.instance().uninstall();
                                    ((HaloApplication) getApplication()).installHalo();
                                    ModulesActivity.start(SettingsActivity.this, true);
                                }
                            }
                        })
                        //.setTitle(getString(R.string.environment_select))
                        .setCustomTitle(headerView)
                        .setCancelable(true)
                        .show();
                mPreviousDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                mPreviousDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        });

        mViewHolder.mDbContainer.setVisibility(View.VISIBLE);
        mViewHolder.mDeleteDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (storage != null) {
                    int currentEnv = storage.prefs().getInteger(PREFERENCES_HALO_ENVIRONMENT, MobgenHaloApplication.PROD);
                    String customUrl = null;
                    if (currentEnv == MobgenHaloApplication.CUSTOM) {
                        customUrl = storage.prefs().getString(PREFERENCES_HALO_ENVIRONMENT_CUSTOM_URL, null);
                    }
                    storage.db().deleteDatabase();
                    storage.prefs().edit().clear().commit();
                    //restore current environment
                    storage.prefs().edit().putInt(PREFERENCES_HALO_ENVIRONMENT, currentEnv).commit();
                    if (customUrl != null) {
                        storage.prefs().edit().putString(PREFERENCES_HALO_ENVIRONMENT_CUSTOM_URL, customUrl).commit();
                    }
                }
                if (storageContent != null) {
                    storageContent.db().deleteDatabase();
                    storageContent.prefs().edit().clear().commit();
                }
                if (storageTranslation != null) {
                    storageTranslation.db().deleteDatabase();
                    storageTranslation.prefs().edit().clear().commit();
                }

            }
        });

        setApplicationVars();
    }

    private void setApplicationVars() {
        Credentials credentials = Halo.core().credentials();
        mViewHolder.mClientId.setText(credentials.getUsername());
        Device device = Halo.core().manager().getDevice();
        if (device != null) {
            mViewHolder.mUserAlias.setText(device.getAlias());
            mViewHolder.mNotificationToken.setText(device.getNotificationsToken());
        }
        try {
            mViewHolder.mApplicationVersion.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Halog.e(getClass(), "Error while bringing the application version name");
        }
        mViewHolder.mBambooBuild.setText(BuildConfig.BAMBOO_BUILD != null ? BuildConfig.BAMBOO_BUILD.toString() : "IDE");

    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.settings_title);
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPreviousDialog != null) {
            mPreviousDialog.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        if (v.getId() == R.id.tv_gcm) {
            cm.setText(notificationToken.getText());
        } else if (v.getId() == R.id.tv_user_alias) {
            cm.setText(userAlias.getText());
        }
        Toast.makeText(mContext, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private static class SettingsViewHolder {
        private View mEnvironmentContainer;
        private TextView mCurrentEnvironment;
        private TextView mClientId;
        private TextView mUserAlias;
        private TextView mApplicationVersion;
        private TextView mBambooBuild;
        private TextView mNotificationToken;
        private View mDeleteDatabaseButton;
        private View mDbContainer;

        public SettingsViewHolder(View container) {
            mEnvironmentContainer = container.findViewById(R.id.ll_environment_container);
            mCurrentEnvironment = (TextView) container.findViewById(R.id.tv_environment);
            mClientId = (TextView) container.findViewById(R.id.tv_client_id);
            mUserAlias = (TextView) container.findViewById(R.id.tv_user_alias);
            mApplicationVersion = (TextView) container.findViewById(R.id.tv_version);
            mBambooBuild = (TextView) container.findViewById(R.id.tv_bamboo_build);
            mNotificationToken = (TextView) container.findViewById(R.id.tv_gcm);
            mDeleteDatabaseButton = container.findViewById(R.id.bt_delete_db);
            mDbContainer = container.findViewById(R.id.ll_database_container);
        }
    }

}
