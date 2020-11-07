package com.example.loveislandapp.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.BufferedSink;

public class PersonalInfoHttp {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final String baseUrl
            = "http://192.168.1.102:30010/userinfo/";
    public static final String testUrl
            = "http://10.0.2.2:20000";

    private OkHttpClient client = new OkHttpClient();

    public class PersonalInfoResult
    {
        public String school="电子神技大学";
        public boolean sex=false;
        public String introduction="我是Ersakelly,感到很震惊吧";
    }

    public class EditResult
    {
        public boolean success;
        public boolean nickNameUsed;
        public EditResult(boolean success,boolean nickNameUsed)
        {
            this.success=success;
            this.nickNameUsed=nickNameUsed;
        }
    }

    public class SaveEditRequestContent
    {
        String nickname;
        String school;
        String introduction;
    }

    public PersonalInfoResult getPersonalInfo(String uid)
    {
        String url=baseUrl+"uid";

        Request request=new Request.Builder()
                .url(url)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();

            ResponseBody responseBody=response.body();
            if(responseBody==null)
            {
                return new PersonalInfoResult();
            }

            Gson gson=new Gson();
            String responseHttp=gson.fromJson(responseBody.string(),String.class);
            Log.v("responseJson/personalInfo",responseHttp);

            return new PersonalInfoResult();
        }
        catch (IOException e)
        {
            return new PersonalInfoResult();
        }
    }

    public EditResult setPersonalInfo(String nickname,String school,String introduction)
    {
        String url=baseUrl+"uid";

        SaveEditRequestContent saveEditRequestContent=new SaveEditRequestContent();
        saveEditRequestContent.nickname=nickname;
        saveEditRequestContent.school=school;
        saveEditRequestContent.introduction=introduction;

        Gson gson=new Gson();
        String json=gson.toJson(saveEditRequestContent);

        RequestBody requestBody=RequestBody.create(json, JSON);

        Request request=new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        try{
            Response response=client.newCall(request).execute();
            ResponseBody responseBody=response.body();
            if(responseBody==null)
            {
                return new EditResult(false,false);
            }
            String responseHttp=gson.fromJson(responseBody.string(),String.class);
            Log.v("responseJson/edit",responseHttp);

            return new EditResult(true,true);
        }catch (IOException e)
        {
            return new EditResult(false,false);
        }
    }

}
