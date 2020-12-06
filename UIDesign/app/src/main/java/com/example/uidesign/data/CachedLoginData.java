package com.example.uidesign.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class CachedLoginData {
    private SharedPreferences sp;
    public CachedLoginData(Context context)
    {
        sp= context.getApplicationContext().getSharedPreferences("CachedLoginData",Context.MODE_PRIVATE);
    }
    public Map<String,String> readCachedLoginData(){
        Map<String,String> data=new HashMap<String,String>();
        data.put("username",sp.getString("username",""));
        data.put("password",sp.getString("password",""));
        return data;
    }

    public void saveCachedLoginData(String username,String password){
        SharedPreferences.Editor editor=sp.edit();
        editor.putString("username",username);
        editor.putString("password",password);
        editor.apply();
    }
}
