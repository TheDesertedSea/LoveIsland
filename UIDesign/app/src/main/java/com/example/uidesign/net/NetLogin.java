package com.example.uidesign.net;

import android.util.Log;

import com.example.uidesign.data.CachedLoginData;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.data.UserSocketManager;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetLogin {
    private static final boolean DEBUG=true;

    private static final String SCHEME="http";
    private static final String FORMAT="host:30010/login/login?mailbox=****&password=****&time=****";
    private static final String HOST="";
    private static final int PORT=30010;
    private static final String PATH_SEGMENTS="login/login";

    //结果码
    public static final int OK=0;
    public static final int DUPLICATE_LOGIN=1;
    public static final int INFO_WRONG=2;
    public static final int OTHER_FAIL=3;
    public static final int GO_TO_COLD_BOOT=4;

    //报错信息
    public static final String ERROR_DUPLICATE_LOGIN="重复登录，已将之前登录设备下线";
    public static final String ERROR_INFO_WRONG="用户名或密码错误";
    public static final String ERROR_OTHER_FAIL="登录失败";

    public static class ResponseClass
    {
        public int user;
        public Object content;
    }

    public static class SuccessContent
    {
        public int uid;
        public String nickName;
        public String token;
        public String host;
        public int port;
        public boolean bColdBooted;
    }

    public int login(String username,String password)
    {
        if(DEBUG)
        {
            return OK;
        }

        OkHttpClient client=new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(HOST).port(PORT).addPathSegments(PATH_SEGMENTS)
                .addQueryParameter("mailbox",username).addQueryParameter("password",password)
                .addQueryParameter("time",
                        String.valueOf(System.currentTimeMillis())).build();
        Log.v("httpUrl",url.toString());

        Request request = new Request.Builder()
                .url(url)
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
            Gson gson=new Gson();
            ResponseClass responseClass=gson.fromJson(responseJson,ResponseClass.class);
            if(responseClass.user==0)
            {
                return INFO_WRONG;
            }
            SuccessContent successContent=(SuccessContent)responseClass.content;
            LogginedUser.getInstance().setUid(successContent.uid);
            LogginedUser.getInstance().setNickName(successContent.nickName);
            LogginedUser.getInstance().setToken(successContent.token);
            UserSocketManager.getInstance().connect(successContent.host,successContent.port);
            if(!successContent.bColdBooted)
            {
                return GO_TO_COLD_BOOT;
            }
            return OK;
        }catch (IOException e)
        {
            return OTHER_FAIL;
        }

    }

}
