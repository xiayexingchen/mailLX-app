package com.example.mail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.R;
import com.example.mail.api.SharedPreferencesUtil;
import com.example.mail.entity.Mail;
import com.example.mail.entity.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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

public class AddActivity extends AppCompatActivity {



    private EditText etTo;
    private EditText etTitle;
    private EditText etContent;
    private EditText etAuthorizition;
    private TextView tvFrom;
    private TextView tvFromAccount;
    private Button btSubmit;
    Handler handler;
    private String ip;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // 找到控件
        etTo = findViewById(R.id.to_add);
        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
//        etAuthorizition = findViewById(R.id.authorization);
        tvFrom = findViewById(R.id.from_account);
        tvFromAccount= findViewById(R.id.from_add);
        btSubmit = findViewById(R.id.submit);

        MyApplication application = (MyApplication) this.getApplicationContext();
        ip = application.getNumber();

//        Intent intent = getIntent();
//        int openMode = intent.getIntExtra("mode",1);
//        if(openMode==3) {
//            etContent.setText(intent.getStringExtra("content"));
//        }



        handler = new Handler() {
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
                        tvFrom.setText(user.getNickName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(AddActivity.this);
        tvFromAccount.setText(util.readString("username"));

        btSubmit.setOnClickListener(this::submit);



    }


    public void submit(View v) {
        String senderAddress = tvFromAccount.getText().toString();
        String reciverAddress = etTo.getText().toString();
        String subject = etTitle.getText().toString();
        String content = etContent.getText().toString();

        String ok = "发送成功";
        String err = "发送失败";
        String empty = "空错误";
        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(AddActivity.this);
        String account = util.readString("username");
        new Thread(new Runnable() {
            @Override
            public void run() {
                FormBody.Builder params = new FormBody.Builder();
                try {
                    params.add("senderAddress", senderAddress);
                    params.add("reciverAddress", reciverAddress);
                    params.add("subject", subject);
                    params.add("content", content);
                    String url = "http://10.72.11.179:8080/user/add-mail";
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
                        Intent intent = new Intent(AddActivity.this, HomeActivity.class);
                        startActivity(intent);
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), ok, Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } else {
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

    public void getNickName() {
        String ok = "获取成功";
        String err = "网络错误";
        String empty = "空错误";
        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(AddActivity.this);
        String account = util.readString("username");
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