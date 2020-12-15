package com.example.uidesign.net;

import android.util.Log;

import com.example.uidesign.data.LogginedUser;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.GregorianCalendar;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetPersonalCenter {
    private static final boolean DEBUG=true;

    private static final String SCHEME="http";
    private static final String FORMAT_ICON_EDIT="host:30010/user/setPortrait/:uid";
    private static final String FORMAT_INFO_GET="host:30010/user/userInfo/:uid";
    private static final String FORMAT_INFO_EDIT="host:30010/user/setInfo";
    private static final String HOST="";
    private static final int PORT=30010;
    private static final String PATH_SEGMENTS_ICON_EDIT="user/setPortrait";
    private static final String PATH_SEGMENTS_INFO_GET="user/userInfo";
    private static final String PATH_SEGMENTS_INFO_EDIT="user/setInfo";

    public static final int PERSONAL_INFO_EDIT_SUCCESS=0;
    public static final int DUPLICATE_NICK_NAME=1;
    public static final int PERSONAL_INFO_EDIT_OTHER_FAIL=2;

    public static final String MESSAGE_PERSONAL_INFO_EDIT_SUCCESS="个人信息修改成功";
    public static final String MESSAGE_DUPLICATE_NICK_NAME="该昵称已被使用，修改失败";
    public static final String MESSAGE_PERSONAL_INFO_EDIT_OTHER_FAIL="个人信息修改失败";

    public class UserInfo
    {
        public String nickName;
        public boolean sex;
        public String school;
        public String introduction;
    }

    public class UserInfoResponse
    {
        public int isUser;
        public Object content;
    }

    public class UserInfoEditRequest
    {
        public int uid;
        public String nickName;
        public String school;
        public String introduction;

    }

    public class UserInfoEditResponse
    {
        public int result_code;
    }

    public class IconEditResponse
    {
        public int success;
    }

    public UserInfo getUserInfo(int uid)
    {
        if(DEBUG)
        {
            UserInfo userInfo=new UserInfo();
            userInfo.nickName="荒海";
            userInfo.sex=true;
            userInfo.school="电子科技大学";
            userInfo.introduction="荒海，遥远天边的旅人。周游四海，只为寻求那传说中的冰山宝石？冰山何在，绿林存乎？";
            return userInfo;
        }

        OkHttpClient client=new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(HOST).port(PORT).addPathSegments(
                PATH_SEGMENTS_INFO_GET+"/"+uid)
                .build();
        Log.v("httpUrl",url.toString());

        Request request = new Request.Builder()
                .url(url)
                .addHeader("token", LogginedUser.getInstance().getToken())
                .build();
        try
        {
            Response response=client.newCall(request).execute();
            ResponseBody responseBody=response.body();
            if(responseBody==null)
            {
                return null;
            }

            String responseJson=responseBody.string();
            Gson gson=new Gson();
            UserInfoResponse userInfoResponse=gson.fromJson(responseJson,UserInfoResponse.class);
            if(userInfoResponse.isUser==1)
            {
                return (UserInfo)userInfoResponse.content;
            }else
            {
                return null;
            }

        }catch(IOException e)
        {
            return null;
        }
    }

    public int setUserInfo(int uid,String nickName,String school,String introduction)
    {
        if(DEBUG)
        {
            return PERSONAL_INFO_EDIT_SUCCESS;
        }

        OkHttpClient client=new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(HOST).port(PORT).addPathSegments(
                PATH_SEGMENTS_INFO_EDIT)
                .build();
        Log.v("httpUrl",url.toString());

        Gson gson=new Gson();

        UserInfoEditRequest userInfoEditRequest=new UserInfoEditRequest();
        userInfoEditRequest.uid=uid;
        userInfoEditRequest.nickName=nickName;
        userInfoEditRequest.school=school;
        userInfoEditRequest.introduction=introduction;

        String requestJson=gson.toJson(userInfoEditRequest);

        RequestBody requestBody=RequestBody.create(requestJson, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(url)
                .addHeader("token",LogginedUser.getInstance().getToken())
                .post(requestBody)
                .build();

        try
        {
            Response response=client.newCall(request).execute();
            ResponseBody responseBody=response.body();
            if(requestBody==null)
            {
                return PERSONAL_INFO_EDIT_OTHER_FAIL;
            }
            String responseJson=responseBody.string();
            UserInfoEditResponse userInfoEditResponse=gson.fromJson(requestJson,UserInfoEditResponse.class);
            if(userInfoEditResponse.result_code==1)
            {
                return PERSONAL_INFO_EDIT_SUCCESS;
            }else
            {
                return PERSONAL_INFO_EDIT_OTHER_FAIL;
            }
        }catch(IOException e)
        {
            return PERSONAL_INFO_EDIT_OTHER_FAIL;
        }
    }

    public boolean setIcon(File picFile,int uid)
    {
        if(DEBUG)
        {
            return true;
        }

        RequestBody requestBody=RequestBody.create(picFile,MediaType.get("image/jpg"));

        MultipartBody multipartBody=new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("photo",uid+".jpg",requestBody)
                .build();

        OkHttpClient client=new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(HOST).port(PORT).addPathSegments(
                PATH_SEGMENTS_ICON_EDIT+"/"+uid)
                .build();
        Log.v("httpUrl",url.toString());

        Request request = new Request.Builder()
                .url(url)
                .addHeader("token",LogginedUser.getInstance().getToken())
                .post(multipartBody)
                .build();
        try
        {
            Response response=client.newCall(request).execute();
            ResponseBody responseBody=response.body();
            if(requestBody==null)
            {
                return false;
            }
            String responseJson=responseBody.string();
            Gson gson=new Gson();
            IconEditResponse iconEditResponse=gson.fromJson(responseJson,IconEditResponse.class);
            if(iconEditResponse.success==1)
            {
                return true;
            }else
            {
                return false;
            }

        }catch(IOException e)
        {
            return false;
        }
    }
}
