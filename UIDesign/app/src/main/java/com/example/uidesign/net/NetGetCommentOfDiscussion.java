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

public class NetGetCommentOfDiscussion {
    private static final String SCHEME = "http";
    private static final String FORMAT = "host:30010/discuss/pull_comment";
    private static final String PATH_SEGMENTS = "discuss/pull_comment";

    //返回结果
    public static final NetGetCommentOfDiscussion.ResponseClass FAIL = null;

    public static class RequestClass
    {
        public int discussID;   //帖子的id
        public int uid; //账号的id
    }

    //返回的一个评论的信息
    public class ResponseItem {
        public int discuss_commentID;
        public int discussID;//没用
        public int uid;
        public String nickname;
        public String dcCont;
        public long dcTime;
    }
    //返回的所有信息
    public static class ResponseClass
    {
        public ArrayList<NetGetCommentOfDiscussion.ResponseItem> commentArray;
    }

    public NetGetCommentOfDiscussion.ResponseClass getComment(int postID, int uid) {

        if(ProjectSettings.UI_TEST)
        {
            ArrayList<NetGetCommentOfDiscussion.ResponseItem> commentArray=new ArrayList<>();
            NetGetCommentOfDiscussion.ResponseItem responseItem=new NetGetCommentOfDiscussion.ResponseItem();
            responseItem.discuss_commentID=1;
            responseItem.discussID=1;
            responseItem.uid=1;
            responseItem.nickname="MObistan";
            responseItem.dcCont="LOVEEOVL OHOHOHOHOHOh";
            responseItem.dcTime=System.currentTimeMillis();
            commentArray.add(responseItem);
            NetGetCommentOfDiscussion.ResponseClass responseClass=new NetGetCommentOfDiscussion.ResponseClass();
            responseClass.commentArray=commentArray;
            return responseClass;
        }

        NetGetCommentOfDiscussion.ResponseClass responseClass = new NetGetCommentOfDiscussion.ResponseClass();
        responseClass.commentArray = new ArrayList<ResponseItem>();

        OkHttpClient client = new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(NetSettings.HOST_1).port(NetSettings.PORT_1).addPathSegments(PATH_SEGMENTS)
                .build();
        Log.v("httpUrl",url.toString());

        NetGetCommentOfDiscussion.RequestClass requestClass = new NetGetCommentOfDiscussion.RequestClass();
        requestClass.discussID = postID;
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
            Log.v("bug",responseJson);
            JsonArray jsonElements = JsonParser.parseString(responseJson).getAsJsonArray();
            Gson gson_get = new Gson();
            for(JsonElement e:jsonElements)
            {
                NetGetCommentOfDiscussion.ResponseItem temp = gson_get.fromJson(e, NetGetCommentOfDiscussion.ResponseItem.class);
                responseClass.commentArray.add(temp);
            }
            return responseClass;
        }catch (IOException e)
        {
            return null;
        }

    }
}
