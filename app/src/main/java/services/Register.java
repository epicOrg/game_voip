package services;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import interaction.FieldsNames;


public class Register implements Service {
	
	private JSONObject json;
	private Handler handler;
	
	public Register(JSONObject json) {
		super();
		this.json = json;
	}
	
	@Override
	public void start() {
		readFields();
	}

    private void readFields() {

        try {
            boolean value = json.getBoolean(FieldsNames.NO_ERRORS);
            Log.d("REGSTER_RESPONSE", json.toString());
            RegistrationResult result;
            if(value) {
                result = new RegistrationResult(true, null);
            }else{
                String error = "";
                error = extractErrors(error);
                result = new RegistrationResult(false,error );
            }
            Message message= handler.obtainMessage(0,result);
            message.sendToTarget();
        } catch (JSONException e) {}
    }

    private String extractErrors(String error) throws JSONException {
        JSONObject errorsObj = json.getJSONObject(FieldsNames.ERRORS);
        Iterator<String> errors = errorsObj.keys();
        while (errors.hasNext()){
            String errorName = errors.next();
            JSONArray errorTypes = errorsObj.getJSONArray(errorName);
            for(int i = 0; i< errorTypes.length(); i++){
                error += errorName + errorTypes.getString(i) + "\n";
            }
        }
        return error;
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public class RegistrationResult {

        private boolean ok;
        private String error;

        public RegistrationResult(boolean result, String error) {
            this.ok = result;
            this.error = error;
        }

        public boolean isOk() {
            return ok;
        }

        public String getError() {
            return error;
        }
    }

}
