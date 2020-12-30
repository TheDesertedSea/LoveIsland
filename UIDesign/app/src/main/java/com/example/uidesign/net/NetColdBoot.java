package com.example.uidesign.net;

import android.util.Log;

import com.example.uidesign.data.CardType;
import com.example.uidesign.data.ColdBootItem;
import com.example.uidesign.data.LogginedUser;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class NetColdBoot {
    private static final boolean DEBUG=false;

    private static final String GET_FORMAT="HOST:30010/card/domain";
    private static final String GET_PATH_SEGMENTS="card/domain";

    private static final String SEND_FORMAT="HOST:30010/card/domainSelect/:uid";
    private static final String SEND_PATH_SEGMENTS="card/domainSelect";




    public List<ColdBootItem> getColdBootItem(int uid)
    {
        List<ColdBootItem> result=new ArrayList<ColdBootItem>();
        if(DEBUG)
        {
            ColdBootItem coldBootItem=new ColdBootItem();
            coldBootItem.domainID=0;
            coldBootItem.domainName="米饭";

            for(int i=0;i<10;++i)
            {
                result.add(coldBootItem);
            }
            return result;
        }

        OkHttpClient client=new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(NetSettings.HOST_1).port(NetSettings.PORT_1).addPathSegments(GET_PATH_SEGMENTS)
                .build();
        Log.v("httpUrl",url.toString());


        Request request = new Request.Builder()
                .url(url)
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
            Log.v("json",responseJson);
            JsonArray jsonElements= JsonParser.parseString(responseJson).getAsJsonArray();
            Log.v("json",String.valueOf(jsonElements.size()));
            Gson gson=new Gson();
            for(JsonElement e:jsonElements)
            {
                Log.v("json",e.toString());
                ColdBootItem temp=gson.fromJson(e,ColdBootItem.class);
                result.add(temp);
            }
            return result;
        }catch (IOException e)
        {
            return null;
        }

    }


    public class ReturnClass
    {
        public int success;
    }

    public boolean sendColdBootItem(int uid,List<Integer> list)
    {
        if(DEBUG)
        {
            return true;
        }

        OkHttpClient client=new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(NetSettings.HOST_1).port(NetSettings.PORT_1)
                .addPathSegments(SEND_PATH_SEGMENTS+"/"+ LogginedUser.getInstance().getUid())
                .build();
        Log.v("httpUrl",url.toString());

        JsonArray jsonElements=new JsonArray();
        for(Integer e:list)
        {

            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("domainID",e);
            jsonElements.add(jsonObject);
        }

        String json=jsonElements.toString();

        RequestBody requestBody=RequestBody.create(json, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();

        try
        {
            Response response=client.newCall(request).execute();
            ResponseBody responseBody=response.body();
            if(responseBody==null)
            {
                return false;
            }
            String responseJson=responseBody.string();
            Gson gson=new Gson();

            ReturnClass returnClass=gson.fromJson(responseJson,ReturnClass.class);
            return returnClass.success != 0;
        }catch (IOException e)
        {
            return false;
        }
    }

}
