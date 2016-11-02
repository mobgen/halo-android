package com.mobgen.halo.android.app.ui.modules.partial;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.MockAppConfiguration;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;

/**
 * This activity contains and displays in a left panel menu all the modules active for the current
 * user in Halo.
 */
public class ModulesActivity extends MobgenHaloActivity {

    /**
     * Compatibility toggle.
     */
    private ActionBarDrawerToggle mToggle;

    /**
     * The navigation drawer.
     */
    private NavigationView mNavigationDrawer;

    public static void start(Context context, boolean clearTop) {
        Intent intent = new Intent(context, ModulesActivity.class);
        if (clearTop) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        context.startActivity(intent);
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, ModulesActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modules);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.dl_modules);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") View titleContainer = inflater.inflate(R.layout.view_toolbar_title, null);
        ((TextView) titleContainer.findViewById(R.id.tv_title)).setText(R.string.app_name);
        getSupportActionBar().setCustomView(titleContainer,
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );
        mToggle = new ActionBarDrawerToggle(this, drawer, 0, 0);
        mNavigationDrawer = (NavigationView) findViewById(R.id.nv_drawer);
        drawer.addDrawerListener(mToggle);
        mToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void applyConfig(MockAppConfiguration config) {
        super.applyConfig(config);
        if (!TextUtils.isEmpty(config.getMenuColor())) {
            mNavigationDrawer.setBackgroundColor(HaloUtils.getArgb(config.getMenuColor()));
        }
    }
}
