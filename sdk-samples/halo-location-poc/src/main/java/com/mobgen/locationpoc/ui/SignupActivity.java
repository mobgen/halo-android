package com.mobgen.locationpoc.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobgen.halo.android.auth.HaloAuthApi;
import com.mobgen.halo.android.auth.models.HaloAuthProfile;
import com.mobgen.halo.android.auth.models.HaloUserProfile;
import com.mobgen.halo.android.auth.models.IdentifiedUser;
import com.mobgen.halo.android.auth.providers.SocialNotAvailableException;
import com.mobgen.halo.android.framework.toolbox.data.CallbackV2;
import com.mobgen.halo.android.framework.toolbox.data.HaloResultV2;
import com.mobgen.locationpoc.R;

import java.util.Random;

/**
 * Created by f.souto.gonzalez on 07/06/2017.
 */

public class SignupActivity extends AppCompatActivity {

    public static final String USER_NAME = "userName";
    public static final String USER_MAIL = "userEmail";
    public static final String USER_PHOTO = "userPhoto";

    private EditText mEmailText;
    private EditText mNameText;
    private EditText mSurnameText;
    private EditText mPasswordText;
    private TextInputLayout mEmailLayout;
    private TextInputLayout mNameLayout;
    private TextInputLayout mSurNameLayout;
    private TextInputLayout mPasswordLayout;
    private Button mSignUpButton;
    private Context mContext;

    public static void start(@NonNull Context context) {
        Intent intent = new Intent(context, SignupActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mContext = this;

        mNameText = (EditText) findViewById(R.id.input_name);
        mSurnameText = (EditText) findViewById(R.id.input_surname);
        mEmailText = (EditText) findViewById(R.id.input_email);
        mPasswordText = (EditText) findViewById(R.id.input_password);

        mNameLayout = (TextInputLayout) findViewById(R.id.input_layout_name);
        mSurNameLayout = (TextInputLayout) findViewById(R.id.input_layout_surname);
        mEmailLayout = (TextInputLayout) findViewById(R.id.input_layout_email);
        mPasswordLayout = (TextInputLayout) findViewById(R.id.input_layout_password);

        mSignUpButton = (Button) findViewById(R.id.btn_signup);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    /**
     * Attempt to login
     */
    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        mSignUpButton.setEnabled(false);
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_PopupOverlay);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getString(R.string.login_attempt));
        progressDialog.show();

        final String email = mEmailText.getText().toString();
        final String password = mPasswordText.getText().toString();
        final String name = mNameText.getText().toString();
        String surname = mSurnameText.getText().toString();
        String displayName = name + " " + surname;
        //we use a random  photo to register user
        Random r = new Random();
        int low = 100;
        int high = 500;
        int randomID = r.nextInt(high - low) + low;
        final String photo = "https://unsplash.it/700/400?image=" + randomID;

        final HaloAuthProfile auth = new HaloAuthProfile(email, password);
        HaloUserProfile userProfile = new HaloUserProfile(null, displayName, name.trim(), surname.toString().trim(),
                photo, email.trim());
        MobgenHaloApplication.getAuth().register(auth, userProfile)
                .execute(new CallbackV2<HaloUserProfile>() {
                    @Override
                    public void onFinish(@NonNull HaloResultV2<HaloUserProfile> result) {
                        progressDialog.dismiss();
                        if (result.data() != null) {
                            //store user details
                            MobgenHaloApplication.halo()
                                    .getCore().manager().storage()
                                    .prefs()
                                    .edit()
                                    .putString(USER_MAIL, email)
                                    .putString(USER_NAME, name)
                                    .putString(USER_PHOTO, photo)
                                    .apply();
                            attemptLogin(auth);
                        } else {
                            onSignupFailed();
                        }
                    }
                });
    }


    private void attemptLogin(HaloAuthProfile user) {
        try {
            MobgenHaloApplication.getAuth().loginWithHalo(HaloAuthApi.SOCIAL_HALO, user, new CallbackV2<IdentifiedUser>() {
                @Override
                public void onFinish(@NonNull HaloResultV2<IdentifiedUser> result) {
                    if (result.data() != null) {
                        mSignUpButton.setEnabled(true);
                        HomeActivity.start(mContext);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), getString(R.string.signup_error_login), Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            });
        } catch (SocialNotAvailableException e) {
        }
    }

    /**
     * Error with a login attempt
     */
    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), getString(R.string.signup_error), Toast.LENGTH_LONG).show();
        mSignUpButton.setEnabled(true);
    }

    /**
     * Validate the fields of the form
     *
     * @return True if all its ok
     */
    public boolean validate() {
        boolean valid = true;

        String email = mEmailText.getText().toString();
        String password = mPasswordText.getText().toString();
        String name = mNameText.getText().toString();
        String surname = mSurnameText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mEmailText.setError(getString(R.string.login_validate_email_error));
            mEmailLayout.setErrorEnabled(true);
            valid = false;
        } else {
            mEmailText.setError(null);
            mEmailLayout.setErrorEnabled(false);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            mPasswordText.setError(getString(R.string.login_validate_password_error));
            mPasswordLayout.setErrorEnabled(true);
            valid = false;
        } else {
            mPasswordText.setError(null);
            mPasswordLayout.setErrorEnabled(false);
        }

        if (name.isEmpty()) {
            mNameText.setError(getString(R.string.login_validate_name_error));
            mNameLayout.setEnabled(true);
            valid = false;
        } else {
            mNameText.setError(null);
            mNameLayout.setEnabled(false);
        }

        if (surname.isEmpty()) {
            mSurnameText.setError(getString(R.string.login_validate_surname_error));
            mSurNameLayout.setEnabled(true);
            valid = false;
        } else {
            mSurnameText.setError(null);
            mSurNameLayout.setEnabled(false);
        }

        return valid;
    }

    @Override
    public void onBackPressed() {
        LoginActivity.start(mContext);
        finish();
    }
}
