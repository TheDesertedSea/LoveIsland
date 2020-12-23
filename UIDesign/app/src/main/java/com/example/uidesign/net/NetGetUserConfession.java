package com.example.uidesign.net;

import android.util.Log;

import com.example.uidesign.data.LogginedUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetGetUserConfession {
    private static final String SCHEME = "http";
    private static final String FORMAT = "host:30010/forum/pull_userconf";
    private static final String HOST = "";
    private static final int PORT = 30010;
    private static final String PATH_SEGMENTS = "forum/pull_userconf";

    //返回结果
    public static final NetGetUserConfession.ResponseClass FAIL = null;

    //结果信息
    public static final String SUCCESS_INFO = "刷新成功";
    public static final String FAIL_INFO = "刷新失败";

    public static class RequestClass
    {
        public int get_uid;   //请求的账号id
        public int local_uid; //本地账号的id
    }

    //返回的一个帖子的信息
    public class ResponseItem {
        public int commentID;
        public int uid;
        public String content;
        public int likes;
    }
    //返回的所有信息
    public static class ResponseClass
    {
        public int maxID;
        public ArrayList<NetGetConfession.ResponseItem> confessionArray;
    }

    public NetGetConfession.ResponseClass getConfession(int commentID, int uid) {

        NetGetConfession.ResponseClass responseClass = new NetGetConfession.ResponseClass();
        responseClass.confessionArray = new ArrayList<NetGetConfession.ResponseItem>();
        responseClass.maxID = 0;

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(HOST).port(PORT).addPathSegments(PATH_SEGMENTS)
                .build();
        Log.v("httpUrl",url.toString());

        NetGetConfession.RequestClass requestClass=new NetGetConfession.RequestClass();
        requestClass.commentID = commentID;
        requestClass.uid = uid;

        Gson gson_pull = new Gson();
        String requestJson = gson_pull.toJson(requestClass);

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
                return null;
            }
            String responseJson = responseBody.string();
            JsonArray jsonElements = JsonParser.parseString(responseJson).getAsJsonArray();
            Gson gson_get = new Gson();
            for(JsonElement e:jsonElements)
            {
                NetGetConfession.ResponseItem temp = gson_get.fromJson(e, NetGetConfession.ResponseItem.class);
                responseClass.confessionArray.add(temp);

            }
            return responseClass;
        }catch (IOException e)
        {
            return null;
        }

    }
}
