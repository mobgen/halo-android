package com.mobgen.halo.android.app.ui.social;


import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.social.HaloSocialApi;
import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.social.models.HaloUserProfile;

import java.util.Arrays;
import java.util.List;

public class SocialTokenInformation extends MobgenHaloActivity implements  View.OnClickListener{

    /**
     * The context
     */
    private Context mContext;
    /**
     * Halo sign in.
     */
    private TextView haloToken;
    /**
     * Facebook token.
     */
    private TextView facebookToken;
    /**
     * Google token
     */
    private TextView googleToken;
    /**
     *
     * Account manager
     */
    private AccountManager mAccountManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().clearFocus();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_token);
        mContext = this;

        haloToken = (TextView) findViewById(R.id.tv_sht);
        facebookToken = (TextView) findViewById(R.id.tv_sft);
        googleToken = (TextView) findViewById(R.id.tv_sgt);

        mAccountManager = AccountManager.get(mContext);
        Account account = getAccount();
        if(account!=null) {
            String haloTokenStored = getSocialToken(account, "halo");
            String googleTokenStored = getSocialToken(account, "google");
            String facebookTokenStored = getSocialToken(account, "facebook");

            if (haloTokenStored != null) {
                haloToken.setText(haloTokenStored);
            }
            if (facebookTokenStored != null) {
                facebookToken.setText(facebookTokenStored);
            }
            if (googleTokenStored != null) {
                googleToken.setText(googleTokenStored);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        haloToken.setOnClickListener(this);
        facebookToken.setOnClickListener(this);
        googleToken.setOnClickListener(this);
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.social_halo_token_title);
    }

    @Override
    public void onClick(View v) {
        ClipboardManager cm = (ClipboardManager)mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        if (v.getId() == R.id.tv_sht) {
            cm.setText(haloToken.getText());
        } else if(v.getId() == R.id.tv_sft) {
            cm.setText(facebookToken.getText());
        } else if(v.getId() == R.id.tv_sgt) {
            cm.setText(googleToken.getText());
        }
        Toast.makeText(mContext, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    private Account getAccount() {
        try {
            List<Account> accounts = Arrays.asList(mAccountManager.getAccountsByType("halo.account.demoapp"));
            return accounts.get(0);
        } catch (SecurityException securityException){
            return null;
        }

    }

    private String getSocialToken(Account account, String accountType){
        return mAccountManager.getUserData(account, accountType);
    }

}
