package com.example.mail;

import android.app.Application;

import com.example.mail.entity.Mail;

import java.util.List;
import java.util.ArrayList;


public class MyApplication extends Application {
    private String number;
    private String username;
    private List<Mail> mailList = new ArrayList<>();  // 用于存储邮件列表
    private int stmpPort;
    private int pop3Port;
    private String  smtpServer;
    private String pop3Server;

    public int getStmpPort() {
        return stmpPort;
    }

    public void setStmpPort(int stmpPort) {
        this.stmpPort = stmpPort;
    }

    public int getPop3Port() {
        return pop3Port;
    }

    public void setPop3Port(int pop3Port) {
        this.pop3Port = pop3Port;
    }

    public String isSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public String isPop3Server() {
        return pop3Server;
    }

    public void setPop3Server(String pop3Server) {
        this.pop3Server = pop3Server;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    // 获取号码
    public String getNumber() {
        return number;
    }

    // 设置号码
    public void setNumber(String number) {
        this.number = number;
    }

    // 获取用户名
    public String getUsername() {
        return username;
    }

    // 设置用户名
    public void setUsername(String username) {
        this.username = username;
    }

    // 获取邮件列表
    public List<Mail> getMailList() {
        return mailList;
    }

    // 设置邮件列表
    public void setMailList(List<Mail> mailList) {
        this.mailList = mailList;
    }

    // 添加单个邮件到邮件列表
    public void addMail(Mail mail) {
        mailList.add(mail);
    }
}
