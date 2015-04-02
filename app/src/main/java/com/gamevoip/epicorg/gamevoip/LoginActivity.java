package com.gamevoip.epicorg.gamevoip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import org.json.JSONObject;
import java.util.HashMap;
import communication.ServerCommunicationThread;
import data.LoginData;
import interaction.FieldsNames;
import interaction.ProgressShower;
import services.Login;

/**
 * Activity du LogIn in cui l'utente inserisce l'username e la password per
 * essere riconosciuto dal server
 *
 */
public class LoginActivity extends Activity{

    private LoginActivity thysActivity = this;
    private ServerCommunicationThread serverCommunicationThread;
    private HashMap<Integer,View> views = new HashMap<Integer, View>();
    private SharedPreferences loginPreference;
    private ProgressShower progressShower;
    private LoginData loginData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //inizializzazione communcaztionManager
        serverCommunicationThread = ServerCommunicationThread.getInstance();
        serverCommunicationThread.setHandler(new LoginHandler());
        serverCommunicationThread.start();

        loginPreference = getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE);

        getViews();
        progressShower = new ProgressShower(views.get(R.id.login_progress),views.get(R.id.login_form),
                getResources().getInteger(android.R.integer.config_shortAnimTime));

        // e è attiva la funzione rememberme fai il login direttamente
        checkRememberMe();
    }

    private void checkRememberMe() {
        if(loginPreference.getBoolean("Remember", false)){
            ((TextView)views.get(R.id.username)).setText(loginPreference.getString("Username","user"));
            ((TextView)views.get(R.id.password)).setText(loginPreference.getString("Password", "pass"));
            Log.d("USER_REMEMBER", loginPreference.getString("Username", "user"));
            Log.d("PASS_REMEMBER", loginPreference.getString("Password","pass"));
            attemptLogin(findViewById(R.id.login));
        }
    }

    private void getViews() {
        views.put(R.id.username, findViewById(R.id.username));
        views.put(R.id.password, findViewById(R.id.password));
        views.put(R.id.login_form,findViewById(R.id.login_form));
        views.put(R.id.login_progress,findViewById(R.id.login_progress));
    }
    /**
     *Passa all'Activity di registrazione, invocato dalll'apposto bottone
     */
    public void notRegistered(View view){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    /**
     * Invocato per attivare/disattivare la funzione RememberMe
     */
    public void rememberMe(View view){
        CheckBox rememberBox = (CheckBox) findViewById(R.id.remeberMeBox);
        SharedPreferences.Editor editor = loginPreference.edit();
        Log.d("REMEMBER_ME", String.valueOf(rememberBox.isChecked()));
        if(rememberBox.isChecked())
            editor.putBoolean("Remember" , true);
        else
            editor.putBoolean("Remember", false);
        editor.commit();
    }

    /**
     * Avvia il Login inviando la richiesta al server
     */

    public void attemptLogin(View view) {

        ((TextView)views.get(R.id.username)).setError(null);
        ((TextView)views.get(R.id.password)).setError(null);
        loginData = getData();
        boolean cancel = loginData.checkData(getApplicationContext(), views);

        if (!cancel) {
            progressShower.showProgress(true);
            serverCommunicationThread.send(createRequest());
        }
    }

    private JSONObject createRequest() {
        JSONObject request = new JSONObject();
        try {
            request.put(FieldsNames.SERVICE, FieldsNames.LOGIN);
            request.put(FieldsNames.USERNAME, loginData.getUsername());
            request.put(FieldsNames.PASSWORD, loginData.getPassword());
            Log.d("REQUEST", request.toString());
        } catch (Exception e) {
            //TODO
        }
        return  request;
    }
    // recupera i dati dai campi di testo
    private LoginData getData(){
        String username =  ((TextView)views.get(R.id.username)).getText().toString();
        String password =  ((TextView)views.get(R.id.password)).getText().toString();
        return new LoginData(username,password);
    }

    /**
     * Handler che permette la comunicazione tra il Thread di login attivato alla ricezione della risposta dal server e
     * l'Activity di login stessa ricevendo i risultati del Login
     *
     */
    public class LoginHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            Login.LoginResult result = (Login.LoginResult) msg.obj;
            if(result.isOk()){
                if(loginPreference.getBoolean("Remember",false)) {
                    SharedPreferences.Editor editor = loginPreference.edit();
                    editor.putString("Username", loginData.getUsername());
                    editor.putString("Password", loginData.getPassword());
                    editor.commit();
                    Log.d("REMEMBER", "fields saved");
                }
                Intent intent = new Intent(thysActivity, CallActivity.class);
                intent.putExtra("Username", loginData.getUsername());
                thysActivity.startActivity(intent);
            }else {
                String error = result.getError();
                showAlertDialog(error);
                progressShower.showProgress(false);
            }
            Log.d("RESULT", String.valueOf(result.isOk()));
        }
    }

    private void showAlertDialog(String error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(thysActivity);
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