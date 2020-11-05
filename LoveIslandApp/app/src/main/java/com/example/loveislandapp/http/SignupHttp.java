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

//注册http处理器
public class SignupHttp {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final String url
            = "http://10.0.2.2:20000";
    public static final String type
            ="signup";

    private OkHttpClient client = new OkHttpClient();

    public class SignupResult
    {
        public boolean success;
        public boolean nickNameUsed;
        public boolean signuped;
        public boolean exception;

        public SignupResult(boolean s,boolean si,boolean n,boolean e)
        {
            success=s;
            signuped=si;
            nickNameUsed=n;
            exception=e;
        }
    }

    public class SignupRequestContent
    {
        public String username;
        public String password;
        public String nickname;
        public boolean male;
        public String school;
    }

    public class SignupResponseContent
    {
        public boolean success;
        public boolean nickNameUsed;
        public boolean signuped;
    }

    public SignupResult signup(String username,String password,String nickname,boolean male,String school)
    {
        SignupRequestContent signupRequestContent=new SignupRequestContent();
        signupRequestContent.username=username;
        signupRequestContent.password=password;
        signupRequestContent.nickname=nickname;
        signupRequestContent.male=male;
        signupRequestContent.school=school;

        HttpFormat requestHttp=new HttpFormat();
        requestHttp.type=type;
        requestHttp.content=signupRequestContent;

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
            if(responseBody==null) //报文体为空
            {
                return new SignupResult(false,false,false,true);
            }
            HttpFormat responseHttp=gson.fromJson(responseBody.string(),HttpFormat.class);
            if(responseHttp.type=="signup")
            {
                SignupResponseContent signupResponseContent= (SignupResponseContent) responseHttp.content;
                return new SignupResult(signupResponseContent.success,signupResponseContent.nickNameUsed,
                        signupResponseContent.signuped,false);
            }else
            {
                return new SignupResult(false,false,false,true);
            }


        }catch(IOException e)
        {
            return new SignupResult(false,false,false,true);
        }
    }
}
