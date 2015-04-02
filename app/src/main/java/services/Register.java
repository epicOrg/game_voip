package services;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import interaction.FieldsNames;

/**
 * @author	Noris
 * @since	2015-03-26
 */

public class Register implements Service {
	
	//private DataManager dataManager = DataManager.getIstance();
	
	private JSONObject json;
	
	private String username;
	private String password;
	private String email;

	private boolean value = true;
	
	public Register(JSONObject json) {
		super();
		this.json = json;
	}
	
	@Override
	public void start() {
		readFields();

        if(value){
         //TODO
        }
	}

    @Override
    public void setContext(Context context) {

    }

    private void readFields() {
		
		try {
            value = json.getBoolean(FieldsNames.NO_ERRORS);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
