package interaction;

/**
 * @author	Noris
 * @since	2015-03-31
 */

public class ErrorStrings {
	
	public static final String ERR_USR_SHORT = "Nome utente minimo " + FieldsValues.USERNAME_MIN_LENGTH + " caratteri.";
	public static final String ERR_USR_LONG  = "Nome utente massimo " + FieldsValues.USERNAME_MAX_LENGTH + " caratteri.";
	public static final String ERR_USR_INVALID = "L'username deve contenere almeno una lettera.";
	public static final String ERR_USR_INVALID_CHAR = "Carattere inserito non valido.";
	
	public static final String ERR_PSW_SHORT = "Password minimo " + FieldsValues.PASSWORD_MIN_LENGTH + " caratteri.";
	public static final String ERR_PSW_LONG  = "Password massimo " + FieldsValues.PASSWORD_MAX_LENGTH + " caratteri.";
	public static final String ERR_PSW_INVALID = "La password e deve contenere almeno un numero e una lettera.";
	public static final String ERR_PSW_INVALID_CHAR = "Carattere inserito non valido.";
	
	public static final String ERR_EMAIL_INVALID = "Non hai inserito una email valida.";
	public static final String ERR_EMAIL_INVALID_DOMAIN = "Il dominio non è valido.";

}
