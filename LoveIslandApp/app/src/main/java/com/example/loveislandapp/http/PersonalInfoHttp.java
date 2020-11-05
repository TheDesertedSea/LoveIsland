package com.example.loveislandapp.http;

import android.graphics.Bitmap;

import java.io.IOException;
import java.util.List;

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
            = "http://10.0.2.2:20000/personal-info/";

    private OkHttpClient client = new OkHttpClient();

//    public Bitmap getIcon(String username)
//    {
//        String relativePath=username+"/icon";
//        String url=baseUrl+relativePath;
//
//        Request request=new Request.Builder()
//                .url(url)
//                .get()
//                .build();
//
//        try {
//            Response response = client.newCall(request).execute();
//            ResponseBody responseBody = response.body();
//            if(responseBody==null)
//            {
//
//            }
//
//        }catch(IOException e)
//        {
//            return new
//        }
//    }
//
//    public List<String> getTextInfo(String username)
//    {
//
//    }


}
