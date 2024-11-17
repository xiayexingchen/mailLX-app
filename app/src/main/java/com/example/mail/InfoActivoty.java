package com.example.mail;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.R;
import com.example.mail.api.SharedPreferencesUtil;
import com.example.mail.entity.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class InfoActivoty extends AppCompatActivity {

    private EditText baccount;
    private EditText bpassword;
    private EditText ackBpassword;
    private EditText bnickname;
    private EditText bauthorization;
    private Button changeinfo;
    private TextView returnbtn;
    private ImageButton editPasswordBtn;
    private TextView confirmPasswordLabel;
    private String ip;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_activoty);

        // 初始化IP地址
        MyApplication application = (MyApplication) getApplicationContext();
        ip = application.getNumber();

        // 初始化控件
        initViews();
        // 初始化Handler
        initHandler();
        // 设置点击事件
        setClickListeners();

    }

    private void initViews() {
        baccount = findViewById(R.id.baccount);
        bpassword = findViewById(R.id.bpassword);
        ackBpassword = findViewById(R.id.ackBpassword);
        // bnickname = findViewById(R.id.bnickname);
        // bauthorization = findViewById(R.id.bauthorization);
        changeinfo = findViewById(R.id.changeinfo);
        returnbtn = findViewById(R.id.returnbtn);
        editPasswordBtn = findViewById(R.id.editPasswordBtn);
        confirmPasswordLabel = findViewById(R.id.confirmPasswordLabel);

        // 默认隐藏确认密码相关控件
        ackBpassword.setVisibility(View.GONE);
        confirmPasswordLabel.setVisibility(View.GONE);
        changeinfo.setVisibility(View.GONE);

        //初状态显示
        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(InfoActivoty.this);
        baccount.setText(util.readString("username"));
        bpassword.setText(util.readString("password"));
    }

    private void initHandler() {
        handler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    String response = msg.getData().getString("res");
                    Log.d("infoTest", "msg:: " +response);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        int code = jsonObject.getInt("code");
                        if (code == 200) {
                            Gson gson = new Gson();
                            User user = gson.fromJson(jsonObject.getString("data"),
                                    new TypeToken<User>() {}.getType());
                            updateUI(user);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    private void setClickListeners() {
        // 返回按钮点击事件
        returnbtn.setOnClickListener(v -> finish());

        // 编辑密码按钮点击事件
        editPasswordBtn.setOnClickListener(v -> {
            ackBpassword.setVisibility(View.VISIBLE);
            confirmPasswordLabel.setVisibility(View.VISIBLE);
            changeinfo.setVisibility(View.VISIBLE);
        });

        // 保存修改按钮点击事件
        changeinfo.setOnClickListener(this::updateUserInfo);
    }

    private void updateUI(User user) {
        baccount.setText(user.getAccount());
        bpassword.setText(user.getPassword());
    }


    private void updateUserInfo(View v) {
        String account = baccount.getText().toString();
        String password = bpassword.getText().toString();
        String confirmPassword = ackBpassword.getText().toString();

        new Thread(() -> {
            try {
                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                JSONObject jsonObject = new JSONObject();
                SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(InfoActivoty.this);
                jsonObject.put("username", account);
                jsonObject.put("password", password);
                jsonObject.put("newPassword",confirmPassword);

                OkHttpClient client = new OkHttpClient();
                RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url("http://" + "10.0.2.2:8080" + "/update-password")
                        .post(requestBody)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(() -> Toast.makeText(InfoActivoty.this,
                                "修改失败", Toast.LENGTH_SHORT).show());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String result = response.body().string();
                        Log.d("infoTest", "msg:: " +result);
                        try {
                            JSONObject jsonObject1 = new JSONObject(result);
                            int code = jsonObject1.getInt("state");
                            runOnUiThread(() -> {
                                if (code == 200) {
                                    Toast.makeText(InfoActivoty.this,
                                            "修改成功", Toast.LENGTH_SHORT).show();
                                    util.putString("password",confirmPassword);
                                    // 隐藏确认密码相关控件
                                    ackBpassword.setVisibility(View.GONE);
                                    confirmPasswordLabel.setVisibility(View.GONE);
                                    changeinfo.setVisibility(View.GONE);
                                } else {
                                    Toast.makeText(InfoActivoty.this,
                                            "修改失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }
}