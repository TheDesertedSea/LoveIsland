package com.example.uidesign.ui;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.getInstance().add(this);    //创建时将自己加入活动收集器中
    }
    @Override
    protected void onDestroy()
    {
        ActivityCollector.getInstance().remove(this);   //销毁时将自己从活动收集器中移除
        super.onDestroy();
    }
}
