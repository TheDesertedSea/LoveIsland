package com.example.uidesign.net;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetLogin {
    private static final boolean DEBUG=true;

    private static final String format=":30010/login/login?mailbox=****&password=****&time=****";
    private static final String serverIp="";
    private static final String baseUrl=serverIp+":30010/login/login";

    //结果码
    public static final int OK=0;
    public static final int DUPLICATE_LOGIN=1;
    public static final int INFO_WRONG=2;
    public static final int OTHER_FAIL=3;

    //报错信息
    public static final String ERROR_DUPLICATE_LOGIN="重复登录，已将之前登录设备下线";
    public static final String ERROR_INFO_WRONG="用户名或密码错误";
    public static final String ERROR_OTHER_FAIL="登录失败";

    public class ResponseClass
    {
        
    }

    public int login(String username,String password)
    {
        if(DEBUG)
        {
            return OK;
        }

        OkHttpClient client=new OkHttpClient();

        String url=baseUrl+"?mailbox="+username+"&password="+password+"&time="+System.currentTimeMillis();

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
            gson.fromJson(responseJson,)
        }catch (IOException e)
        {

        }



    }


}
