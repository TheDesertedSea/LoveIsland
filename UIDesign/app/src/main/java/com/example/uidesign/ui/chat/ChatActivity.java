package com.example.uidesign.ui.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.uidesign.data.ChatMsg;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.databinding.ActivityChatBinding;
import com.example.uidesign.net.NetPersonalCenter;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static boolean DEBUG=true;

    private ActivityChatBinding binding;
    private Context thisContext=this;
    private Activity thisActivity=this;
    private List<ChatMsg> msgList = new ArrayList<>();
    private ChatMsgAdapter adapter;

    private int otherUid;

    private String HOST="";
    private String baseIconUrl="http://"+HOST+":30010/user/userPortrait/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(!DEBUG)
        {
            Intent intent=getIntent();
            otherUid=intent.getIntExtra("otherUid",0);
        }else
        {
            otherUid=LogginedUser.getInstance().getUid();
        }


        LinearLayoutManager layoutManager = new LinearLayoutManager(thisContext);
        binding.messageRecyclerView.setLayoutManager(layoutManager);

        NetPersonalCenter netPersonalCenter=new NetPersonalCenter();
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetPersonalCenter.UserInfo myUserInfo=netPersonalCenter.getUserInfo(LogginedUser.getInstance().getUid());
                NetPersonalCenter.UserInfo otherUserInfo=netPersonalCenter.getUserInfo(otherUid);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView temp1=new ImageView(thisContext);
                        ImageView temp2=new ImageView(thisContext);
                        Glide.with(thisContext).load(baseIconUrl+myUserInfo.portraitName).into(temp1);
                        Glide.with(thisContext).load(baseIconUrl+otherUserInfo.portraitName).into(temp2);
                        adapter=new ChatMsgAdapter(msgList,temp2.getDrawingCache(),temp1.getDrawingCache(),myUserInfo.nickName,
                                otherUserInfo.nickName);
                        binding.messageRecyclerView.setAdapter(adapter);
                    }
                });
            }
        }).start();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisActivity.finish();
            }
        });
    }
}