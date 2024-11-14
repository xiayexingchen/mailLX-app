package com.example.mail;

import android.util.Log;

import com.example.mail.SocketManager;

public class Pop3Helper {

    private static final String TAG = "Pop3Helper";
    private SocketManager socketManager;

    public Pop3Helper() {
        // 获取 SocketManager 实例
        socketManager = SocketManager.getInstance();
    }

    // 连接到 POP3 服务器
    public boolean connectToPop3(String server, int port) {
        if (!socketManager.connectPop3(server, port)) {
            Log.e(TAG, "POP3连接失败");
            return false;
        }
        return true;
    }

    // 发送 POP3 登录命令
    public boolean login(String username, String password) {
        // 发送 USER 命令
        socketManager.sendPop3Command("USER " + username);
        String response = socketManager.receivePop3Response();
        Log.d(TAG, "POP3服务器响应: " + response);

        if(response.contains("+OK"))
        {
            // 发送 PASS 命令
            socketManager.sendPop3Command("PASS " + password);
            response = socketManager.receivePop3Response();
            Log.d(TAG, "POP3服务器响应: " + response);

            // 判断登录是否成功
            return response.contains("+OK");
        }else {
            return false;
        }
    }

    // 关闭 POP3 连接
    public void closeConnection() {
        socketManager.closePop3Connection();
    }
}
