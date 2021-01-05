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

import static android.content.ContentValues.TAG;

public class NetSendDiscussion {
    private static final String SCHEME = "http";
    private static final String FORMAT = "host:30010/discuss/push";
    private static final String PATH_SEGMENTS = "discuss/push";

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
        public String discont;
        public long nowDate;
    }

    public static class ResponseClass
    {
        public int success;
    }

    public String sendDiscussion(int uid, String content_text, long date) {

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(NetSettings.HOST_1).port(NetSettings.PORT_1).addPathSegments(PATH_SEGMENTS)
                .build();
        Log.v("httpUrl",url.toString());

        NetSendDiscussion.RequestClass requestClass = new RequestClass();
        requestClass.uid = uid;
        requestClass.discont = content_text;
        requestClass.nowDate = date;

        Gson gson = new Gson();
        String requestJson = gson.toJson(requestClass);

        RequestBody requestBody = RequestBody.create(requestJson, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
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
            NetSendDiscussion.ResponseClass responseClass = gson.fromJson(responseJson, NetSendDiscussion.ResponseClass.class);
            if(responseClass.success == 1)
            {
                return SUCCESS;
            }
            else
            {
                return FAIL;
            }
        }catch (IOException e)
        {
            return FAIL;
        }

    }
}
