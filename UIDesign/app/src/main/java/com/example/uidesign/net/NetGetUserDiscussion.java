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

public class NetGetUserDiscussion {
    private static final String SCHEME = "http";
    private static final String FORMAT = "host:30010/discuss/pull_userdiscuss";
    private static final String PATH_SEGMENTS = "discuss/pull_userdiscuss";

    //返回结果
    public static final NetGetUserDiscussion.ResponseClass FAIL = null;

    //结果信息
    public static final String SUCCESS_INFO = "刷新成功";
    public static final String FAIL_INFO = "刷新失败";

    public static class RequestClass
    {
        public int ouid;
        public int suid;
    }

    //返回的一个帖子的信息
    public class ResponseItem {
        public int discussID;
        public int uid;
        public String nickname;
        public String disCont;
        public int disLikes;
        public long disTime;
        public int bool_like;
    }
    //返回的所有信息
    public static class ResponseClass
    {
        public ArrayList<ResponseItem> discussionArray;
    }

    public NetGetUserDiscussion.ResponseClass getDiscussion(int ouid,int suid) {

        NetGetUserDiscussion.ResponseClass responseClass = new NetGetUserDiscussion.ResponseClass();
        responseClass.discussionArray = new ArrayList<ResponseItem>();

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(NetSettings.HOST_1).port(NetSettings.PORT_1).addPathSegments(PATH_SEGMENTS)
                .build();
        Log.v("httpUrl",url.toString());

        NetGetUserDiscussion.RequestClass requestClass=new NetGetUserDiscussion.RequestClass();
        requestClass.ouid = ouid;
        requestClass.suid = suid;

        Gson gson_pull = new Gson();
        String requestJson = gson_pull.toJson(requestClass);
        Log.v("user-dis-req",requestJson);

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
                return null;
            }
            String responseJson = responseBody.string();
            Log.v("user-dis-res",responseJson);
            JsonArray jsonElements = JsonParser.parseString(responseJson).getAsJsonArray();
            Gson gson_get = new Gson();
            for(JsonElement e:jsonElements)
            {
                NetGetUserDiscussion.ResponseItem temp = gson_get.fromJson(e, NetGetUserDiscussion.ResponseItem.class);
                responseClass.discussionArray.add(temp);

            }
            return responseClass;
        }catch (IOException e)
        {
            return null;
        }

    }
}

