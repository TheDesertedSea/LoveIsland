package com.example.uidesign.net;

import android.util.Log;

import com.example.uidesign.ProjectSettings;
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

public class NetGetCommentOfConfession {
    private static final String SCHEME = "http";
    private static final String FORMAT = "host:30010/forum/pull_comment";
    private static final String PATH_SEGMENTS = "forum/pull_comment";

    //返回结果
    public static final NetGetCommentOfConfession.ResponseClass FAIL = null;

    public static class RequestClass
    {
        public int confessionID;   //帖子的id
        public int uid; //账号的id
    }

    //返回的一个评论的信息
    public class ResponseItem {
        public int confession_commentID;
        public int confessionID;//没用
        public int uid;
        public String nickname;
        public String ccCont;
        public long ccTime;
    }
    //返回的所有信息
    public static class ResponseClass
    {
        public ArrayList<NetGetCommentOfConfession.ResponseItem> commentArray;
    }

    public NetGetCommentOfConfession.ResponseClass getComment(int postID, int uid) {
        if(ProjectSettings.UI_TEST)
        {
            ArrayList<NetGetCommentOfConfession.ResponseItem> commentArray=new ArrayList<>();
            ResponseItem responseItem=new ResponseItem();
            responseItem.confession_commentID=1;
            responseItem.confessionID=1;
            responseItem.uid=1;
            responseItem.nickname="MObistan";
            responseItem.ccCont="LOVEEOVL OHOHOHOHOHOh";
            responseItem.ccTime=System.currentTimeMillis();
            commentArray.add(responseItem);
            commentArray.add(responseItem);
            ResponseClass responseClass=new ResponseClass();
            responseClass.commentArray=commentArray;
            return responseClass;
        }

        NetGetCommentOfConfession.ResponseClass responseClass = new NetGetCommentOfConfession.ResponseClass();
        responseClass.commentArray = new ArrayList<NetGetCommentOfConfession.ResponseItem>();

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(NetSettings.HOST_1).port(NetSettings.PORT_1).addPathSegments(PATH_SEGMENTS)
                .build();
        Log.v("httpUrl",url.toString());

        NetGetCommentOfConfession.RequestClass requestClass = new NetGetCommentOfConfession.RequestClass();
        requestClass.confessionID = postID;
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
            Log.v("TAG", "json"+responseJson);
            JsonArray jsonElements = JsonParser.parseString(responseJson).getAsJsonArray();
            Gson gson_get = new Gson();
            for(JsonElement e:jsonElements)
            {
                NetGetCommentOfConfession.ResponseItem temp = gson_get.fromJson(e, NetGetCommentOfConfession.ResponseItem.class);
                responseClass.commentArray.add(temp);
            }
            return responseClass;
        }catch (IOException e)
        {
            return null;
        }

    }
}
