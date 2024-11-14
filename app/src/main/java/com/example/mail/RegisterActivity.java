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

import com.example.app.R;
import com.example.mail.api.SharedPreferencesUtil;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private Button btnLogin;

    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtRePassword;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 找到控件
        btnRegister = findViewById(R.id.SignUpButton);
        btnLogin = findViewById(R.id.BackLoginButton);
        edtEmail = findViewById(R.id.EmailEdit);
        edtPassword = findViewById(R.id.PassWordEdit);
        edtRePassword = findViewById(R.id.PassWordAgainEdit);

        MyApplication application = (MyApplication) this.getApplicationContext();
        ip = application.getNumber();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getApplicationContext(), "ok", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(this::register);
    }

    public void register(View v) {
        //获取输入的账户和密码
        String account = edtEmail.getText().toString();
        String password = edtPassword.getText().toString();
        String ackPassword = edtRePassword.getText().toString();

        String ok = "注册成功";
        String err = "注册失败，两次密码不一致";
        String empty = "错误，";
        String isExist = "用户已存在";

        if (account.length() == 0 || password.length() == 0) {
            Toast.makeText(RegisterActivity.this, empty, Toast.LENGTH_SHORT).show();
        } else if (!password.equals(ackPassword)) {
            Toast.makeText(RegisterActivity.this, err, Toast.LENGTH_SHORT).show();
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    FormBody.Builder params = new FormBody.Builder();
                    try {
                        params.add("username", account);
                        params.add("password", password);
                        String url = "http://10.0.2.2:8080/register";
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
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), ok, Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        } else if (code == 413) {
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), isExist, Toast.LENGTH_SHORT).show();
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