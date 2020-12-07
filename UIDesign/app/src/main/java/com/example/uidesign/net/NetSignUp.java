package com.example.uidesign.net;

import android.util.Log;

import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.data.UserSocketManager;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetSignUp {
    private static final boolean DEBUG=true;

    private static final String SCHEME="http";
    private static final String FORMAT="host:30010/login/register";
    private static final String HOST="";
    private static final int PORT=30010;
    private static final String PATH_SEGMENTS="login/register";

    //结果码
    public static final int OK=0;
    public static final int DUPLICATE_SIGN_UP=1;
    public static final int OTHER_FAIL=2;

    //结果信息
    public static final String OK_INFO="注册成功";
    public static final String DUPLICATE_SIGN_UP_INFO="该邮箱已经被注册过了";
    public static final String OTHER_FAIL_INFO="注册失败";

    public static class RequestClass
    {
        public String username;
        public String password;
        public String nickName;
        public boolean sex;
        public String school;
    }

    public static class ResponseClass
    {
        public int result_code;
    }

    public int signUp(String username,String password,String nickName,boolean sex,String school)
    {
        if(DEBUG)
        {
            return OK;
        }

        OkHttpClient client=new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(HOST).port(PORT).addPathSegments(PATH_SEGMENTS)
                .build();
        Log.v("httpUrl",url.toString());

        RequestClass requestClass=new RequestClass();
        requestClass.username=username;
        requestClass.password=password;
        requestClass.nickName=nickName;
        requestClass.sex=sex;
        requestClass.school=school;

        Gson gson=new Gson();
        String requestJson=gson.toJson(requestClass);

        RequestBody requestBody=RequestBody.create(requestJson, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try
        {
            Response response=client.newCall(request).execute();
            ResponseBody responseBody=response.body();
            if(responseBody==null)
            {
                return OTHER_FAIL;
            }
            String responseJson=responseBody.string();
            ResponseClass responseClass=gson.fromJson(responseJson, ResponseClass.class);
            if(responseClass.result_code==0)
            {
                return DUPLICATE_SIGN_UP;
            }
            if(responseClass.result_code==2)
            {
                return OTHER_FAIL;
            }
            return OK;
        }catch (IOException e)
        {
            return OTHER_FAIL;
        }

    }
}
