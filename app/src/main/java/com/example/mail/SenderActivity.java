package com.example.mail;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.R;
import com.example.mail.api.SharedPreferencesUtil;
import com.example.mail.entity.Mail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SenderActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private List<Mail> mailList = null;
    ListView lv;
    String userAddress;
    Handler handler;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);

        SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(SenderActivity.this);
        userAddress = util.readString("user");

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
                        List<Mail> mails = gson.fromJson(jsonObject1.getString("data"), new TypeToken<List<Mail>>(){}.getType());
                        if(mails.size()!=0) {
                            Collections.reverse(mails);
                            mailList = new ArrayList<>();
                            for(Mail i:mails) {
                                mailList.add(i);
                            }
                            System.out.println(mailList.get(0));
                            if(mails.size()!=0) {
                                SenderActivity.MyListDataAdapter adapter = new SenderActivity.MyListDataAdapter();
                                lv.setAdapter(adapter);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        initData();
        freshData();

        lv = findViewById(R.id.rlv_send);

        lv.setOnItemClickListener(this);

        SenderActivity.MyListDataAdapter adapter = new SenderActivity.MyListDataAdapter();
        lv.setAdapter(adapter);

    }

    private void initData() {
        mailList = new ArrayList<Mail>();
        mailList.add(new Mail("from.com", "to.com", Timestamp.valueOf("2022-5-26 14:00:00"), "欢迎使用邮件系统", "用户你好！这是一条系统初始化邮件，仅用用于测试用"));

    }

    private void freshData() {

        String ok = "获取成功";
        String err = "网络错误";
        String empty = "空错误";

        new Thread(new Runnable() {
            @Override
            public void run() {
                MediaType JSON = MediaType.parse("application/json;charset=utf-8");
                JSONObject jsonObject = new JSONObject();
                OkHttpClient httpClient = new OkHttpClient();
                try {
                    jsonObject.put("senderAddress", userAddress);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsonObject));
                String url = "http://"+ip+":8080/mail/querySendMail";
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
                            System.out.println(jsonObject1);
                            int code = jsonObject1.getInt("code");
                            Gson gson=new Gson();
                            List<Mail> mails = gson.fromJson(jsonObject1.getString("data"), new TypeToken<List<Mail>>(){}.getType());
                            for(Mail i:mails) {
                                mailList.add(i);
                            }
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.rlv_send:
                Mail curMail = (Mail) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(SenderActivity.this,DetailsActivity.class);
                intent.putExtra("content",curMail.getBody());
                intent.putExtra("from",curMail.getSenderEmail());
                intent.putExtra("to",curMail.getReceiverEmail());
                intent.putExtra("subject",curMail.getSubject());
                intent.putExtra("date",curMail.getSendTime());
                intent.putExtra("mode",3);
                startActivityForResult(intent,1);
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
                view = View.inflate(SenderActivity.this, R.layout.list_item, null);
                viewHolder = new MyListViewHolder();;
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
            viewHolder.receiverDate.setText(mail.getSendTime().toString());

            return view;
        }

        class MyListViewHolder {
            TextView sender_name, subject, receiverDate,content;
        }
    }


}