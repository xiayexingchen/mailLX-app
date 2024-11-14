package com.example.mail;

import android.app.Application;

public class MyApplication extends Application {
    private static String number;
    private String username;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
