package com.example.uidesign.net;

import android.util.Log;


import com.example.uidesign.data.LogginedUser;
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

public class NetLuckyCard {
    private static final boolean DEBUG=true;

    private static final String SCHEME="http";
    private static final String FORMAT_GET="host:30010/card/getcard/:uid";
    private static final String FORMAT_MATCH="host:30010/card/match/:uid/:cid";
    private static final String HOST="";
    private static final int PORT=30010;
    private static final String PATH_SEGMENTS_GET="card/getcard";
    private static final String PATH_SEGMENTS_MATCH="card/match";

    public class CardType
    {
        int cid;
        String cardName;
    }

    public class MatchResponseClass
    {
        int uid;
    }

    public ArrayList<CardType> getLuckyCard(int uid)
    {
        ArrayList<CardType> cards=new ArrayList<CardType>();
        if(DEBUG) {
            CardType card=new CardType();
            card.cid=0;
            card.cardName="炸鸡";
            cards.add(card);
            cards.add(card);
            cards.add(card);
            cards.add(card);
            cards.add(card);
            cards.add(card);
            return cards;
        }

        OkHttpClient client=new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(HOST).port(PORT).addPathSegments(
                PATH_SEGMENTS_GET+"/"+uid)
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
            JsonArray jsonElements=JsonParser.parseString(responseJson).getAsJsonArray();
            Gson gson=new Gson();
            for(JsonElement e:jsonElements)
            {
                CardType temp=gson.fromJson(e,CardType.class);
                cards.add(temp);
            }
            return cards;
        }catch (IOException e)
        {
            return null;
        }
    }

    public int matchCard(int cid,int uid)
    {
        if(DEBUG) {
            return uid;
        }

        OkHttpClient client=new OkHttpClient();

        HttpUrl url = new HttpUrl.Builder().scheme("http").host(HOST).port(PORT).addPathSegments(
                PATH_SEGMENTS_GET+"/"+uid+"/"+cid)
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
                return -1;
            }
            String responseJson=responseBody.string();
            Gson gson=new Gson();
            MatchResponseClass matchResponseClass=gson.fromJson(responseJson,MatchResponseClass.class);
            return matchResponseClass.uid;
        }catch (IOException e)
        {
            return -1;
        }
    }
}
