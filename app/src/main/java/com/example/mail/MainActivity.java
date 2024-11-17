package com.example.mail;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.R;
import com.example.mail.api.SharedPreferencesUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



import com.example.mail.Pop3Helper;
import com.example.mail.entity.Mail;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnRegister;

    private EditText etAccount;
    private EditText etPassword;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 找到控件
        btnLogin = findViewById(R.id.LoginButton);
        btnRegister = findViewById(R.id.ToSignUpButton);
        etAccount = findViewById(R.id.UserNameEdit);
        etPassword = findViewById(R.id.PassWordEdit);

        // 获取应用程序中的 IP 地址
        MyApplication application = (MyApplication) this.getApplicationContext();
        application.setNumber("10.72.11.179");
        ip = application.getNumber();


        // 注册按钮点击事件，跳转到注册页面
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // 登录按钮点击事件
        btnLogin.setOnClickListener(this::login);
    }

    public void login(View v) {
        // 获取输入的账户和密码
        String account = etAccount.getText().toString();
        String password = etPassword.getText().toString();
        MyApplication application = (MyApplication) this.getApplicationContext();
        String ok = "登录成功";
        String err = "密码或账号有误，请重新登录";
        String empty = "错误，密码或账号为空";
        String isNotExist = "用户不存在";
        String isDelete = "用户被禁用";

        if (account.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, empty, Toast.LENGTH_SHORT).show();
        } else {
            new Thread(() -> {
                // 使用 Pop3Helper 进行 POP3 登录
                Pop3Helper pop3Helper = new Pop3Helper();
                SmtpHelper smtpHelper = new SmtpHelper();
                String url = "http://10.0.2.2:8080/user/get-server-msg";
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                OkHttpClient httpClient = new OkHttpClient();
                Response response = null;
                String MyResult = null;
                int code=0;
                try {
                    response = httpClient.newCall(request).execute();
                    MyResult = response.body().string();
                    // 打印获取到的报文信息
                    Log.d("POP3 Response", "Response Body: " + MyResult);
                    JSONObject jsonObject1 = null;
                    jsonObject1 = new JSONObject(MyResult);
                    code=jsonObject1.getInt("state");
                    if (code == 200) {
                        // 获取 "body" 数组
                        JSONArray bodyArray = jsonObject1.getJSONArray("body");
// 获取数组中的第一个元素 (可以根据需要获取更多元素)
                        JSONObject jsonObject = bodyArray.getJSONObject(0);
// 打印解析后的数据
                        Log.d("POP3 Response", "Response Body: " + jsonObject);
// 获取字段
                        int smtpPort = jsonObject.getInt("smtpPort");
                        int pop3Port = jsonObject.getInt("pop3Port");
                        String smtpServer = jsonObject.getString("smtpState");
                        String pop3Server = jsonObject.getString("pop3State");
// 打印字段
                        Log.d("POP3 Response", "SMTP Port: " + smtpPort);
                        Log.d("POP3 Response", "POP3 Port: " + pop3Port);
                        Log.d("POP3 Response", "SMTP Server: " + smtpServer);
                        Log.d("POP3 Response", "POP3 Server: " + pop3Server);


                        application.setPop3Port(pop3Port);
                        application.setStmpPort(smtpPort);
                        application.setSmtpServer(smtpServer);
                        application.setPop3Server(pop3Server);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(code==402){
                    runOnUiThread(() -> {
                        Toast.makeText(MainActivity.this, "该IP地址禁止访问", Toast.LENGTH_SHORT).show();
                    });

                }
                else if(application.isPop3Server()!=null&&application.isPop3Server().equals("false"))
                {

                    runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "请先Pop3开启服务器", Toast.LENGTH_SHORT).show();
                    });
                }
                else {
                    int pop3Port = application.getPop3Port();
                    // 连接到 POP3 服务器
                    if (pop3Helper.connectToPop3("10.0.2.2", pop3Port)) {
                        // 使用 POP3 登录验证
                        String loginSuccessful = pop3Helper.login(account, password);

                        if (loginSuccessful.contains("OK")) {
                            List<Mail> mailList = pop3Helper.getMailList();
                            for (int i = 0; i < mailList.size(); i++) {
                                Mail mail = mailList.get(i);
                                mail.setMid(i);
                            }
                            application.setMailList(mailList);
                            // 连接 SMTP 服务器
                            // 登录成功，跳转到主页面
                            runOnUiThread(() -> {
                                // 发送登录成功邮件
                                sendLoginSuccessEmail(account);
                                // 保存登录状态
                                SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(MainActivity.this);
                                util.putBoolean("isLogin", true);
                                util.putString("username", account);
                                util.putString("password", password);
                                // 跳转到主页
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                                // 显示登录成功信息
                                Toast.makeText(getApplicationContext(), ok, Toast.LENGTH_SHORT).show();
                            });
                        }else if(loginSuccessful.contains("ERR User log out")){
                            runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), isDelete, Toast.LENGTH_SHORT).show();
                            });
                        }else if(loginSuccessful.contains("450")){
                            runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), isNotExist, Toast.LENGTH_SHORT).show();
                            });
                        }
                        else {
                            // 登录失败
                            runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                            });
                        }
                    } else {
                        // 连接失败
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "无法连接到 POP3 服务器", Toast.LENGTH_SHORT).show();
                        });
                    }

                }








            }).start();
        }
    }

    private void sendLoginSuccessEmail(String account) {
        // 发送登录成功的邮件
        String recipient = account;
        String subject = "Login Success";
        String body = "Dear " + account + ",\n\nYou have successfully logged in!";
        // TODO: 通过 SMTP 发送邮件
    }
}
