package com.example.uidesign.ui.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.example.uidesign.ProjectSettings;
import com.example.uidesign.data.ChatMsg;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.net.SocketMsg;
import com.example.uidesign.net.UserSocketManager;
import com.example.uidesign.data.database.DatabaseManager;
import com.example.uidesign.data.database.Entity_ChatMsg;
import com.example.uidesign.databinding.ActivityChatBinding;
import com.example.uidesign.ui.personal_page.PersonalPageActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static boolean DEBUG=false;

    private ActivityChatBinding binding;
    private Context thisContext=this;
    private Activity thisActivity=this;
    private List<ChatMsg> msgList = new ArrayList<>();
    private ChatMsgAdapter adapter;
    private ChatActivityHandler chatActivityHandler=new ChatActivityHandler();

    private int otherUid;
    private String otherNickName;



    private List<Entity_ChatMsg> entity_chatMsgs;


    public class ChatActivityHandler extends Handler
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 100:
                    Log.v("recieve","reciveve");
                    ChatMsg chatMsg=(ChatMsg)msg.obj;
                    Log.v("recieve",chatMsg.getContent());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            msgList.add(chatMsg);
                            adapter.notifyItemInserted(msgList.size()-1);
                            binding.messageRecyclerView.scrollToPosition(msgList.size()-1);
                        }
                    });
                    break;
                case 200:
                    msgList=new ArrayList<ChatMsg>();
                    for(Entity_ChatMsg e:entity_chatMsgs)
                    {
                        ChatMsg chatMsg1=new ChatMsg(e.from,e.to,e.content,new Date(e.date));
                        msgList.add(chatMsg1);
                    }

                    adapter=new ChatMsgAdapter(msgList,LogginedUser.getInstance().getNickName(),
                            otherNickName,thisContext,chatActivityHandler);
                    binding.messageRecyclerView.addItemDecoration(new DividerItemDecoration(thisContext, DividerItemDecoration.VERTICAL));
                    binding.messageRecyclerView.setAdapter(adapter);
                    binding.chatTitle.setText(otherNickName);
                    break;
                case 300:
                    Intent intent=new Intent(thisContext, PersonalPageActivity.class);
                    intent.putExtra("uid",msg.arg1);
                    startActivity(intent);
                    break;
            }
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(!DEBUG)
        {
            Intent intent=getIntent();
            otherUid=intent.getIntExtra("user",0);
            otherNickName=intent.getStringExtra("nickname");

        }else
        {
            otherUid=LogginedUser.getInstance().getUid();
            otherNickName="yahaha";
        }
        UserSocketManager.getInstance().chatActivityHandler=chatActivityHandler;
        UserSocketManager.getInstance().bInChat=true;
        UserSocketManager.getInstance().currentChatWith=otherUid;

        LinearLayoutManager layoutManager = new LinearLayoutManager(thisContext);
        binding.messageRecyclerView.setLayoutManager(layoutManager);

        new Thread(new Runnable() {
            @Override
            public void run() {
                entity_chatMsgs= DatabaseManager.getAppDatabase().dao_chatMsg().getChatMsgLog(LogginedUser.getInstance().getUid(),otherUid);
                Message message=chatActivityHandler.obtainMessage();
                message.what=200;
                chatActivityHandler.sendMessage(message);
            }
        }).start();

        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SocketMsg temp=new SocketMsg();
                temp.type="sendMsg";
                temp.from=LogginedUser.getInstance().getUid();
                temp.fromName=LogginedUser.getInstance().getNickName();
                temp.to=otherUid;
                temp.nowDate=System.currentTimeMillis();
                temp.msg=binding.messageInput.getText().toString();
                binding.messageInput.setText("");
                Gson gson=new Gson();
                String sendString=gson.toJson(temp);

                ChatMsg chatMsg=new ChatMsg(temp.from,temp.to,temp.msg,new Date(temp.nowDate));
                msgList.add(chatMsg);
                adapter.notifyItemInserted(msgList.size()-1);
                binding.messageRecyclerView.scrollToPosition(msgList.size()-1);

                if(!ProjectSettings.UI_TEST)
                {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Entity_ChatMsg entity_chatMsg=new Entity_ChatMsg();
                            entity_chatMsg.from=temp.from;
                            entity_chatMsg.to=temp.to;
                            entity_chatMsg.content=temp.msg;
                            entity_chatMsg.date=temp.nowDate;
                            assert DatabaseManager.getAppDatabase() != null;
                            DatabaseManager.getAppDatabase().dao_chatMsg().insertAll(entity_chatMsg);
                        }
                    }).start();


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                UserSocketManager.getInstance().getDataOutputStream().writeUTF(sendString);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }


            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserSocketManager.getInstance().bInChat=false;
                thisActivity.finish();
            }
        });
    }
}