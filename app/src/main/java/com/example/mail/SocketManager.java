package com.example.mail;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketManager {

    private static SocketManager instance;
    private Socket pop3Socket;
    private Socket smtpSocket;
    private BufferedReader pop3Reader;
    private BufferedReader smtpReader;
    private PrintWriter pop3Writer;
    private PrintWriter smtpWriter;
    private boolean isPop3Connected = false;
    private boolean isSmtpConnected = false;

    private static final String TAG = "SocketManager";

    private SocketManager() {
        // 私有化构造函数，确保只有一个实例
    }

    public static synchronized SocketManager getInstance() {
        if (instance == null) {
            instance = new SocketManager();
        }
        return instance;
    }

    // 连接 POP3 服务器
    public boolean connectPop3(String host, int port) {
        if(pop3Socket != null && !pop3Socket.isClosed())
        {
            Log.d(TAG, "POP3已连接成功");
            return true;
        }
        try {
            pop3Socket = new Socket(host, port);
            pop3Reader = new BufferedReader(new InputStreamReader(pop3Socket.getInputStream()));
            pop3Writer = new PrintWriter(new OutputStreamWriter(pop3Socket.getOutputStream()), true);
            isPop3Connected = true;
            Log.d(TAG, "POP3连接成功");
            return true;
        } catch (IOException e) {
            Log.e(TAG, "POP3连接失败: " + e.getMessage());
            return false;
        }
    }

    // 连接 SMTP 服务器
    public boolean connectSmtp(String host, int port) {
        if(smtpSocket != null && !smtpSocket.isClosed())
        {
            Log.d(TAG, "SMTP已连接成功");
            return true;
        }
        try {
            smtpSocket = new Socket(host, port);
            smtpReader = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
            smtpWriter = new PrintWriter(new OutputStreamWriter(smtpSocket.getOutputStream()), true);
            isSmtpConnected = true;
            Log.d(TAG, "SMTP连接成功");
            String line;
            line = smtpReader.readLine();
            Log.d(TAG, "SMTP服务器响应: " + line);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "SMTP连接失败: " + e.getMessage());
            return false;
        }
    }

    // 发送 POP3 命令
    public void sendPop3Command(String command) {
        if (isPop3Connected && pop3Writer != null) {
            pop3Writer.println(command);
            pop3Writer.flush();
        }
    }

    // 发送 SMTP 命令
    public void sendSmtpCommand(String command) {
        if (isSmtpConnected && smtpWriter != null) {
            smtpWriter.println(command);
            smtpWriter.flush();
        }
    }

    // 获取 POP3 响应
    public String receivePop3Response() {
        StringBuilder response = new StringBuilder();
        if (isPop3Connected && pop3Reader != null) {
            try {
                String line;
                while (!(line = pop3Reader.readLine()).equals("#end#")) {
                    Log.d(TAG, "POP3读取响应: " +line);
                    response.append(line).append("\n");

                }
            } catch (IOException e) {
                Log.e(TAG, "POP3读取响应失败: " + e.getMessage());
            }
        }
        return response.toString();
    }

    // 获取 SMTP 响应
    public String receiveSmtpResponse() {
        StringBuilder response = new StringBuilder();
        if (isSmtpConnected && smtpReader != null) {
            try {
                    String line;
                    line = smtpReader.readLine();
                    response.append(line).append("\n");
                    Log.d(TAG, "SMTP读取响应: " +line);

            } catch (IOException e) {
                Log.e(TAG, "SMTP读取响应失败: " + e.getMessage());
            }
        }
        return response.toString();
    }

    // 关闭 POP3 连接
    public void closePop3Connection() {
        closeConnection(pop3Socket, pop3Reader, pop3Writer);
        isPop3Connected = false;
    }

    // 关闭 SMTP 连接
    public void closeSmtpConnection() {
        closeConnection(smtpSocket, smtpReader, smtpWriter);
        isSmtpConnected = false;
    }

    // 关闭连接的通用方法
    private void closeConnection(Socket socket, BufferedReader reader, PrintWriter writer) {
        try {
            if (socket != null && !socket.isClosed()) {
                sendPop3Command("QUIT");
                if (reader != null) reader.close();
                if (writer != null) writer.close();
                socket.close();
                Log.d(TAG, "连接已关闭");
            }
        } catch (IOException e) {
            Log.e(TAG, "关闭连接失败: " + e.getMessage());
        }
    }

    public boolean isPop3Connected() {
        return isPop3Connected;
    }

    public boolean isSmtpConnected() {
        return isSmtpConnected;
    }
}
