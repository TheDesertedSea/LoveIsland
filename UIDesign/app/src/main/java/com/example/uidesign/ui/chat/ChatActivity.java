package com.example.uidesign.ui.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.uidesign.data.ChatMsg;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.data.UserInfo;
import com.example.uidesign.data.UserSocketManager;
import com.example.uidesign.data.database.DatabaseManager;
import com.example.uidesign.data.database.Entity_ChatMsg;
import com.example.uidesign.databinding.ActivityChatBinding;
import com.example.uidesign.net.NetPersonalCenter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
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

    public class ChatActivityHandler extends Handler
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 100:
                    ChatMsg chatMsg=(ChatMsg)msg.obj;
                    msgList.add(chatMsg);
                    adapter.notifyItemInserted(msgList.size()-1);
                    binding.messageRecyclerView.scrollToPosition(msgList.size()-1);
            }
        }

    }

    public class TempChatMsg
    {
        public int from;
        public int to;
        public String content;
        public long date;
    }

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

        List<Entity_ChatMsg> entity_chatMsgs= DatabaseManager.getAppDatabase().dao_chatMsg().getChatMsgLog(LogginedUser.getInstance().getUid(),otherUid);
        msgList=new ArrayList<ChatMsg>();
        for(Entity_ChatMsg e:entity_chatMsgs)
        {
            ChatMsg chatMsg=new ChatMsg(e.from,e.to,e.content,new Date(e.date));
            msgList.add(chatMsg);
        }

        NetPersonalCenter netPersonalCenter=new NetPersonalCenter();
        new Thread(new Runnable() {
            @Override
            public void run() {
                UserInfo otherUserInfo=netPersonalCenter.getUserInfo(otherUid);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ImageView temp1=new ImageView(thisContext);
                        ImageView temp2=new ImageView(thisContext);
                        Glide.with(thisContext).load(baseIconUrl+LogginedUser.getInstance().getUid()+".jpg").into(temp1);
                        Glide.with(thisContext).load(baseIconUrl+otherUid+".jpg").into(temp2);
                        adapter=new ChatMsgAdapter(msgList,temp2.getDrawingCache(),temp1.getDrawingCache(),LogginedUser.getInstance().getNickName(),
                                otherUserInfo.nickName);
                        binding.messageRecyclerView.setAdapter(adapter);
                        binding.chatTitle.setText(otherUserInfo.nickName);
                    }
                });
            }
        }).start();

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TempChatMsg temp=new TempChatMsg();
                temp.from=LogginedUser.getInstance().getUid();
                temp.to=otherUid;
                temp.date=System.currentTimeMillis();
                UserSocketManager.getInstance().getSocket().emit("sendMsg",temp);
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