package com.example.loveislandapp.http;

import com.example.loveislandapp.data.HttpFormat;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginHttp {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final String url
            = "http://10.0.2.2:20000";

    OkHttpClient client = new OkHttpClient();

    public class LoginResult
    {
        public boolean success;
        public boolean logined;
        public boolean exception;
        public LoginResult(boolean s,boolean l,boolean i)
        {
            success=s;
            logined=l;
            exception=i;
        }
    }
    public class LoginRequestContent
    {
        public String username;
        public String password;
    }
    public class LoginResponseContent
    {
        public boolean success;
        public boolean logined;
    }

    public LoginResult Login(String username,String password)
    {
        LoginRequestContent loginRequestContent=new LoginRequestContent();
        loginRequestContent.username=username;
        loginRequestContent.password=password;

        HttpFormat requestHttp=new HttpFormat();
        requestHttp.type="login";
        requestHttp.content=loginRequestContent;

        Gson gson=new Gson();
        String json=gson.toJson(requestHttp);

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try{
            Response response = client.newCall(request).execute();
            ResponseBody responseBody=response.body();
            HttpFormat responseHttp=gson.fromJson(responseBody.string(),HttpFormat.class);
            if(responseHttp.type=="login")
            {
                LoginResponseContent loginResponseContent= (LoginResponseContent) responseHttp.content;
                return new LoginResult(loginResponseContent.success,loginResponseContent.logined,false);
            }else
            {
                return new LoginResult(false,false,true);
            }


        }catch(IOException e)
        {
            return new LoginResult(false,false,true);
        }

    }
}
