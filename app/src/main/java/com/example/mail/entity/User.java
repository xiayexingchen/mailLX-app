package com.example.mail.entity;

import java.io.Serializable;

public class User implements Serializable {
    private String nickName;

    private String password;

    private String account;

    private Integer UserState;

    private String authorizationCode;

    public User(String nickName, String password, String account, Integer userState) {
        this.nickName = nickName;
        password = password;
        account = account;
        UserState = userState;
    }

    public User() {
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        password = password;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        account = account;
    }


    public Integer getUserState() {
        return UserState;
    }

    public void setUserState(Integer userState) {
        UserState = userState;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode) {
        this.authorizationCode = authorizationCode;
    }

    @Override
    public String toString() {
        return "User{" +
                "nickName='" + nickName + '\'' +
                ", Password='" + password + '\'' +
                ", Account='" + account + '\'' +
                ", UserState=" + UserState +
                '}';
    }
}
