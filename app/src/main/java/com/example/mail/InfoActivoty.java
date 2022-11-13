package com.example.mail;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.R;
import com.example.mail.api.SharedPreferencesUtil;
import com.example.mail.entity.User;
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

    private EditText edaccount;
    private EditText edpassword;
    private EditText ednickname;
    private EditText edauthorizationcode;
    private Button btchange;
    Handler handler;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_activoty);

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
                        Gson gson = new Gson();
                        User user = gson.fromJson(jsonObject1.getString("data"), new TypeToken<User>() {
                        }.getType());
                        System.out.println(user);
                        edaccount.setText(user.getAccount());
                        edpassword.setText(user.getPassword());
                        ednickname.setText(user.getNickName());
                        edauthorizationcode.setText(user.getAuthorizationCode());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        edaccount = findViewById(R.id.baccount);
        edpassword = findViewById(R.id.bpassword);
        ednickname = findViewById(R.id.bnickname);
        edauthorizationcode = findViewById(R.id.bauthorization);
        btchange = findViewById(R.id.changeinfo);

        getNickName();

        btchange.setOnClickListener(this::change);
    }

    public void change(View v) {
        //获取输入的账户和密码
        String account = edaccount.getText().toString();
        String password = edpassword.getText().toString();
        String nickname = ednickname.getText().toString();
        String authorizationCode = edauthorizationcode.getText().toString();

        String ok = "修改成功";
        String err = "修改失败";
        String empty = "空错误";
        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                JSONObject jsonObject = new JSONObject();
                OkHttpClient httpClient = new OkHttpClient();
                try {
                    jsonObject.put("account", account);
                    jsonObject.put("password",password);
                    jsonObject.put("nickName", nickname);
                    jsonObject.put("authorizationCode", authorizationCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsonObject));
                String url = "http://"+ip+":8080/people/updatePeopleMsg";
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
                            int code = jsonObject1.getInt("code");
                            System.out.println(code);
                            if(code==200) {
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(), ok, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } else {
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        }).start();
    }


    public void getNickName() {
        String ok = "获取成功";
        String err = "网络错误";
        String empty = "空错误";
        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(InfoActivoty.this);
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
                            int code = jsonObject1.getInt("code");
                            System.out.println(code);
                            Message msg = new Message();//创建信使（很形象的理解）
                            msg.what = 1;//给信使做标记
                            Bundle bundle = new Bundle();//创建放数据的容器

                            bundle.putString("res", MyResult);
                            msg.setData(bundle);
                            handler.sendMessage(msg);    // handler传递参数
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
            }
        }).start();
    }
}