package com.example.mail.api;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OKHttpUtils {
    private OKHttpUtils() {
    }
    ;
    private static OKHttpUtils instance = new OKHttpUtils();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Handler handler = new Handler(Looper.getMainLooper());

    public static OKHttpUtils getInstance() {
        return instance;
    }

    public void doGet(String url, CallBack callBack) {
        Request request = new Request.Builder().url(url).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     callBack.onError(e);
                                 }
                             }
                );
            }

            @Override
            public void onResponse(Call call, Response response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onError(e);
                        }
                    });
                }
                String finalString = string;
                handler.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     callBack.onSuccess(finalString);
                                 }
                             }
                );
            }
        });
    }

    public void doPost(String url, RequestBody requestBody,CallBack callBack) {
        Request request = new Request.Builder().url(url).post(requestBody).build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     callBack.onError(e);
                                 }
                             }
                );
            }

            @Override
            public void onResponse(Call call, Response response) {
                String string = null;
                try {
                    string = response.body().string();
                } catch (IOException e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onError(e);
                        }
                    });
                }
                String finalString = string;
                handler.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     callBack.onSuccess(finalString);
                                 }
                             }
                );
            }
        });
    }

}
