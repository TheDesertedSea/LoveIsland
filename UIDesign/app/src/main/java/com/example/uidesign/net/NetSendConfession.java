package com.example.uidesign.net;

import android.util.Log;

import com.example.uidesign.data.LogginedUser;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetSendConfession {

    private static final String SCHEME = "http";
    private static final String FORMAT = "host:30010/forum/push";
    private static final String HOST = "192.168.1.100";
    private static final int PORT = 30010;
    private static final String PATH_SEGMENTS = "forum/push";

    //返回结果
    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";

    //结果信息
    public static final String SUCCESS_INFO = "发布成功";
    public static final String FAIL_INFO = "发布失败";

    public static class RequestClass
    {
        public int uid;
        //这里的String以后可以改成类，类里面放入文本和图片
        public String confcont;
    }

    public static class ResponseClass
    {
        public String result_code;
    }

    public String sendConfession(int uid, String content_text) {

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(HOST).port(PORT).addPathSegments(PATH_SEGMENTS)
                .build();
        Log.v("httpUrl",url.toString());

        NetSendConfession.RequestClass requestClass = new NetSendConfession.RequestClass();
        requestClass.uid = uid;
        requestClass.confcont = content_text;

        Gson gson = new Gson();
        String requestJson = gson.toJson(requestClass);

        RequestBody requestBody = RequestBody.create(requestJson, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", LogginedUser.getInstance().getToken())
                .post(requestBody)
                .build();

        try
        {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if(responseBody == null)
            {
                return FAIL;
            }
            String responseJson = responseBody.string();
            NetSendConfession.ResponseClass responseClass = gson.fromJson(responseJson, NetSendConfession.ResponseClass.class);
            if(responseClass.result_code == "success")
            {
                return SUCCESS;
            }
            if(responseClass.result_code == "fail")
            {
                return FAIL;
            }
            return FAIL;
        }catch (IOException e)
        {
            return FAIL;
        }

    }
}
