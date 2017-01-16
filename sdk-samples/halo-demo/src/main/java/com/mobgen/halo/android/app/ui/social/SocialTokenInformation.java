package com.mobgen.halo.android.app.ui.social;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.framework.common.exceptions.HaloParsingException;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.sdk.core.management.models.Token;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SocialTokenInformation extends MobgenHaloActivity implements  View.OnClickListener{

    /**
     * The context
     */
    private Context mContext;
    /**
     * The expiration date.
     */
    private TextView expirationDate;
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
        expirationDate = (TextView) findViewById(R.id.tv_hated);
        facebookToken = (TextView) findViewById(R.id.tv_sft);
        googleToken = (TextView) findViewById(R.id.tv_sgt);

        mAccountManager = AccountManager.get(mContext);
        Account account = getAccount();
        if(account!=null) {
            String haloTokenStored = getSocialToken(account, "halo");
            String googleTokenStored = getSocialToken(account, "google");
            String facebookTokenStored = getSocialToken(account, "facebook");

            if (haloTokenStored != null) {
                try {
                    Token storedToken = Token.deserialize(haloTokenStored, Halo.instance().framework().parser());
                    long expiresIn = storedToken.getReceivedDate().getTime() + storedToken.getExpiresIn();
                    Date expireDate = new Date(expiresIn);
                    haloToken.setText(storedToken.getAccessToken());
                    expirationDate.setText("Expires in: "+ expireDate.toGMTString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
            if(accounts.size()>0) {
                return accounts.get(0);
            }else {
                return null;
            }

        } catch (SecurityException securityException){
            return null;
        }

    }

    private String getSocialToken(Account account, String accountType){
        return mAccountManager.getUserData(account, accountType);
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }

}
