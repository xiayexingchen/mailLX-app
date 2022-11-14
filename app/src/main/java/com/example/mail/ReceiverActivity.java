package com.example.mail;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.R;
import com.example.mail.api.OKHttpUtils;
import com.example.mail.api.SharedPreferencesUtil;
import com.example.mail.entity.Mail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReceiverActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private List<Mail> mailList = null;
    private String  userAddress;
    ListView lv;
    Handler handler;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver);

        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(ReceiverActivity.this);
        userAddress = util.readString("username");

        MyApplication application = (MyApplication) this.getApplicationContext();
        ip = application.getNumber();

        handler = new Handler(Looper.getMainLooper()) {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    // 动态更新数据UI界面
                    String str = msg.getData().getString("body") + "";//获取值时相应的类型要对应，传入为String类型用getString；Int类型用getInt。
                    System.out.println(str);
                    try {
                        Gson gson=new Gson();
                        mailList = (List<Mail>) gson.fromJson(str, new TypeToken<List<Mail>>(){}.getType());
                        if(mailList.size()!=0) {
                            MyListDataAdapter adapter = new MyListDataAdapter();
                            lv.setAdapter(adapter);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        initData();
        freshData();

        lv = findViewById(R.id.rlv);

        lv.setOnItemClickListener(this);

        MyListDataAdapter adapter = new MyListDataAdapter();
        lv.setAdapter(adapter);


    }

    private void initData() {
        mailList = new ArrayList<Mail>();
    }

    private void freshData() {

        String ok = "获取成功";
        String err = "网络错误";
        String empty = "邮箱为空";

        new Thread(new Runnable() {
            @Override
            public void run() {
                FormBody.Builder params = new FormBody.Builder();
                try {
                    params.add("username", userAddress);
                    String url = "http://10.72.11.179:8080/user/get-receive-mails";
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
                        Gson gson=new Gson();
                        mailList = (List<Mail>) gson.fromJson(jsonObject1.getString("body"), new TypeToken<List<Mail>>(){}.getType());
                        System.out.println(mailList);
                        Message msg = new Message();//创建信使（很形象的理解）
                        msg.what = 1;//给信使做标记
                        Bundle bundle = new Bundle();//创建放数据的容器

                        bundle.putString("body", jsonObject1.getString("body"));
                        msg.setData(bundle);
                        handler.sendMessage(msg);	// handler传递参数
                    } else if (code == 403){
                        Looper.prepare();
                        Toast.makeText(getApplicationContext(), empty, Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.rlv:
                Mail curMail = (Mail) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(ReceiverActivity.this,DetailsActivity.class);
                intent.putExtra("content",curMail.getBody());
                intent.putExtra("from",curMail.getSenderEmail());
                intent.putExtra("to",curMail.getReceiverEmail());
                intent.putExtra("subject",curMail.getSubject());
                intent.putExtra("date",curMail.getSendTime());
                intent.putExtra("mode",3);
                startActivity(intent);
                break;
        }
    }


    class MyListDataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mailList.size();
        }

        @Override
        public Object getItem(int i) {
            return mailList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MyListViewHolder viewHolder;
            if (view == null) {
                view = View.inflate(ReceiverActivity.this, R.layout.list_item, null);
                viewHolder = new MyListViewHolder();
                viewHolder.sender_name = view.findViewById(R.id.sender_name);
                viewHolder.subject = view.findViewById(R.id.subject);
                viewHolder.content= view.findViewById(R.id.content);
                viewHolder.receiverDate = view.findViewById(R.id.receiverDate);
                view.setTag(viewHolder);
            } else {
                viewHolder = (MyListViewHolder) view.getTag();
            }

            Mail mail = mailList.get(i);
            System.out.println(mail);
            viewHolder.sender_name.setText(mail.getSenderEmail());
            viewHolder.subject.setText(mail.getSubject());
            viewHolder.content.setText(mail.getBody());
            viewHolder.receiverDate.setText(mail.getReceiverEmail());

            return view;
        }

        class MyListViewHolder {
            TextView sender_name, subject, receiverDate,content;
        }
    }
}
