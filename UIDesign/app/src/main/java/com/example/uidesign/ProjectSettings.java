package com.example.uidesign;

import com.bumptech.glide.request.RequestOptions;

public class ProjectSettings {
    public static final boolean UI_TEST=false;
    public static final RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.lilimu)//图片加载出来前，显示的图片
            .fallback(R.drawable.lilimu) //url为空的时候,显示的图片
            .error(R.drawable.lilimu);//图片加载失败后，显示的图片
}
