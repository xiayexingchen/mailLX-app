package com.example.mail;

import android.util.Log;

import com.example.mail.SocketManager;
import com.example.mail.entity.Mail;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public String login(String username, String password) {
        // 发送 USER 命令
        socketManager.sendPop3Command("USER " + username);
        String response = socketManager.receivePop3Response();
        Log.d(TAG, "POP3服务器响应: " + response);

        if(response.contains("+OK")) {
            // 发送 PASS 命令
            socketManager.sendPop3Command("PASS " + password);
            response = socketManager.receivePop3Response();
            Log.d(TAG, "POP3服务器响应: " + response);
            if (response.contains("+OK")){
                return "OK";

            }else if(response.contains("-ERR User log out")){
                return "ERR User log out";
            }else
               return  "ERR";
        }else if(response.contains("450")){
            return "450";
        }
        else {
            return "ERR";
        }
    }
    // 获取邮件列表
    public List<String> getMailListIds() {
        List<String> mailIds = new ArrayList<>();
        socketManager.sendPop3Command("LIST");
        String response = socketManager.receivePop3Response();
        Log.d(TAG, "POP3服务器响应: " + response);

        // 解析邮件列表响应
        if (response.contains("+OK")) {
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.startsWith("+OK")) continue;  // 跳过标识符
                String[] parts = line.split(" ");
                if (parts.length > 1) {
                    mailIds.add(parts[0]);  // 获取邮件编号
                }
            }
        }

        return mailIds;
    }

    // 获取指定邮件内容
    public String getMailContent(String mailId) {
        socketManager.sendPop3Command("RETR " + mailId);
        return socketManager.receivePop3Response();
    }

    public List<Mail> getMailList()
    {
        List<Mail> mailList=new ArrayList<>();
        List<String> mailIds=getMailListIds();
        for(String mailId:mailIds)
        {
            String mailContent=getMailContent(mailId);
            System.out.println(mailContent);
            Mail mail=parseMail(mailContent);
            System.out.println(mail);
            mailList.add(mail);

        }

        return mailList;
    }
    public Mail parseMail(String rawMail) {
        Mail mail = new Mail();

        // Split the raw email into lines
        String[] lines = rawMail.split("\n");

        // Iterate over the lines and extract the values
        for (String line : lines) {
            // Skip the +OK response (POP3 server response status line)
            if (line.startsWith("+OK")) {

                continue;
            }

            if (line.startsWith("From: ")) {

                // Extract the sender email (removing "From: " and "< >")
                String sender = line.substring(6).trim(); // remove "From: "
                System.out.println(sender);
                mail.setSenderEmail(removeAngleBrackets(sender));

            } else if (line.startsWith("To: ")) {

                // Extract the recipient email
                String receiver = line.substring(4).trim(); // remove "To: "
                mail.setReceiverEmail(removeAngleBrackets(receiver));
            } else if (line.startsWith("SendTime: ")) {

                // Extract the send time
                String sendTimeStr = line.substring(10).trim(); // remove "SendTime: "
                sendTimeStr= removeAngleBrackets(sendTimeStr);

                mail.setSendTime(sendTimeStr); // remove "SendTime: "
            } else if (line.startsWith("Subject: ")) {

                // Extract the subject
                mail.setSubject(removeAngleBrackets(line.substring(9).trim())); // remove "Subject: "
            } else if (line.startsWith("Body: ")) {
                // Extract the body content
                mail.setBody(removeAngleBrackets(line.substring(6).trim())); // remove "Body: "
            }
        }

        return mail;
    }

    // Helper method to remove angle brackets around the email
    private String removeAngleBrackets(String str) {
        if (str.startsWith("<") && str.endsWith(">")) {
            return str.substring(1, str.length() - 1);  // Remove the "<" and ">"
        }
        return str;  // In case no angle brackets, return the original string
    }
    // Helper method to convert SendTime String to Timestamp
    private Timestamp parseSendTime(String sendTimeStr) {
        try {
            // Define the format of the SendTime string
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
            Date parsedDate = sdf.parse(sendTimeStr);
            return new Timestamp(parsedDate.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Return null if parsing fails
        }
    }

    public boolean deleteMail(int mailId)
    {
        socketManager.sendPop3Command("DELE "+(mailId+1));
        String response=socketManager.receivePop3Response();
        Log.d(TAG, "POP3服务器响应: " + response);
        return response.contains("+OK");
    }


    public boolean restMail(int mailId)
    {
        socketManager.sendPop3Command("REST "+(mailId+1));
        String response=socketManager.receivePop3Response();
        Log.d(TAG, "POP3服务器响应: " + response);
        return response.contains("+OK");
    }


    // 关闭 POP3 连接
    public void closeConnection() {
        socketManager.closePop3Connection();
    }
}
