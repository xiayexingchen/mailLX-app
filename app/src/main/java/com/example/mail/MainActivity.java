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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



import com.example.mail.Pop3Helper;

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
        application.setNumber("10.0.2.2");
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



                // 连接到 POP3 服务器
                if (pop3Helper.connectToPop3(ip, 110)) {
                    // 使用 POP3 登录验证
                    boolean loginSuccessful = pop3Helper.login(account, password);

                    if (loginSuccessful) {
                        // 连接 SMTP 服务器
                        if(smtpHelper.connectToSmtp(ip, 25))
                        {
                            smtpHelper.login(account, password);
                        }
                        // 登录成功，跳转到主页面
                        runOnUiThread(() -> {
                            // 发送登录成功邮件
                            sendLoginSuccessEmail(account);

                            // 保存登录状态
                            SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(MainActivity.this);
                            util.putBoolean("isLogin", true);
                            util.putString("username", account);

                            // 跳转到主页
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);

                            // 显示登录成功信息
                            Toast.makeText(getApplicationContext(), ok, Toast.LENGTH_SHORT).show();
                        });
                    } else {
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
