package com.gamevoip.epicorg.gamevoip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * A login screen that offers login via email/password.
 */
public class RegistrationActivity extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegistrationTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        mUsernameView = (EditText) findViewById(R.id.username);
        mConfirmPasswordView = (EditText) findViewById(R.id.confirm_password);

        mPasswordView = (EditText) findViewById(R.id.password);
       /** mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });*/

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptRegistration(View view) {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        RegistrationData registrationData = getRegistrationData();

        boolean cancel = checkData(registrationData);

        if (cancel) {
            //non fare il login
        } else {
            showProgress(true);
            mAuthTask = new UserRegistrationTask(registrationData);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean checkData(RegistrationData registrationData) {
        boolean cancel = false;


        if (!registrationData.isPasswordValid()) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            mPasswordView.requestFocus();
            cancel = true;
        }
        if (!TextUtils.isEmpty(registrationData.getPassword()) && !registrationData.isPasswordLognEnought()) {
            mPasswordView.setError(getString(R.string.error_short_password));
            mPasswordView.requestFocus();
            cancel = true;
        }

        if (TextUtils.isEmpty(registrationData.getUsername())) {
            mUsernameView.setError(getString(R.string.error_field_required));
            mUsernameView.requestFocus();
            cancel = true;
        }

        if (!registrationData.passwordsMatches()) {
            mConfirmPasswordView.setError(getString(R.string.error_passwords_different));
            mConfirmPasswordView.requestFocus();
            cancel = true;
        }

        if (TextUtils.isEmpty(registrationData.getEmail())) {
            mEmailView.setError(getString(R.string.error_field_required));
            mEmailView.requestFocus();
            cancel = true;
        } else if (!registrationData.isEmailValid()) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            mEmailView.requestFocus();
            cancel = true;
        }
        return cancel;
    }

    private RegistrationData getRegistrationData() {
        String email = mEmailView.getText().toString();
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();

        return new RegistrationData(username,email,password,confirmPassword);
    }
    /**
     * Mostra una barra di caricamento e nasconde i campi riempiti se la versione è almeno Honeycomb altrimenti nasconde solo i campi
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Task asincrono di Registrazione nel quale viene gestita a logica di comunicazione col server
     */
    public class UserRegistrationTask extends AsyncTask<Void, Void, Boolean> {

        private RegistrationData registrationData;

        public UserRegistrationTask(RegistrationData registrationData) {
            this.registrationData = registrationData;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject registrationRequest = new JSONObject();
            try {
                Socket socket = new Socket(InetAddress.getByName("192.168.1.4"), 7007);
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
                try {
                    registrationRequest.put("service", "REGISTER");
                    registrationRequest.put("email", registrationData.getEmail());
                    registrationRequest.put("username", registrationData.getUsername());
                    registrationRequest.put("password", registrationData.getPassword());


                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }
                printWriter.println(registrationRequest.toString());

            } catch (IOException e) {
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}