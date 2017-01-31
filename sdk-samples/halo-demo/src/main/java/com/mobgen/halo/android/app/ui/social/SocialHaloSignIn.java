package com.mobgen.halo.android.app.ui.social;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mobgen.halo.android.app.R;
import com.mobgen.halo.android.app.ui.MobgenHaloActivity;
import com.mobgen.halo.android.app.ui.MobgenHaloApplication;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.halo.android.auth.HaloAuthApi;
import com.mobgen.halo.android.auth.models.HaloAuthProfile;
import com.mobgen.halo.android.auth.models.HaloUserProfile;

public class SocialHaloSignIn extends MobgenHaloActivity implements View.OnClickListener {

    /**
     * The context
     */
    private Context mContext;
    /**
     * The auth api instance.
     */
    private HaloAuthApi mAuthApi;
    /**
     * Halo sign in.
     */
    private Button mSignInWithHalo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_halo_sigin);
        mContext = this;
        mAuthApi = MobgenHaloApplication.getHaloAuthApi();
        mSignInWithHalo = (Button) findViewById(R.id.halo_sign_in);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSignInWithHalo.setOnClickListener(this);
    }

    @Override
    public String getToolbarTitle() {
        return getString(R.string.social_halo_signin_title);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.halo_sign_in) {
            EditText editName = (EditText) findViewById(R.id.edit_signin_name);
            EditText editSurname = (EditText) findViewById(R.id.edit_signin_surname);
            EditText editEmail = (EditText) findViewById(R.id.edit_signin_email);
            EditText editPassword = (EditText) findViewById(R.id.edit_signin_password);
            String displayName = "";
            if (!TextUtils.isEmpty(editName.getText().toString()) && !TextUtils.isEmpty(editSurname.getText().toString())) {
                displayName = editName.getText().toString() + " " + editSurname.getText().toString();
            }

            HaloAuthProfile authProfile = new HaloAuthProfile(editEmail.getText().toString().trim(), editPassword.getText().toString().trim());
            HaloUserProfile userProfile = new HaloUserProfile(null, displayName, editName.getText().toString().trim(), editSurname.getText().toString().trim(), "https://bytebucket.org/mobgen/halo-android/wiki/images/halo.png?rev=b6f0d10e1e474dbbbf4d04d3420b2a18da17e37c", editEmail.getText().toString().trim());
            mAuthApi.register(authProfile, userProfile)
                    .execute(new CallbackV2<HaloUserProfile>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<HaloUserProfile> result) {
                            if (result.status().isOk()) { // Ok
                                Snackbar.make(getWindow().getDecorView(), result.data().getEmail(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    @Override
    public boolean hasBackNavigationToolbar() {
        return true;
    }
}
