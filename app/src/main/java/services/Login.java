package services;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.gamevoip.epicorg.gamevoip.CallActivity;
import com.gamevoip.epicorg.gamevoip.LoginActivity;
import com.gamevoip.epicorg.gamevoip.R;

import org.json.JSONException;
import org.json.JSONObject;

import interaction.CustomAlertDialog;
import interaction.FieldsNames;

/**
 * @author	Noris
 * @since	2015-03-26
 */

public class Login implements Service {

    private JSONObject json;
    private boolean value;
    private String error;
    private Context context;

    public Login(JSONObject json) {
        super();
        this.json = json;
    }

    @Override
    public void start() {
        readFields();
    }

    private void readFields() {
        try {
            value = json.getBoolean(FieldsNames.NO_ERRORS);
            if(value){
                Intent intent = new Intent(context, CallActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                SharedPreferences loginPreference = context.getSharedPreferences("LOGIN_PREF", Context.MODE_PRIVATE);
            }else {
                String error = "ERRORE LOGIN";
                new CustomAlertDialog(context.getString(R.string.dialog_error)
                        ,error, context.getString(R.string.dialog_try_again),
                        context).show();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void setContext(Context context) {
        this.context = context;
    }
}
