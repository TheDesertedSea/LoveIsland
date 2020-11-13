package com.example.loveislandapp.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Set;

//缓存成功登录的信息
public class CachedLoginInfo {

    private SharedPreferences sharedPreferences;

    private String username; //用户名
    private String password; //密码

    //构造函数中获取缓存数据并存储
    public CachedLoginInfo(Context context)
    {
        sharedPreferences=context.getSharedPreferences("logininfo",Context.MODE_PRIVATE);
        username=sharedPreferences.getString("username","");
        password=sharedPreferences.getString("password","");
    }

    public String getUsername(){return username;}
    public String getPassword(){return password;}

    //缓存新数据
    public void cacheLoginInfo(String username,String password)
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("username",username);
        editor.putString("password",password);
        editor.apply();
    }

    //清空数据
    public void clear()
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
