package interaction;

import com.gamevoip.epicorg.gamevoip.R;

import java.util.HashMap;

/**
 * @author	Luca
 * @since	2015-03-31
 */

public class RegistrationErrorStrings {

    private HashMap<String, Integer> errors =  new HashMap<String, Integer>();

    public RegistrationErrorStrings(){
        createMap();
    }

    private void createMap() {
        errors.put(FieldsNames.PASSWORD + " " + FieldsNames.SHORT, R.string.error_short_password);
        errors.put(FieldsNames.PASSWORD + " " + FieldsNames.LONG, R.string.password_long);
        errors.put(FieldsNames.PASSWORD + " " + FieldsNames.INVALID, R.string.error_invalid_password);
        errors.put(FieldsNames.USERNAME + " " + FieldsNames.SHORT, R.string.username_short);
        errors.put(FieldsNames.USERNAME + " " + FieldsNames.LONG, R.string.username_long);
        errors.put(FieldsNames.USERNAME + " " + FieldsNames.INVALID_CHAR, R.string.username_invaid_char);
        errors.put(FieldsNames.USERNAME + " " + FieldsNames.ALREADY_USED, R.string.username_already_used);
        errors.put(FieldsNames.EMAIL + " " + FieldsNames.INVALID, R.string.email_not_valid);
        errors.put(FieldsNames.EMAIL + " " + FieldsNames.INVALID_DOMAIN, R.string.domain_not_vaid);
        errors.put(FieldsNames.EMAIL + " " + FieldsNames.ALREADY_USED, R.string.email_already_used);
    }

    public int getStringIdByError(String error){
        return  errors.get(error);
    }
}
