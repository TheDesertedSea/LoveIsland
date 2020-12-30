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

import static android.content.ContentValues.TAG;

public class NetGetConfession {

    private static final String SCHEME = "http";
    private static final String FORMAT = "host:30010/forum/pull";
    private static final String PATH_SEGMENTS = "forum/pull";
    private static final String PATH_SEGMENTS_SINGLE = "forum/pull_confid";

    //返回结果
    public static final ResponseClass FAIL = null;

    //结果信息
    public static final String SUCCESS_INFO = "刷新成功";
    public static final String FAIL_INFO = "刷新失败";

    public static class RequestClass
    {
        public int confessionID;   //目前客户端已有的最大表白帖号
        public int uid; //账号的id
    }

    //返回的一个帖子的信息
    public class ResponseItem {
        public int confessionID;
        public int uid;
        public String confCont;
        public int confLikes;
        public long confTime;
    }
    //返回的所有信息
    public static class ResponseClass
    {
        public int maxID;
        public ArrayList<ResponseItem> confessionArray;
    }

    public ResponseClass getConfession(int confessionID, int uid) {

        ResponseClass responseClass = new ResponseClass();
        responseClass.confessionArray = new ArrayList<ResponseItem>();
        responseClass.maxID = 0;

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(NetSettings.HOST_1).port(NetSettings.PORT_1).addPathSegments(PATH_SEGMENTS)
                .build();
        Log.v("httpUrl",url.toString());

        NetGetConfession.RequestClass requestClass=new NetGetConfession.RequestClass();
        requestClass.confessionID = confessionID;
        requestClass.uid = uid;

        Gson gson_pull = new Gson();
        String requestJson = gson_pull.toJson(requestClass);

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
            JsonArray jsonElements = JsonParser.parseString(responseJson).getAsJsonArray();
            Gson gson_get = new Gson();
            for(JsonElement e:jsonElements)
            {
                NetGetConfession.ResponseItem temp = gson_get.fromJson(e, NetGetConfession.ResponseItem.class);
                responseClass.confessionArray.add(temp);
                if(responseClass.maxID < temp.confessionID) {
                    responseClass.maxID = temp.confessionID;
                }
                Log.v(TAG,"内容"+temp.confCont);
                Log.v(TAG,"帖子id"+temp.confessionID);
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
        public ResponseItem Obj;
    }

    public class SingleGetRequest
    {
        public int postID;
    }


    public SingleGetResponse getSingleConfession(int postID)
    {
        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(NetSettings.HOST_1).port(NetSettings.PORT_1)
                .addPathSegments(PATH_SEGMENTS_SINGLE)
                .build();
        Log.v("httpUrl",url.toString());

        SingleGetRequest singleGetRequest=new SingleGetRequest();
        singleGetRequest.postID=postID;
        Gson gson=new Gson();
        String requestJson=gson.toJson(singleGetRequest);
        Log.v("requestJson",requestJson);

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
            return gson.fromJson(responseJson,SingleGetResponse.class);

        }catch (IOException e)
        {
            SingleGetResponse singleGetResponse=new SingleGetResponse();
            singleGetResponse.success=0;
            return singleGetResponse;
        }
    }
}
