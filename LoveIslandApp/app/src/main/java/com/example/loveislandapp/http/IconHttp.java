package com.example.loveislandapp.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class IconHttp {
    private static final String baseUrl
            = "http://192.168.1.102:8080/upload";
    private OkHttpClient client = new OkHttpClient();
    public static final String testUrl
            = "http://10.0.2.2:20000";

    class RequestContent{
        byte[] image;
    }

    public boolean setIcon(File picFile) throws IOException {
        FileInputStream fis = new FileInputStream(picFile);
        byte[] image=new byte[fis.available()];
        fis.read(image);
        RequestContent requestContent=new RequestContent();
        requestContent.image=image;
        Gson gson=new Gson();

        RequestBody requestBody=RequestBody.create(picFile,MediaType.get("image/jpeg"));
        Log.v("requestBodyContent",String.valueOf(requestBody.toString()));
        MultipartBody multipartBody=new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("photo","photo.jpeg",requestBody)
                .build();

        Request request=new Request.Builder()
                .url(baseUrl)
                .post(multipartBody)
                .build();
        try{
            Response response = client.newCall(request).execute();
            ResponseBody responseBody=response.body();
            if(responseBody==null) //报文体为空
            {
                return false;
            }
            Log.v("responseJson/icon",responseBody.string());

            return true;

        }catch(IOException e)
        {
            return false;
        }
    }

//    public boolean setIcon(Uri uri, String uid, Context context) throws IOException {
//        Bitmap bitmap= MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
//
//        int bytes = bitmap.getByteCount();
//        Log.v("bytecount",String.valueOf(bitmap.getByteCount()));
//        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
//        Log.v("streamContent",byteArrayOutputStream.toString("UTF-8"));
//
//
//
//        RequestBody requestBody=RequestBody.create(json,MediaType.get("application/json; charset=utf-8"));
//
//        Log.v("requestBodyContent",String.valueOf(requestBody.contentLength()));
//        Request request=new Request.Builder()
//                .url(baseUrl)
//                .post(requestBody)
//                .build();
//        try{
//            Response response = client.newCall(request).execute();
//            ResponseBody responseBody=response.body();
//            if(responseBody==null) //报文体为空
//            {
//                return false;
//            }
//            Log.v("responseJson/icon",responseBody.string());
//
//            return true;
//
//        }catch(IOException e)
//        {
//            return false;
//        }
//    }

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
