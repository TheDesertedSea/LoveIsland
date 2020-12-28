package com.example.uidesign.ui.entry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.uidesign.BaseApplication;
import com.example.uidesign.ui.coldboot.ColdBootActivity;
import com.example.uidesign.data.CachedLoginData;
import com.example.uidesign.net.NetLogin;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityEntryBinding;
import com.example.uidesign.ui.MainActivity;
import com.example.uidesign.ui.login.LoginActivity;
import com.romainpiel.shimmer.Shimmer;

public class EntryActivity extends BaseActivity {

    private ActivityEntryBinding binding;
    private Context thisContext=this;
    private Activity thisActivity=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Shimmer shimmer = new Shimmer();
        shimmer.start(binding.TitleEntryActivity);

        CachedLoginData cachedLoginData=new CachedLoginData(thisContext);
        String username=cachedLoginData.readCachedLoginData().get("username");
        String password=cachedLoginData.readCachedLoginData().get("password");

        if(username.equals(""))
        {
            Intent intent=new Intent(thisContext, LoginActivity.class);
            startActivity(intent);
            this.finish();
            return;
        }

        NetLogin netLogin=new NetLogin();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int result=netLogin.login(username,password,(BaseApplication) getApplication());
                Intent intent;
                if(result==NetLogin.OK||result==NetLogin.DUPLICATE_LOGIN)
                {
                    intent=new Intent(thisContext, MainActivity.class);

                }else if(result==NetLogin.GO_TO_COLD_BOOT)
                {
                    intent = new Intent(thisContext, ColdBootActivity.class);
                }else
                {
                    cachedLoginData.saveCachedLoginData("",
                            "");
                    intent = new Intent(thisContext, LoginActivity.class);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                        thisActivity.finish();
                    }
                });

            }
        }).start();


    }
}