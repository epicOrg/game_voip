package communication;

import org.json.JSONException;
import org.json.JSONObject;

import interaction.FieldsNames;
import services.Login;
import services.Register;
import services.Service;
import services.Unknown;

/**
 * @author	Noris
 * @since	2015-03-26
 */

public class ServiceChooser {
	
	public Service setService(JSONObject json) throws JSONException {
		switch( json.getString(FieldsNames.SERVICE) ) {
			case FieldsNames.REGISTER:
				return new Register(json);
			case FieldsNames.LOGIN:
				return new Login(json);
			default:
				//TODO
				return new Unknown();
		}
	}
}
