package com.qbo3d.sargus.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qbo3d.sargus.R;
import com.qbo3d.sargus.Util;
import com.qbo3d.sargus.Utilities.GCMClientManager;
import com.qbo3d.sargus.Utilities.Serv;
import com.qbo3d.sargus.Vars;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.

        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getString(R.string.pd_cargando));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        Intent intent = new Intent();
        String packageName = getPackageName();
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        }

        checkPermission();

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

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) +
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale (this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    ActivityCompat.shouldShowRequestPermissionRationale (this, Manifest.permission.CAMERA) ||
                    ActivityCompat.shouldShowRequestPermissionRationale (this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                Snackbar.make(this.findViewById(android.R.id.content),
                        "Faltan permisos por habilitar",
                        Snackbar.LENGTH_INDEFINITE).setAction("Habilitar",
                        new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestPermissions(
                                        new String[]{
                                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.CAMERA,
                                                Manifest.permission.ACCESS_FINE_LOCATION
                                        }, Vars.PERMISSIONS_MULTIPLE_REQUEST);
                            }
                        }).show();
            } else {
                requestPermissions(
                        new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, Vars.PERMISSIONS_MULTIPLE_REQUEST);
            }
        } else {
            // write your logic code if permission already granted
        }
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
        }
//        else if (!isEmailValid(email)) {
//            mEmailView.setError(getString(R.string.login_error_invalid_email));
//            focusView = mEmailView;
//            cancel = true;
//        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            progressDialog.dismiss();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            register(this, email,password);
        }
    }

//    private boolean isEmailValid(String email) {
//        return email.contains("@") && email.contains(".");
//    }

    private boolean isPasswordValid(String password) {
        return password.length() > 8;
    }

//    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
//        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(LoginActivity.this,
//                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
//
//        mEmailView.setAdapter(adapter);
//    }

//    private interface ProfileQuery {
//        String[] PROJECTION = {
//                ContactsContract.CommonDataKinds.Email.ADDRESS,
//                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
//        };
//
//        int ADDRESS = 0;
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case Vars.PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(cameraPermission && readExternalFile)
                    {
                        // write your logic here
                    } else {
                        Snackbar.make(this.findViewById(android.R.id.content),
                                "Please Grant Permissions to upload profile photo",
                                Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        requestPermissions(
                                                new String[]{Manifest.permission
                                                        .READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                                Vars.PERMISSIONS_MULTIPLE_REQUEST);
                                    }
                                }).show();
                    }
                }
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class LoginTask extends AsyncTask<Void, Void, Boolean> {

        private Activity mActivity;
        private final String mEmail;
        private final String mPassword;
        private final String mId;

        LoginTask(Activity activity, String email, String password, String Id) {
            mActivity = activity;
            mEmail = email;
            mPassword = password;
            mId = Id;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            Vars.isConn = Serv.getLogin(mActivity, mEmail, mPassword, mId);
            return true;
        }

        @SuppressLint("ApplySharedPref")
        @Override
        protected void onPostExecute(final Boolean success) {

            progressDialog.dismiss();
            if (success && Vars.isConn && Vars.usuario.getId() != null) {

                Util.setStorageString(getBaseContext(), "documento", mEmail);
                Util.setStorageString(getBaseContext(), "password", mPassword);

                callMain();
            } else if (!Vars.isConn) {
                mPasswordView.setError(getString(R.string.login_connection_error_content));
                mPasswordView.requestFocus();
            } else if (Vars.usuario.getId() == null){
                mPasswordView.setError(getString(R.string.login_error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }
    }

    private void callMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void register(final Activity activity, final String email, final String password){

        GCMClientManager pushClientManager = new GCMClientManager(LoginActivity.this, getResources().getString(R.string.gcm_defaultSenderId));
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {
                if (Util.isConnect(activity, activity.getLocalClassName())) {
                    new LoginTask(activity, email, password, registrationId).execute((Void) null);
                } else {
                    mPasswordView.setError(getString(R.string.login_connection_error));
                    mPasswordView.requestFocus();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);

                mPasswordView.setError(getString(R.string.login_connection_error));
                mPasswordView.requestFocus();
                progressDialog.dismiss();
            }
        });
    }
}