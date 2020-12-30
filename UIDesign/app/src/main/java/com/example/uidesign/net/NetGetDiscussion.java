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

public class NetGetDiscussion {
    private static final String SCHEME = "http";
    private static final String FORMAT = "host:30010/discuss/pull";
    private static final String PATH_SEGMENTS = "discuss/pull";
    private static final String PATH_SEGMENTS_SINGLE = "discuss/pull_discussid";

    //返回结果
    public static final NetGetDiscussion.ResponseClass FAIL = null;

    //结果信息
    public static final String SUCCESS_INFO = "刷新成功";
    public static final String FAIL_INFO = "刷新失败";

    public static class RequestClass
    {
        public int discussID;   //目前客户端已有的最大表白帖号
        public int uid; //账号的id
    }

    //返回的一个帖子的信息
    public class ResponseItem {
        public int discussID;
        public int uid;
        public String disCont;
        public int disLikes;
        public long disTime;
    }
    //返回的所有信息
    public static class ResponseClass
    {
        public int maxID;
        public ArrayList<NetGetDiscussion.ResponseItem> discussionArray;
    }

    public NetGetDiscussion.ResponseClass getDiscussion(int discussID, int uid) {

        NetGetDiscussion.ResponseClass responseClass = new NetGetDiscussion.ResponseClass();
        responseClass.discussionArray = new ArrayList<NetGetDiscussion.ResponseItem>();
        responseClass.maxID = 0;

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(NetSettings.HOST_1).port(NetSettings.PORT_1).addPathSegments(PATH_SEGMENTS)
                .build();
        Log.v("httpUrl",url.toString());

        NetGetDiscussion.RequestClass requestClass=new NetGetDiscussion.RequestClass();
        requestClass.discussID = discussID;
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
                NetGetDiscussion.ResponseItem temp = gson_get.fromJson(e, NetGetDiscussion.ResponseItem.class);
                responseClass.discussionArray.add(temp);
                if(responseClass.maxID < temp.discussID) {
                    responseClass.maxID = temp.discussID;
                }
            }
            return responseClass;
        }catch (IOException e)
        {
            return null;
        }

    }

    public class SingleGetResponse
    {
        public int success;
        public NetGetDiscussion.ResponseItem Obj;
    }

    public class SingleGetRequest
    {
        public int postID;
    }


    public NetGetDiscussion.SingleGetResponse getSingleDiscussion(int postID)
    {
        OkHttpClient client = new OkHttpClient();

        Log.v("postID",String.valueOf(postID));
        HttpUrl url = new HttpUrl.Builder().scheme("http").host(NetSettings.HOST_1).port(NetSettings.PORT_1)
                .addPathSegments(PATH_SEGMENTS_SINGLE)
                .build();
        Log.v("httpUrl",url.toString());

        NetGetDiscussion.SingleGetRequest singleGetRequest=new NetGetDiscussion.SingleGetRequest();
        singleGetRequest.postID=postID;

        Gson gson=new Gson();
        String requestJson=gson.toJson(singleGetRequest);

        RequestBody requestBody=RequestBody.create(requestJson,MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try {
            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                SingleGetResponse singleGetResponse=new SingleGetResponse();
                singleGetResponse.success=0;
                return singleGetResponse;
            }
            String responseJson=responseBody.string();
            return gson.fromJson(responseJson, NetGetDiscussion.SingleGetResponse.class);

        }catch (IOException e)
        {
            SingleGetResponse singleGetResponse=new SingleGetResponse();
            singleGetResponse.success=0;
            return singleGetResponse;
        }
    }
}
