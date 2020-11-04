package com.example.loveislandapp.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import java.util.Map;
import java.util.Set;

public class CachedLoginInfo {

    private SharedPreferences sharedPreferences;

    private String username;
    private String password;

    public CachedLoginInfo(Context context)
    {
        sharedPreferences=context.getSharedPreferences("logininfo",Context.MODE_PRIVATE);
        username=sharedPreferences.getString("username","");
        password=sharedPreferences.getString("password","");
    }
    public String getUsername(){return username;}
    public String getPassword(){return password;}

    public void cacheLoginInfo(String username,String password)
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("username",username);
        editor.putString("password",password);
        editor.apply();
    }

    public void clear()
    {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
