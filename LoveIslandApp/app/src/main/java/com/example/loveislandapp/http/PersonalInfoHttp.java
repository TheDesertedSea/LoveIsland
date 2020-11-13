package com.example.loveislandapp.http;

import android.media.session.MediaSession;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PersonalInfoHttp {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static final String baseUrl
            = "http://192.168.1.112:30010/userInfo";
    public static final String testUrl
            = "http://10.0.2.2:20000";

    private OkHttpClient client = new OkHttpClient();

    public class PersonalInfoResult
    {
//        public String school="电子神技大学";
//        public boolean sex=false;
//        public String introduction="我是Ersakelly,感到很震惊吧";
        public byte[] image;
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
        String url=baseUrl;


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


            byte[] re=responseBody.bytes();
            Log.v("stream",re.toString());
            PersonalInfoResult personalInfoResult=new PersonalInfoResult();
            personalInfoResult.image=re;

            return personalInfoResult;
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
