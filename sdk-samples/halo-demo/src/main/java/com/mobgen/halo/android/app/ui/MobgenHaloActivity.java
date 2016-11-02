package com.mobgen.halo.android.app.ui;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.MockAppConfiguration;
import com.mobgen.halo.android.app.module.ConfigurationModule;
import com.mobgen.halo.android.app.notifications.ConfigurationNotificationReceiver;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.internal.startup.callbacks.HaloReadyListener;

/**
 * Base activity for the halo mock application.
 */
public abstract class MobgenHaloActivity extends AppCompatActivity implements ConfigurationNotificationReceiver.ConfigurationChangeListener {

    private TextView mToolbarTitle;

    private ConfigurationNotificationReceiver mConfigurationReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            if (hasBackNavigationToolbar()) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            LayoutInflater inflater = LayoutInflater.from(this);
            @SuppressLint("InflateParams") View titleContainer = inflater.inflate(R.layout.view_toolbar_title, null);
            mToolbarTitle = ((TextView) titleContainer.findViewById(R.id.tv_title));
            mToolbarTitle.setText(getToolbarTitle());
            getSupportActionBar().setCustomView(titleContainer,
                    new ActionBar.LayoutParams(
                            ActionBar.LayoutParams.WRAP_CONTENT,
                            ActionBar.LayoutParams.MATCH_PARENT,
                            Gravity.CENTER
                    )
            );
        }

        listenConfigurationChanges();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeConfigurationChangesListener();
    }

    private void listenConfigurationChanges() {
        mConfigurationReceiver = new ConfigurationNotificationReceiver(this, ConfigurationModule.instance());
        LocalBroadcastManager.getInstance(this).registerReceiver(mConfigurationReceiver, new IntentFilter("configuration"));
    }

    private void removeConfigurationChangesListener() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mConfigurationReceiver);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        onPresenterInitialized();
    }

    /**
     * Method used to update data. This is used to avoid problems when halo is not initialized.
     */
    @CallSuper
    public void onPresenterInitialized() {
        loadConfiguration();
    }

    private void loadConfiguration() {
        Halo.instance().ready(new HaloReadyListener() {
            @Override
            public void onHaloReady() {
                MockAppConfiguration configuration = ConfigurationModule.instance().getConfiguration();
                if(configuration!=null){
                    applyConfig(configuration);
                }
            }
        });
    }

    /**
     * Applies the configuration.
     *
     * @param config The configuration.
     */
    @CallSuper
    public void applyConfig(MockAppConfiguration config) {
        if (config != null) {
            if (!TextUtils.isEmpty(config.getToolbarColor())) {
                int toolbarColor = HaloUtils.getArgb(config.getToolbarColor());
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(toolbarColor));
                    if (HaloUtils.isAvailableForVersion(Build.VERSION_CODES.LOLLIPOP)) {
                        getWindow().setStatusBarColor(toolbarColor);
                    }
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadConfiguration();
    }

    /**
     * Provides the toolbar title. Override it to receive the toolbar title.
     *
     * @return The toolbar title.
     */
    public String getToolbarTitle() {
        return "";
    }

    /**
     * Determines if the toolbar has a back button.
     *
     * @return True if the navigation has a back button. False otherwise.
     */
    public boolean hasBackNavigationToolbar() {
        return false;
    }

    /**
     * Sets the toolbar title.
     *
     * @param toolbarTitle The toolbar title.
     */
    public void setToolbarTitle(String toolbarTitle) {
        if (mToolbarTitle != null) {
            mToolbarTitle.setText(toolbarTitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(MockAppConfiguration newConfiguration) {
        applyConfig(newConfiguration);
    }
}
