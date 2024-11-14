package com.example.mail;

import  com.example.mail.Base64Util;

import android.util.Log;

import com.example.mail.SocketManager;

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
        // 发送 USER 命令
        socketManager.sendSmtpCommand("HELO " + username);
        String response = socketManager.receiveSmtpResponse();
        Log.d(TAG, "Smtp服务器响应: " + response);

        if(response.contains("250 OK"))
        {
            // 发送 PASS 命令
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

    // 关闭 POP3 连接
    public void closeConnection() {
        socketManager.closeSmtpConnection();
    }
}
