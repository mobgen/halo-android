package com.mobgen.halo.android.app.ui.social;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.SignInButton;
import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;

/**
 * Social login activity to login with different accounts.
 */
public class SocialLoginActivity extends MobgenHaloActivity implements View.OnClickListener{//, Callback<HaloSocialProfile, Void> {

    /**
     * The social api instance.
     */
   // private HaloSocialApi mSocialApi;

    /**
     * Google sign in.
     */
    private SignInButton mSignInWithGoogle;
    /**
     * Button to sign in.
     */
    private Button mSignInWithFacebook;

    /**
     * Starts the activity.
     *
     * @param context The context to start this activity.
     */
    public static void startActivity(@NonNull Context context) {
        Intent intent = new Intent(context, SocialLoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_login);
       /* mSocialApi = HaloSocialApi.with(MobgenHaloApplication.halo())
                .withGoogle()
                .withFacebook()
                .build();*/
        mSignInWithGoogle = (SignInButton) findViewById(R.id.google_sign_in);
        mSignInWithFacebook = (Button) findViewById(R.id.facebook_sign_in);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSignInWithGoogle.setOnClickListener(this);
        mSignInWithFacebook.setOnClickListener(this);
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.social_login_title);
    }

    @Override
    public void onClick(View v) {
        /*if (v.getId() == R.id.google_sign_in) {
            try {
                mSocialApi.login(HaloSocialApi.SOCIAL_GOOGLE_PLUS, this);
            } catch (SocialNotAvailableException e) {
                Snackbar.make(getWindow().getDecorView(), getString(R.string.error_provider_not_available), Snackbar.LENGTH_LONG).show();
            }
        } else if(v.getId() == R.id.facebook_sign_in){
            try {
                mSocialApi.login(HaloSocialApi.SOCIAL_FACEBOOK, this);
            } catch (SocialNotAvailableException e) {
                Snackbar.make(getWindow().getDecorView(), getString(R.string.error_provider_not_available), Snackbar.LENGTH_LONG).show();
            }
        }*/
    }
/*
    @Override
    public void onFinish(@NonNull HaloResult<HaloSocialProfile, Void> result) {
        if(!result.status().isCanceled()) {
            if (result.status().isSuccess()) { // Success
                Halog.d(getClass(), result.data().toString());
            } else { // Error
                Halog.d(getClass(), result.status().exception().toString());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSocialApi.release();
    }

    */
}
