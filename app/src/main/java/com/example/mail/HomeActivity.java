package com.example.mail;

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

public class HomeActivity extends AppCompatActivity {

    //lh-本类纯跳转，可直接替换
    private TextView userEmail;
    private LinearLayout writeEmailLayout;
    private LinearLayout inboxLayout;
    private LinearLayout sentLayout;
    private LinearLayout deletedLayout;
    private LinearLayout logoutLayout;

    private String ip;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userEmail = findViewById(R.id.userEmail);
        writeEmailLayout = findViewById(R.id.writeEmailLayout);
        inboxLayout = findViewById(R.id.inboxLayout);
        sentLayout = findViewById(R.id.sentLayout);
        deletedLayout = findViewById(R.id.deletedLayout);
        logoutLayout = findViewById(R.id.logoutLayout);


        //取出变量
        MyApplication application = (MyApplication) this.getApplicationContext();
        ip = application.getNumber();
        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(HomeActivity.this);
        userEmail.setText(util.readString("username"));
        System.out.println(userEmail.getText().toString());

        //实现跳转
        userEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, InfoActivoty.class);
                startActivity(intent);
            }
        });
        //实现跳转
        writeEmailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
        //实现跳转
        inboxLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ReceiverActivity.class);
                startActivity(intent);
            }
        });


        //实现跳转
        sentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SenderActivity.class);
                startActivity(intent);
            }
        });

        //实现跳转
        deletedLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DeletedActivity.class);
                startActivity(intent);
            }
        });
        //退出登录
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pop3Helper pop3Helper = new Pop3Helper();

                new Thread(new Runnable() {
                        @Override
                        public void run() {
                            pop3Helper.closeConnection();
                        }
                    }).start();
             finish();
            }
        });
    }
}


