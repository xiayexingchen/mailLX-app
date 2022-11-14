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


        MyApplication application = (MyApplication) this.getApplicationContext();
//setNumber填入服务端ip
        application.setNumber("10.72.11.179");
//取出变量
        ip = application.getNumber();


        //实现跳转
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(this::login);
    }

    public void login(View v) {
        //获取输入的账户和密码
        String account = etAccount.getText().toString();
        String password = etPassword.getText().toString();

        String ok = "登录成功";
        String err = "密码或账号有误，请重新登录";
        String empty = "错误，密码或账号为空";
        String isNotExist = "用户不存在";
        String isDelete = "用户被禁用";

        if (account.length() == 0 || password.length() == 0) {
            Toast.makeText(MainActivity.this, empty, Toast.LENGTH_SHORT).show();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FormBody.Builder params = new FormBody.Builder();
                    try {
                        params.add("username", account);
                        params.add("password", password);
                        String url = "http://10.72.11.179:8080/user/login";
                        Request request = new Request.Builder()
                                .url(url)
                                .post(params.build())
                                .build();

                        OkHttpClient httpClient = new OkHttpClient();
                        Response response = httpClient.newCall(request).execute();
                        String MyResult = response.body().string();
                        JSONObject jsonObject1 = new JSONObject(MyResult);
                        int code = jsonObject1.getInt("state");
                        System.out.println(code);
                        if (code == 200) {
                            SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(MainActivity.this);
                            util.putBoolean("isLogin", true);
                            util.putString("username", account);


                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);

                            startActivity(intent);
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), ok, Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        } else if (code == 404) {
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), isNotExist, Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        } else if (code == 414) {
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), isDelete, Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        else {
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }
}