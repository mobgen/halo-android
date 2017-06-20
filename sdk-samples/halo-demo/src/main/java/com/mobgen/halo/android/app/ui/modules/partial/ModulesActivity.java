package com.mobgen.halo.android.app.ui.modules.partial;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.model.MockAppConfiguration;
import com.mobgen.halo.android.app.model.QROffer;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.app.ui.chat.ChatRoomActivity;
import com.mobgen.halo.android.app.ui.social.SocialLoginActivity;
import com.mobgen.halo.android.auth.models.Pocket;
import com.mobgen.halo.android.auth.models.ReferenceContainer;
import com.mobgen.halo.android.auth.pocket.HaloPocketApi;
import com.mobgen.halo.android.framework.common.utils.HaloUtils;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.HaloManagerApi;
import com.mobgen.halo.android.twofactor.callbacks.HaloTwoFactorAttemptListener;
import com.mobgen.halo.android.twofactor.models.TwoFactorCode;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    /**
     * The info dialog.
     */
    private AlertDialog mInfoDialog;

    /**
     * Request permission code.
     */
    private static final int REQUEST_PERMISSION = 11;

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

        //Show what it is needed
        if (!hasReadSMSPermissions()) {
            openNoPermissions();
        } else {
            setTwoFactorListener();
        }


        //check pocket api
        HaloPocketApi pocketApi = MobgenHaloApplication.getHaloAuthApi().pocket();
        QROffer qrOffer = new QROffer("12","My user",new Date(),"This is my contennt","htpp://google.com");
        List<String> myrefs = new ArrayList<>();
        myrefs.add("1");
        myrefs.add("2");
        ReferenceContainer referenceContainer = new ReferenceContainer("mycollection",myrefs);
        ReferenceContainer referenceContainer2 = new ReferenceContainer("mycollection222",myrefs);
        ReferenceContainer referenceContainer3 = new ReferenceContainer("mlol",myrefs);
        Pocket pocket = new Pocket.Builder()
                .withData(qrOffer)
                .withReferences(new ReferenceContainer[]{referenceContainer,referenceContainer2})
                .withReferences(referenceContainer3)
                .build();
        pocketApi.save(pocket)
                .execute(new CallbackV2<Pocket>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<Pocket> result) {
                        Log.v("my pocket","");
                        result.data().getValues(QROffer.class,Halo.instance().framework().parser());
                        result.data().getReferences();
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setTwoFactorListener();
                }
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        //TODO Delete this code when APP+ credential is ready to send push
        if(HaloManagerApi.with(MobgenHaloApplication.halo())
                .isPasswordAuthentication()) {
            Halo.instance().getCore().logout();
        }

        supportInvalidateOptionsMenu();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(MobgenHaloApplication.getHaloAuthApi()!=null && MobgenHaloApplication.getHaloAuthApi().isAccountStored()){
            getMenuInflater().inflate(R.menu.menu_modules_logout, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_modules_login, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_logout) {
            MobgenHaloApplication.getHaloAuthApi()
                    .logout()
                    .execute(new CallbackV2<Boolean>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<Boolean> result) {
                            if(result.data()){
                                createInfoDialog("See you soon ;)");
                                //remove the chat qr image
                                File chatImage =  new File(MobgenHaloApplication.halo().context().getExternalFilesDir(null).getAbsolutePath().toString() + "/qr/profile.jpg");
                                chatImage.delete();
                            } else {
                                createInfoDialog("You must signin or login");
                            }
                            supportInvalidateOptionsMenu();
                        }
                    });
            return true;
        } else if (item.getItemId() == R.id.menu_login) {
            SocialLoginActivity.startActivity(this);
            return true;
        } else if (item.getItemId() == R.id.menu_chatroom) {
            ChatRoomActivity.startActivity(this);
            return true;
        } else {
            return mToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
        }
    }

    /**
     * Creates the dialog
     */
    public void createInfoDialog(String message) {
        if (mInfoDialog != null) mInfoDialog.dismiss();
        mInfoDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.social_loout_title))
                .setMessage(message)
                .setPositiveButton(getString(R.string.social_loout_ok), null)
                .create();
        mInfoDialog.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mInfoDialog != null) {
            mInfoDialog.dismiss();
            mInfoDialog = null;
        }
    }

    @Override
    public void applyConfig(MockAppConfiguration config) {
        super.applyConfig(config);
        if (!TextUtils.isEmpty(config.getMenuColor())) {
            mNavigationDrawer.setBackgroundColor(HaloUtils.getArgb(config.getMenuColor()));
        }
    }

    private void setTwoFactorListener() {
        MobgenHaloApplication.getTwoFactorApi().listenTwoFactorAttempt(new HaloTwoFactorAttemptListener() {
            @Override
            public void onTwoFactorReceived(@NonNull TwoFactorCode twoFactorCode) {
                Toast.makeText(ModulesActivity.this, "This is the code received: " + twoFactorCode.getCode() + " from a: " + twoFactorCode.getIssuer(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Opens the dialog to request permissions
     */
    private void openNoPermissions() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, REQUEST_PERMISSION);
        }
    }

    /**
     * Determines if the application has enough permissions to read sms
     */
    private boolean hasReadSMSPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }
}
