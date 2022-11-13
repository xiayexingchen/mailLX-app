package com.example.mail.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

//缓存文件工具类
public class SharedPreferencesUtil {

    //静态实例
    private static SharedPreferencesUtil sharedPreferencesUtil;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String FILENAME = "mail_user";

    //私有构造函数
    private SharedPreferencesUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(FILENAME,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    //静态方法获取静态实例
    public static SharedPreferencesUtil getInstance(Context context) {
        if(sharedPreferencesUtil==null) {
            synchronized (SharedPreferencesUtil.class) {
                if(sharedPreferencesUtil==null) {
                    sharedPreferencesUtil = new SharedPreferencesUtil(context);
                }
            }
        }
        return sharedPreferencesUtil;
    }

    //工具类方法
    public void putBoolean(String key,Boolean value) {
        editor.putBoolean(key,value);
        editor.commit();
    }

    public void putString(String key,String value) {
        editor.putString(key,value);
        editor.commit();
    }

    public boolean readBoolean(String key) {
        return sharedPreferences.getBoolean(key,false);
    }

    public String readString(String key) {
        return sharedPreferences.getString(key,"");
    }

    public Object readObject(String key,Class clazz) {
       String str = sharedPreferences.getString(key,"");
        Gson gson =  new Gson();
        return gson.fromJson(str,clazz);
    }

    public void delete(String key) {
        editor.remove(key).commit();
    }

    public void clear() {
        editor.clear().commit();
    }
}
