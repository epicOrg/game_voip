package com.gamevoip.epicorg.gamevoip;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity{

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
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
        serverCommunicationThread.setContext(getApplicationContext());
        serverCommunicationThread.start();

        loginPreference = getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE);

        getViews();

        //progressShower = new ProgressShower(views.get(R.id.login_progress),views.get(R.id.login_form),
          //      getResources().getInteger(android.R.integer.config_shortAnimTime));

        if(loginPreference.getBoolean("Remember", false)){
            ((TextView)views.get(R.id.username)).setText(loginPreference.getString("Username","user"));
            ((TextView)views.get(R.id.password)).setText(loginPreference.getString("Password", "pass"));
            Log.d("USER_REMEMBER", loginPreference.getString("Username","user"));
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

    public void notRegistered(View view){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

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

    public void attemptLogin(View view) {

        ((TextView)views.get(R.id.username)).setError(null);
        ((TextView)views.get(R.id.password)).setError(null);
        loginData = getData();
        boolean cancel = loginData.checkData(getApplicationContext(), views);

        if (!cancel) {
            //progressShower.showProgress(true);
            serverCommunicationThread.send(createRequest());
            //new LoginTask(loginData,progressShower,getApplicationContext(),loginPreference).execute();
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

    private LoginData getData(){
        String username =  ((TextView)views.get(R.id.username)).getText().toString();
        String password =  ((TextView)views.get(R.id.password)).getText().toString();
        return new LoginData(username,password);
    }

    /**
     * Mostra una barra di caricamento e nasconde i campi riempiti
     */
    /**@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        final TextView mLoginFormView =  (TextView)views.get(R.id.login_form);
        final TextView mProgressView =  (TextView)views.get(R.id.login_progress);

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
   /** public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private LoginData loginData;
        private String error;

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
                loginRequest.put("service", "LOGIN");
                loginRequest.put("username", loginData.getUsername());
                loginRequest.put("password", loginData.getPassword());
                printWriter.println(loginRequest.toString());
                Log.d("Richiesta",loginRequest.toString());
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
                e.printStackTrace();
                error = getString(R.string.error_server_unreachable);
                return false;
            } catch (JSONException e) {
                e.printStackTrace();
                error = getString(R.string.error_communication);
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            if (success) {
                if(loginPreference.getBoolean("Remember",false)) {
                    SharedPreferences.Editor editor = loginPreference.edit();
                    editor.putString("Username", loginData.getUsername());
                    editor.putString("Password", loginData.getPassword());
                    editor.commit();
                    Log.d("REMEMBER", "fields saved");
                }
                Intent intent = new Intent(thisActivity, CallActivity.class);
                intent.putExtra("Username", loginData.getUsername());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                showErrorDialog();
                showProgress(false);

            }
            showProgress(false);
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
