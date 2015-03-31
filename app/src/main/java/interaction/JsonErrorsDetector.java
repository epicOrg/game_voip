package interaction;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author	Noris
 * @since	2015-03-31
 */

public class JsonErrorsDetector {
	
	private String errors = new String("");
	private JSONObject json;

	public JsonErrorsDetector(JSONObject json) {
		super();
		this.json = json;
	}
	
	/**
	 * Search errors in a json service message.
	 * 
	 * @return true if the service message contains errors, false otherwise.
	 */
	public boolean searchErrors() {
		
		if ( json.has(FieldsNames.NO_ERRORS) )
			return false;
		
		return true;
	}
	
	public void searchUsernameErrors() throws JSONException {
		
		if ( !json.has(FieldsNames.USERNAME) )
			return;
		
		String usernameErrors = json.get(FieldsNames.USERNAME).toString();
		
		if ( usernameErrors.contains(FieldsNames.SHORT) )
			errors = errors.concat(ErrorStrings.ERR_USR_SHORT);
		
		if ( usernameErrors.contains(FieldsNames.LONG) )
			errors = errors.concat(ErrorStrings.ERR_USR_LONG);
		
		if ( usernameErrors.contains(FieldsNames.INVALID_CHAR) )
			errors = errors.concat(ErrorStrings.ERR_USR_INVALID_CHAR);
		
		if ( usernameErrors.contains(FieldsNames.INVALID) )
			errors = errors.concat(ErrorStrings.ERR_USR_INVALID);
	
	}
	
	public void searchPasswordErrors() throws JSONException {
		
		if ( !json.has(FieldsNames.PASSWORD) )
			return;
		
		String passwordErrors = json.get(FieldsNames.PASSWORD).toString();
		
		if ( passwordErrors.contains(FieldsNames.SHORT) )
			errors = errors.concat(ErrorStrings.ERR_PSW_SHORT);
		
		if ( passwordErrors.contains(FieldsNames.LONG) )
			errors = errors.concat(ErrorStrings.ERR_PSW_LONG);
		
		if ( passwordErrors.contains(FieldsNames.INVALID_CHAR) )
			errors = errors.concat(ErrorStrings.ERR_PSW_INVALID_CHAR);
		
		if ( passwordErrors.contains(FieldsNames.INVALID) )
			errors = errors.concat(ErrorStrings.ERR_PSW_INVALID);
	
	}
	
	public void searchEmailErrors() throws JSONException {
		
		if ( !json.has(FieldsNames.EMAIL) )
			return;
		
		String emailErrors = json.get(FieldsNames.EMAIL).toString();
		
		if ( emailErrors.contains(FieldsNames.INVALID) )
			errors = errors.concat(ErrorStrings.ERR_EMAIL_INVALID);
		
		if ( emailErrors.contains(FieldsNames.INVALID_DOMAIN) )
			errors = errors.concat(ErrorStrings.ERR_EMAIL_INVALID_DOMAIN);
	
	}

	public String getErrors() {
		return errors;
	}

}
