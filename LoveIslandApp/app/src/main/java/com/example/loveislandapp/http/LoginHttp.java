package com.example.loveislandapp.http;

import android.util.Log;

import com.example.loveislandapp.data.HttpFormat;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

//登录http处理器
public class LoginHttp {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final String url
            = "http://192.168.1.102:30010/login";
    public static final String url2
            = "http://10.0.2.2:20000";

    private OkHttpClient client = new OkHttpClient();

    public class LoginResult
    {
        public boolean success;
        public boolean wrong;
        public boolean logined;
        public boolean exception;
        public LoginResult(boolean s,boolean w,boolean l,boolean i)
        {
            success=s;
            wrong=w;
            logined=l;
            exception=i;
        }
    }
    public class LoginRequestContent
    {
        public String name;
        public String pwd;
    }
    public class LoginResponseContent
    {
        public boolean success;
        public boolean wrong;
        public boolean logined;
    }

    public LoginResult Login(String username,String password)
    {
        LoginRequestContent loginRequestContent=new LoginRequestContent();
        loginRequestContent.name=username;
        loginRequestContent.pwd=password;

        Gson gson=new Gson();
        String json=gson.toJson(loginRequestContent);

        RequestBody body = RequestBody.create(json, JSON);
//        Request request = new Request.Builder()
//                .url(url)
//                .get()
//                .addHeader("name",username)
//                .addHeader("pwd",password)
//                .build();


        Request request=new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try{
            Log.v("here","here");
            Response response = client.newCall(request).execute();

//            ResponseBody responseBody=response.body();
//
//            if(responseBody==null)  //报文体为空
//            {
//                return new LoginHttp.LoginResult(false,false,false,true);
//            }
//            HttpFormat responseHttp=gson.fromJson(responseBody.string(),HttpFormat.class);
//            if(responseHttp.type=="login")
//            {
//                LoginResponseContent loginResponseContent= (LoginResponseContent) responseHttp.content;
//                return new LoginResult(loginResponseContent.success,loginResponseContent.wrong,loginResponseContent.logined,false);
//            }else
//            {
//                return new LoginResult(false,false,false,true);
//            }

            return new LoginResult(false,false,false,true);
        }catch(IOException e)
        {
            return new LoginResult(false,false,false,true);
        }

    }
}
