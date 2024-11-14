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

    private Button btnReceiver;
    private Button btnSender;
    private FloatingActionButton floatingActionButton;
    private TextView tvNickname;
    private String ip;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnReceiver = findViewById(R.id.ReceiverBox);
        btnSender = findViewById(R.id.SenderBox);
        floatingActionButton = findViewById(R.id.Add);
        tvNickname = findViewById(R.id.NickName);

        //取出变量
        MyApplication application = (MyApplication) this.getApplicationContext();
        ip = application.getNumber();

        handler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    // 动态更新数据UI界面
                    String str = msg.getData().getString("res") + "";//获取值时相应的类型要对应，传入为String类型用getString；Int类型用getInt。
                    try {
                        JSONObject jsonObject1 = new JSONObject(str);
                        int code = jsonObject1.getInt("code");
                        Gson gson=new Gson();
                        User user = gson.fromJson(jsonObject1.getString("data"), new TypeToken<User>(){}.getType());
                        System.out.println(user);
                        tvNickname.setText(user.getNickName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

//        getNickName();
        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(HomeActivity.this);
        tvNickname.setText(util.readString("username"));

        //实现跳转
        btnReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ReceiverActivity.class);
                startActivity(intent);
            }
        });

        tvNickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, InfoActivoty.class);
                startActivity(intent);
            }
        });


        //实现跳转
        btnSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SenderActivity.class);
                startActivity(intent);
            }
        });

        //实现跳转
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });
    }

    public void getNickName() {
        String ok = "获取成功";
        String err = "网络错误";
        String empty = "空错误";
        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(HomeActivity.this);
        String account = util.readString("user");
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                JSONObject jsonObject = new JSONObject();
                OkHttpClient httpClient = new OkHttpClient();
                try {
                    jsonObject.put("account", account);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsonObject));
                String url = "http://"+ip+":8080/people/queryPeopleMsg";
                Request request = new Request.Builder()
                        .url(url)
                        .post(requestBody)
                        .build();

                Call call = httpClient.newCall(request);
                call.enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String MyResult = response.body().string();
                        try {
                            JSONObject jsonObject1 = new JSONObject(MyResult);
                            int code = jsonObject1.getInt("state");
                            System.out.println(code);
                            Message msg = new Message();//创建信使（很形象的理解）
                            msg.what = 1;//给信使做标记
                            Bundle bundle = new Bundle();//创建放数据的容器

                            bundle.putString("res",MyResult);
                            msg.setData(bundle);
                            handler.sendMessage(msg);	// handler传递参数
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

}