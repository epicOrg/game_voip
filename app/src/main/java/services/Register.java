package services;

import android.os.Handler;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

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
            RegistrationResult result;
            if(value) {
                result = new RegistrationResult(true, null);
            }else{
                String error = json.getString("sources");
                //TODO
                result = new RegistrationResult(false, error);
            }
            Message message= handler.obtainMessage(0,result);
            message.sendToTarget();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
