package com.gamevoip.epicorg.gamevoip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import communication.CustomAlertDialog;
import communication.ServerCommunicationReciver;
import data.RegistrationData;

/**
 * A login screen that offers login via email/password.
 */
public class RegistrationActivity extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegistrationTask mAuthTask = null;
    private RegistrationActivity thisActivity = this;

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
        private String error;

        public UserRegistrationTask(RegistrationData registrationData) {
            this.registrationData = registrationData;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONObject registrationRequest = new JSONObject();
            try {
                Socket socket = new Socket(InetAddress.getByName("192.168.1.4"), 7007);
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);

                registrationRequest.put("service", "REGISTER");
                registrationRequest.put("email", registrationData.getEmail());
                registrationRequest.put("username", registrationData.getUsername());
                registrationRequest.put("password", registrationData.getPassword());
                Log.d("Richiesta", registrationRequest.toString());
                printWriter.println(registrationRequest.toString());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                JSONObject response = new JSONObject(reader.readLine());
                Log.d("Risposta", response.toString());

                reader.close();

                boolean value = response.getBoolean("value");
                if(!value){
                    error = response.getString("Description");
                }else {
                    new ServerCommunicationReciver(socket).run();
                }
                return value;

            } catch (IOException e) {
                error = getString(R.string.error_server_unreachable);
                return false;
            } catch (JSONException e) {
                error = getString(R.string.error_communication);
                return false;
            }
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                Intent intent = new Intent(thisActivity, CallActivity.class);
                intent.putExtra("Username", registrationData.getUsername());
                startActivity(intent);
            } else {
                showErrorDialog();
                showProgress(false);
            }
        }
        private void showErrorDialog() {
            new CustomAlertDialog(getString(R.string.dialog_error)
                    ,error, getString(R.string.dialog_try_again),
                    thisActivity).show();
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}