package services;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author	Noris
 * @since	2015-03-26
 */

public class Unknown implements Service {

	@Override
	public void start() {
		//return getResponse().toString();
	}

    @Override
    public void setContext(Context context) {

    }

    private JSONObject getResponse() {
		
		JSONObject jsonResponse = new JSONObject();
		
		try {
			
			jsonResponse.put("service", "UNKNOWN");
			return jsonResponse;
			
		} catch (JSONException e) {
			//TODO
			return new JSONObject();
		}

	}

}
