package data;

import data.LoginData;

/**
 * Created by Luca on 27/03/2015.
 */
public class RegistrationData extends LoginData {

    private String email;
    private String confirmPassword;

    public RegistrationData(String username,String email, String password, String confirmPassword) {
        super(username, password);
        this.email = email;
        this.confirmPassword = confirmPassword;
    }

    public boolean passwordsMatches(){
        return confirmPassword.equals(super.getPassword());
    }

    public boolean isEmailValid(){
        return email.contains("@") && email.contains(".");
    }

    public String getEmail() {
        return email;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }
}
