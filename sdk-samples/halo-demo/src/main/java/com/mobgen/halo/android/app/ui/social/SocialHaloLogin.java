package com.mobgen.halo.android.app.ui.social;


import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.framework.common.helpers.logger.Halog;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.HaloSocialApi;
import com.mobgen.halo.android.social.models.HaloSocialProfile;
import com.mobgen.halo.android.social.providers.SocialNotAvailableException;
import com.mobgen.halo.android.social.models.HaloAuthProfile;

public class SocialHaloLogin extends MobgenHaloActivity implements View.OnClickListener, CallbackV2<HaloSocialProfile> {

    /**
     * The context
     *
     */
    private Context mContext;
    /**
     * The social api instance.
     */
    private HaloSocialApi mSocialApi;
    /**
     * Halo sign in.
     */
    private Button mLoginWithHalo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_halo_login);
        mContext=this;
        mSocialApi = MobgenHaloApplication.getHaloSocialApi();
        mLoginWithHalo = (Button)findViewById(R.id.halo_login);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mLoginWithHalo.setOnClickListener(this);
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.social_halo_login_title);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.halo_login) {
            final EditText editEmail = (EditText) findViewById(R.id.edit_login_email);
            final EditText editPassword = (EditText) findViewById(R.id.edit_login_password);
            try {
                final HaloAuthProfile authProfile = new HaloAuthProfile(editEmail.getText().toString().trim(),editPassword.getText().toString().trim(), mSocialApi.getCurrentAlias());
                mSocialApi.login(HaloSocialApi.SOCIAL_HALO, authProfile, this);
            } catch (SocialNotAvailableException e) {
                Snackbar.make(getWindow().getDecorView(), getString(R.string.error_provider_not_available), Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onFinish(@NonNull HaloResultV2<HaloSocialProfile> result) {
        if (result.status().isOk()) { // Ok
            Snackbar.make(getWindow().getDecorView(), result.data().name(), Snackbar.LENGTH_LONG).show();
        } else { // Error
            Halog.d(getClass(), result.status().exception().toString());
        }
    }
}
