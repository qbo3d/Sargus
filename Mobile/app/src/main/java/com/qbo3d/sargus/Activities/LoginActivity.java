package com.qbo3d.sargus.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qbo3d.sargus.R;

import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends FragmentActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    private ProgressDialog progressDialog;

    private Activity activity;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_DayNight_Dialog);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.pd_cargando));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mEmailView = findViewById(R.id.email);
//        populateAutoComplete();

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    private void attemptLogin() {

        progressDialog.show();

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.login_error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.login_error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.login_error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            progressDialog.dismiss();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

//            register(email,password);
				Toast.makeText(activity, "Registrar", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@") && email.contains(".");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 8;
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
    }

//    @SuppressLint("StaticFieldLeak")
//    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
//
//        private final String mEmail;
//        private final String mPassword;
//        private final String mId;
//        private boolean res;
//
//        UserLoginTask(String email, String password, String Id) {
//            mEmail = email;
//            mPassword = password;
//            mId = Id;
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            res = Util.getObjetoLogin(mPassword, mEmail, mId);
//            return true;
//        }
//
//        @SuppressLint("ApplySharedPref")
//        @Override
//        protected void onPostExecute(final Boolean success) {
//
//            progressDialog.dismiss();
//
//            if (success) {
//                if(res) {
//                    if (mMantenerView.isChecked()){
//                        userDetails = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                        SharedPreferences.Editor edit = userDetails.edit();
//                        edit.putString("usuario", mEmail);
//                        edit.putString("password", mPassword);
//                        edit.commit();
//                    }
//                    callMain();
//                } else {
//                    mPasswordView.setError(getString(R.string.login_error_incorrect_password));
//                    mPasswordView.requestFocus();
//                }
//            }
//        }
//    }

    private void callMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

//    private void register(final String email, final String password){
//
//        GCMClientManager pushClientManager = new GCMClientManager(LoginActivity.this, getResources().getString(R.string.gcm_defaultSenderId));
//        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
//            @Override
//            public void onSuccess(String registrationId, boolean isNewRegistration) {
//                if (Util.isConnect(LoginActivity.this)) {
//                    new UserLoginTask(email, password, registrationId).execute((Void) null);
//                } else {
//                    mPasswordView.setError(getString(R.string.login_connection_error));
//                    mPasswordView.requestFocus();
//                    progressDialog.dismiss();
//                }
//            }
//
//            @Override
//            public void onFailure(String ex) {
//                super.onFailure(ex);
//
//                mPasswordView.setError(getString(R.string.login_connection_error));
//                mPasswordView.requestFocus();
//                progressDialog.dismiss();
//            }
//        });
//    }
}