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
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.example.app.R;

public class RegisterActivity extends AppCompatActivity {

    private Button btnRegister;
    private Button btnLogin;

    private EditText edtEmail;
    private EditText edtAccount;
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
        edtAccount = findViewById(R.id.UserNameEdit);
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
        String nickName = edtAccount.getText().toString();
        String password = edtPassword.getText().toString();
        String ackPassword = edtRePassword.getText().toString();

        String ok = "注册成功";
        String err = "注册失败";
        String empty = "错误，有为空填写内容";

        if (account.length() == 0 || password.length() == 0) {
            Toast.makeText(RegisterActivity.this, empty, Toast.LENGTH_SHORT).show();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                    JSONObject jsonObject = new JSONObject();
                    OkHttpClient httpClient = new OkHttpClient();
                    try {
                        jsonObject.put("account", account);
                        jsonObject.put("password", password);
                        jsonObject.put("ackPassword", ackPassword);
                        jsonObject.put("nickName", nickName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsonObject));
                    String url = "http://"+ip+":8080/register";
                    Request request = new Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build();

                    Call call = httpClient.newCall(request);
                    call.enqueue(new Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.d("whq注册", "失败了");
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
                                if (code == 200) {
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
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
    }
}