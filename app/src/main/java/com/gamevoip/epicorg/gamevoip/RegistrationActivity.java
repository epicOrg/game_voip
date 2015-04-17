package com.gamevoip.epicorg.gamevoip;

import android.app.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import communication.ServerCommunicationThread;
import data.RegistrationData;
import interaction.FieldsNames;
import interaction.ProgressShower;
import services.Login;
import services.Register;

/**
 * A login screen that offers login via email/password.
 */
public class RegistrationActivity extends Activity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private RegistrationActivity thisActivity = this;
    private ServerCommunicationThread serverCommunicationThread;
    private HashMap<Integer,View> views = new HashMap<Integer, View>();
    private ProgressShower progressShower;
    private RegistrationData registrationData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        serverCommunicationThread = ServerCommunicationThread.getInstance();
        serverCommunicationThread.setHandler(new RegistrationHandler());
        getViews();
        progressShower = new ProgressShower(views.get(R.id.login_progress),views.get(R.id.registration_form),
                getResources().getInteger(android.R.integer.config_shortAnimTime));


    }

    private void getViews() {
        views.put(R.id.email, findViewById(R.id.email));
        views.put(R.id.username, findViewById(R.id.username));
        views.put(R.id.password, findViewById(R.id.password));
        views.put(R.id.registration_form, findViewById(R.id.registration_form));
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

        registrationData = getRegistrationData();

        boolean cancel = registrationData.checkData(getApplicationContext(),views);

        if (!cancel){
            progressShower.showProgress(true);
            serverCommunicationThread.send(createRequest(registrationData));
        }
    }

    private JSONObject createRequest(RegistrationData registrationData) {

        JSONObject request = new JSONObject();
        try {
            request.put(FieldsNames.SERVICE, FieldsNames.REGISTER);
            request.put(FieldsNames.EMAIL, registrationData.getEmail());
            request.put(FieldsNames.USERNAME, registrationData.getUsername());
            request.put(FieldsNames.PASSWORD, registrationData.getPassword());
            Log.d("REQUEST", request.toString());
        } catch (JSONException e) {
            //TODO
        }
        return  request;
    }
    // recupera i dati dai campi di testo
    private RegistrationData getRegistrationData() {
        String email = ((EditText)views.get(R.id.email)).getText().toString();
        String username = ((EditText)views.get(R.id.username)).getText().toString();
        String password = ((EditText)views.get(R.id.password)).getText().toString();
        String confirmPassword = ((EditText)views.get(R.id.confirm_password)).getText().toString();

        return new RegistrationData(username,email,password,confirmPassword);
    }

    public class RegistrationHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Register.RegistrationResult result = (Register.RegistrationResult) msg.obj;
                if(result.isOk()){
                    Intent intent = new Intent(thisActivity, LoginActivity.class);
                    thisActivity.startActivity(intent);
            }else {
                String error = result.getError();
                showAlertDialog(error);
            }
            progressShower.showProgress(false);
            Log.d("RESULT", String.valueOf(result.isOk()));
        }
    }

    private void showAlertDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(thisActivity);
        builder.setMessage(error)
                .setTitle(getString(R.string.dialog_error));
        builder.setPositiveButton(getString(R.string.dialog_try_again), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }
}