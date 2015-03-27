package com.gamevoip.epicorg.gamevoip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
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
public class LoginActivity extends Activity{

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);
        /**mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

    public void notRegistered(View view){

        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(View view) {
        if (mAuthTask != null) {
            return;
        }

        mUsernameView.setError(null);
        mPasswordView.setError(null);
        LoginData loginData = getData();
        boolean cancel = checkData(loginData);

        if (cancel) {
            //non fare il login
        } else {
            showProgress(true);
            mAuthTask = new UserLoginTask(loginData);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean checkData(LoginData loginData) {
        boolean cancel = false;


        if (!loginData.isPasswordValid()) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            mPasswordView.requestFocus();
            cancel = true;
        }

        if (!TextUtils.isEmpty(loginData.getPassword()) && !loginData.isPasswordLognEnought()) {
            mPasswordView.setError(getString(R.string.error_short_password));
            mPasswordView.requestFocus();
            cancel = true;
        }

        if (TextUtils.isEmpty(loginData.getUsername())) {
            mUsernameView.setError(getString(R.string.error_field_required));
            mUsernameView.requestFocus();
            cancel = true;
        }
        return cancel;
    }

    private LoginData getData(){
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        return new LoginData(username,password);
    }

    /**
     * Mostra una barra di caricamento e nasconde i campi riempiti
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
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
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    /**
     * Task asincrono di Login nel quale viene gestita a logica di comunicazione col server
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private LoginData loginData;

        public UserLoginTask(LoginData loginData) {
            this.loginData = loginData;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            System.out.println(loginData.getUsername());
            System.out.println(loginData.getPassword());

            JSONObject loginRequest = new JSONObject();
            try {
                Socket socket = new Socket(InetAddress.getByName("192.168.1.4"), 7007);
                PrintWriter printWriter = new PrintWriter(socket.getOutputStream(),true);
                try {
                    loginRequest.put("service", "LOGIN");
                    loginRequest.put("username", loginData.getUsername());
                    loginRequest.put("password", loginData.getPassword());

                } catch (JSONException e) {
                    e.printStackTrace();
                    return false;
                }

                System.out.println(loginRequest.toString());

                printWriter.println(loginRequest.toString());

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