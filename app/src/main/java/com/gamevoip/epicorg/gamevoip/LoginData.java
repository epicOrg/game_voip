package com.gamevoip.epicorg.gamevoip;

/**
 * Created by Luca on 27/03/2015.
 */
public class LoginData {

    private String username;
    private String password;

    public LoginData(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean isPasswordLognEnought(){
        return password.length() > 7;
    }

    public boolean isPasswordValid(){
        return password.matches(".*\\d+.*") && password.matches(".*[a-zA-Z]+.*");
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
