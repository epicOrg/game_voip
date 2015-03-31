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
import java.util.HashMap;

import communication.CommunicationManager;
import interaction.CustomAlertDialog;
import communication.ServerCommunicationReciver;
import data.RegistrationData;

/**
 * A login screen that offers login via email/password.
 */
public class RegistrationActivity extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private RegistrationActivity thisActivity = this;
    private CommunicationManager communicationManager;
    private HashMap<Integer,View> views = new HashMap<Integer, View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        communicationManager = CommunicationManager.getInstance();
        communicationManager.setContext(getApplicationContext());

        views.put(R.id.email, findViewById(R.id.email));
        views.put(R.id.username, findViewById(R.id.username));
        views.put(R.id.password, findViewById(R.id.password));
        views.put(R.id.login_form, findViewById(R.id.login_form));
        views.put(R.id.login_progress,findViewById(R.id.login_progress));
        views.put(R.id.confirm_password, findViewById(R.id.confirm_password));
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptRegistration(View view) {
        // Reset errors.
        ((EditText)views.get(R.id.email)).setError(null);
        ((EditText)views.get(R.id.password)).setError(null);
        ((EditText)views.get(R.id.username)).setError(null);
        ((EditText)views.get(R.id.confirm_password)).setError(null);

        RegistrationData registrationData = getRegistrationData();

        boolean cancel = registrationData.checkData(getApplicationContext(),views);

        if (cancel) {
            //non fare il login
        } else {
            showProgress(true);
            communicationManager.send(createRequest(registrationData));
        }
    }

    private JSONObject createRequest(RegistrationData registrationData) {

        JSONObject request = new JSONObject();
        try {
            request.put("service", "REGISTER");
            request.put("email", registrationData.getEmail());
            request.put("username", registrationData.getUsername());
            request.put("password", registrationData.getPassword());
            Log.d("Richiesta", request.toString());
        } catch (JSONException e) {
            //TODO
        }

        return  request;
    }

    private RegistrationData getRegistrationData() {
        String email = ((EditText)views.get(R.id.email)).getText().toString();
        String username = ((EditText)views.get(R.id.email)).getText().toString();
        String password = ((EditText)views.get(R.id.password)).getText().toString();
        String confirmPassword = ((EditText)views.get(R.id.confirm_password)).getText().toString();

        return new RegistrationData(username,email,password,confirmPassword);
    }
    /**
     * Mostra una barra di caricamento e nasconde i campi riempiti se la versione è almeno Honeycomb altrimenti nasconde solo i campi
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        final View mLoginFormView = views.get(R.id.login_form);
        final View mProgressView = views.get(R.id.login_progress);

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
    /**
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
    }*/
}