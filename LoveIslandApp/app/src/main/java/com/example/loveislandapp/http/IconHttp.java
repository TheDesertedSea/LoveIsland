package com.example.loveislandapp.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class IconHttp {
    private static final String baseUrl
            = "http://192.168.1.102:30010/usericon/";
    private OkHttpClient client = new OkHttpClient();
    public static final String testUrl
            = "http://10.0.2.2:20000";

    public boolean setIcon(String path, String uid)
    {
        Log.v("imagePath",path);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        String type = options.outMimeType;
        Log.v("imageType",type);
        File file=new File(path);
        RequestBody requestBody=RequestBody.create(file, MediaType.parse(type));
        Request request=new Request.Builder()
                .url(baseUrl+uid)
                .post(requestBody)
                .build();
        try{
            Response response = client.newCall(request).execute();
            ResponseBody responseBody=response.body();
            if(responseBody==null) //报文体为空
            {
                return false;
            }
            Gson gson=new Gson();
            String responseHttp=gson.fromJson(responseBody.string(),String.class);
            Log.v("responseJson/icon",responseHttp);

            return true;

        }catch(IOException e)
        {
            return false;
        }
    }

    private byte[] bitmap2Bytes(Bitmap bm){
        if(bm==null)
        {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

}
