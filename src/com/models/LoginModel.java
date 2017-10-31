package com.models;

public class LoginModel{

    private String username;
    private String password;

    private String errorMsg;

    public void login(String  username, String password) {
        this.username = username;
        this.password = password;
        System.out.println(this.username);
        System.out.println(this.password);

        this.errorMsg = "username or password incorrect";
    }

    public String getValue() {
        return this.errorMsg;
    }
}