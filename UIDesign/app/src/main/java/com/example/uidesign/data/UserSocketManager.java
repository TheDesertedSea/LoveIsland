package com.example.uidesign.data;

import android.os.Message;


import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.uidesign.data.database.DatabaseManager;
import com.example.uidesign.data.database.Entity_ChatMsg;
import com.example.uidesign.ui.chat.ChatActivity;
import com.example.uidesign.ui.notifications.NotificationsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.util.Date;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class UserSocketManager {
    private static UserSocketManager instance;
    public static UserSocketManager getInstance()
    {
        if(instance==null)
        {
            instance=new UserSocketManager();
        }
        return instance;
    }
    private UserSocketManager(){}

    private Socket socket;
    public int currentChatWith;
    public ChatActivity.ChatActivityHandler chatActivityHandler;
    public NotificationsFragment.NotificationsFragmentHandler notificationsFragmentHandler;
    public boolean bInChat=false;
    public boolean bInNotifications=false;

    public void connect(String host,int port)
    {
        try
        {
            socket= IO.socket("http://"+host+String.valueOf(port));
            socket.on("receiveMsg", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONObject obj = (JSONObject)args[0];
                    try {
                        int from=obj.getInt("from");
                        int to=obj.getInt("to");
                        String content=obj.getString("msg");
                        long nowDate=obj.getLong("nowDate");
                        ChatMsg chatMsg=new ChatMsg(from,to,content, new Date(nowDate));
                        if(bInChat&&
                                (currentChatWith==chatMsg.getTo()
                                        ||currentChatWith==chatMsg.getFrom()
                                )
                        )
                        {
                            Message message=chatActivityHandler.obtainMessage();
                            message.what=100;
                            message.obj=chatMsg;
                            chatActivityHandler.sendMessage(message);
                        }else if(bInNotifications)
                        {
                            
                        }
                        Entity_ChatMsg entity_chatMsg=new Entity_ChatMsg();
                        entity_chatMsg.from=from;
                        entity_chatMsg.to=to;
                        entity_chatMsg.content=content;
                        entity_chatMsg.date=nowDate;
                        assert DatabaseManager.getAppDatabase() != null;
                        DatabaseManager.getAppDatabase().dao_chatMsg().insertAll(entity_chatMsg);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).on("receiveConfLike", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            }).on("receiveDisLike", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            }).on("receiveConfCom", new Emitter.Listener(){

                @Override
                public void call(Object... args) {

                }
            }).on("receiveDisCom", new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            });
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public Socket getSocket(){return socket;}
}
