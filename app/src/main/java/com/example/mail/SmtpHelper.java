package com.example.mail;

import  com.example.mail.Base64Util;

import android.util.Log;

import com.example.mail.SocketManager;
import com.example.mail.entity.Mail;
import com.example.mail.entity.SmtpResponse;
public class SmtpHelper {

    private static final String TAG = "SmtpHelper";
    private SocketManager socketManager;


    public SmtpHelper() {
        // 获取 SocketManager 实例
        socketManager = SocketManager.getInstance();
    }

    // 连接到 Smtp 服务器
    public boolean connectToSmtp(String server, int port) {
        if (!socketManager.connectSmtp(server, port)) {
            Log.e(TAG, "Smtp连接失败");
            return false;
        }
        return true;
    }

    // 发送 Smtp 登录命令
    public boolean login(String username, String password) {
        socketManager.sendSmtpCommand("HELO " + username);
        String response = socketManager.receiveSmtpResponse();
        Log.d(TAG, "Smtp服务器响应: " + response);

        if(response.contains("250 OK"))
        {

            socketManager.sendSmtpCommand("AUTH LOGIN");
            response = socketManager.receiveSmtpResponse();
            Log.d(TAG, "Smtp服务器响应: " + response);
            if(response.contains("334 dXNlcm5hbWU6")){
                socketManager.sendSmtpCommand(Base64Util.encodeByBase64(username));
                response = socketManager.receiveSmtpResponse();
                Log.d(TAG, "Smtp服务器响应: " + response);
                if(response.contains("334 cGFzc3dvcmQ6")){
                    socketManager.sendSmtpCommand(Base64Util.encodeByBase64(password));
                    response = socketManager.receiveSmtpResponse();
                    Log.d(TAG, "Smtp服务器响应: " + response);
                    return response.contains("235 Authentication successful");

                }else {
                    return false;
                }
            }else{
                return false;
            }

        }else {
            return false;
        }

    }
    // 发送邮件
    public boolean sendEmail(Mail mail) {
        // 发送MAIL FROM 命令
        socketManager.sendSmtpCommand("MAIL FROM: <" + mail.getSenderEmail() + ">");
        String response = socketManager.receiveSmtpResponse();
        SmtpResponse smtpResponse = new SmtpResponse(response);
        smtpResponse.parseResponse();
        if (!smtpResponse.isSuccess()) {
            Log.e(TAG, "MAIL FROM命令失败: " + response);
            return false;
        }

        // 发送RCPT TO 命令
        socketManager.sendSmtpCommand("RCPT TO: <" + mail.getReceiverEmail() + ">");
        response = socketManager.receiveSmtpResponse();
        smtpResponse = new SmtpResponse(response);
        smtpResponse.parseResponse();
        if (!smtpResponse.isSuccess()) {
            Log.e(TAG, "RCPT TO命令失败: " + response);
            return false;
        }

        // 发送DATA 命令
        socketManager.sendSmtpCommand("DATA");
        response = socketManager.receiveSmtpResponse();
        smtpResponse = new SmtpResponse(response);
        smtpResponse.parseResponse();
        if (!smtpResponse.isSuccess()) {
            Log.e(TAG, "DATA命令失败: " + response);
            return false;
        }

        // 发送邮件内容
        String mailContent = "from: " + mail.getSenderEmail()+ "\r\n" +
                "to: " + mail.getReceiverEmail() + "\r\n" +
                "subject: " + mail.getSubject() + "\r\n"
                 + mail.getBody() + "\r\n.";
        socketManager.sendSmtpCommand(mailContent);
        response = socketManager.receiveSmtpResponse();
        smtpResponse = new SmtpResponse(response);
        smtpResponse.parseResponse();
        if (!smtpResponse.isSuccess()) {
            Log.e(TAG, "邮件发送失败: " + response);
            return false;
        }

        // 发送QUIT命令关闭连接
        closeConnection();

        Log.d(TAG, "邮件发送成功");
        return true;
    }

    // 关闭 POP3 连接
    public void closeConnection() {
        socketManager.closeSmtpConnection();
    }
}
