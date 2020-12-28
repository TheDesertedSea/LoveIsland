package com.example.uidesign.net;

import android.util.Log;

import com.example.uidesign.data.LogginedUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetGetCertainConfession {
    private static final String SCHEME = "http";
    private static final String FORMAT = "host:30010/forum/pull_discuss&likes";
    private static final String HOST = "192.168.1.105";
    private static final int PORT = 30010;
    private static final String PATH_SEGMENTS = "forum/pull_discuss&likes";

    //返回结果
    public static final NetGetCertainConfession.ResponseClass FAIL = null;

    public static class RequestClass
    {
        public int postID;   //帖子的id
        public int uid; //账号的id
    }

    //返回的一个评论的信息
    public class ResponseItem {
        public int commentID;
        public int confessionID;//没用
        public int uid;
        public String content;
        public Date time;
    }
    //返回的所有信息
    public static class ResponseClass
    {
        public ArrayList<NetGetCertainConfession.ResponseItem> commentArray;
    }

    public NetGetCertainConfession.ResponseClass getComment(int postID, int uid) {

        NetGetCertainConfession.ResponseClass responseClass = new NetGetCertainConfession.ResponseClass();
        responseClass.commentArray = new ArrayList<NetGetCertainConfession.ResponseItem>();

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(HOST).port(PORT).addPathSegments(PATH_SEGMENTS)
                .build();
        Log.v("httpUrl",url.toString());

        NetGetCertainConfession.RequestClass requestClass = new NetGetCertainConfession.RequestClass();
        requestClass.postID = postID;
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
                NetGetCertainConfession.ResponseItem temp = gson_get.fromJson(e, NetGetCertainConfession.ResponseItem.class);
                responseClass.commentArray.add(temp);
            }
            return responseClass;
        }catch (IOException e)
        {
            return null;
        }

    }
}
