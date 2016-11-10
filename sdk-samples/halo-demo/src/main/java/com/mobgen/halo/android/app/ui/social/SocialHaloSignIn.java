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
import com.mobgen.halo.android.sdk.api.Halo;
import com.mobgen.halo.android.social.HaloSocialApi;
import com.mobgen.halo.android.social.models.HaloAuthProfile;
import com.mobgen.halo.android.social.models.HaloSocialProfile;
import com.mobgen.halo.android.social.models.IdentifiedUser;
import com.mobgen.halo.android.social.models.HaloUserProfile;

public class SocialHaloSignIn extends MobgenHaloActivity implements View.OnClickListener {

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
    private Button mSignInWithHalo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_halo_sigin);
        mContext=this;
        mSocialApi = MobgenHaloApplication.getHaloSocialApi();
        mSignInWithHalo = (Button)findViewById(R.id.halo_sign_in);
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
        if(v.getId() == R.id.halo_sign_in) {
            EditText editName = (EditText) findViewById(R.id.edit_signin_name);
            EditText editSurname = (EditText) findViewById(R.id.edit_signin_surname);
            EditText editEmail = (EditText) findViewById(R.id.edit_signin_email);
            EditText editPassword = (EditText) findViewById(R.id.edit_signin_password);
            String displayName = "";
            if(!TextUtils.isEmpty(editName.getText().toString()) && !TextUtils.isEmpty(editSurname.getText().toString())){
                displayName =  editName.getText().toString() +" "+editSurname.getText().toString();
            }

            final HaloAuthProfile authProfile = new HaloAuthProfile(editEmail.getText().toString().trim(),editPassword.getText().toString().trim(), Halo.instance().manager().getDevice().getAlias());
            HaloUserProfile userProfile = new HaloUserProfile(displayName,editName.getText().toString().trim(),editSurname.getText().toString().trim(),"http://yogasara.staff.gunadarma.ac.id/photo.jpg",editEmail.getText().toString().trim());
            mSocialApi.register(authProfile,userProfile)
                    .execute(new CallbackV2<HaloSocialProfile>() {
                        @Override
                        public void onFinish(@NonNull HaloResultV2<HaloSocialProfile> result) {
                            if (result.status().isOk()) { // Ok
                                Snackbar.make(getWindow().getDecorView(), result.data().email(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
