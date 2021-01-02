package com.example.uidesign.ui.personal_page;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.data.UserInfo;
import com.example.uidesign.net.NetPersonalCenter;
import com.example.uidesign.net.NetSettings;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityPersonalPageBinding;
import com.example.uidesign.ui.chat.ChatActivity;
import com.example.uidesign.ui.my_confession.MyConfessionActivity;
import com.example.uidesign.ui.my_discussion.MyDiscussionActivity;

public class PersonalPageActivity extends BaseActivity {

    private ActivityPersonalPageBinding binding;
    private Activity thisActivity=this;
    private Context thisContext=this;
    private UserInfo userInfo;

    private final String baseIconUrl="http://"+ NetSettings.HOST_1 +":"+NetSettings.PORT_1+"/user/userPortrait/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPersonalPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //接收intent，额外信息uid
        Intent intent=getIntent();
        int uid=intent.getIntExtra("uid", -1);

        NetPersonalCenter netPersonalCenter=new NetPersonalCenter();
        Glide.with(thisContext)
                .load(baseIconUrl+uid)
                .diskCacheStrategy(DiskCacheStrategy.NONE).into(binding.userIcon);
        new Thread(new Runnable() {
            @Override
            public void run() {
                userInfo=netPersonalCenter.getUserInfo(uid);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.nickNamePersonalPage.setText(userInfo.nickname);
                        String sexAndSchool= "";
                        if(userInfo.sex)
                        {
                            sexAndSchool="男生"+", "+userInfo.school;

                        }else
                        {
                            sexAndSchool="女生"+", "+userInfo.school;
                        }
                        binding.sexSchoolPersonalPage.setText(sexAndSchool);
                        binding.introPersonalCenter.setText(userInfo.introduction);
                    }
                });
            }
        }).start();

        binding.buttonHisConfession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(thisContext, MyConfessionActivity.class);
                intent.putExtra("uid",uid);
                intent.putExtra("sex",userInfo.sex);
                intent.putExtra("me",false);
                startActivity(intent);
            }
        });

        binding.buttonHisDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(thisContext, MyDiscussionActivity.class);
                intent.putExtra("uid",uid);
                intent.putExtra("sex",userInfo.sex);
                intent.putExtra("me",false);
                startActivity(intent);
            }
        });

        binding.chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(thisContext, ChatActivity.class);
                intent.putExtra("user",uid);
                intent.putExtra("nickname",userInfo.nickname);
                startActivity(intent);
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisActivity.finish();
            }
        });
    }
}