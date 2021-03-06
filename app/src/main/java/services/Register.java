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
import interaction.RegistrationErrorStrings;


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
                ArrayList<String> errors;
                errors = extractErrors();
                result = new RegistrationResult(false,errors );
            }
            Message message= handler.obtainMessage(0,result);
            message.sendToTarget();
        } catch (JSONException e) {}
    }

    private ArrayList<String> extractErrors() throws JSONException {
        ArrayList<String> errorKinds = new ArrayList<String>();
        JSONObject errorsObj = json.getJSONObject(FieldsNames.ERRORS);
        Iterator<String> errors = errorsObj.keys();
        while (errors.hasNext()){
            String errorName = errors.next();
            JSONArray errorTypes = errorsObj.getJSONArray(errorName);
            for(int i = 0; i< errorTypes.length(); i++){
                errorKinds.add(errorName +" "+ errorTypes.getString(i));
            }
        }
        return errorKinds;
    }

    @Override
    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public class RegistrationResult {

        private boolean ok;
        private ArrayList<String> errors;

        public RegistrationResult(boolean result, ArrayList<String> errors) {
            this.ok = result;
            this.errors = errors;
        }

        public boolean isOk() {
            return ok;
        }

        public ArrayList<String> getErrors() {
            return errors;
        }
    }

}
