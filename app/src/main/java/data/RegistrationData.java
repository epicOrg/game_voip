package data;

import android.content.Context;
import android.view.View;

import java.util.HashMap;

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

    @Override
    public boolean checkData(Context context, HashMap<Integer, View> views) {
        boolean value =  super.checkData(context, views);
        return value;
    }
}
