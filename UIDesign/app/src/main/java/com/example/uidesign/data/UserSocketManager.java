package com.example.uidesign.data;

import android.os.Message;


import androidx.appcompat.widget.LinearLayoutCompat;

import com.example.uidesign.data.database.AppDatabase;
import com.example.uidesign.data.database.DatabaseManager;
import com.example.uidesign.data.database.Entity_ChatMsg;
import com.example.uidesign.data.database.Entity_Comment;
import com.example.uidesign.data.database.Entity_Contact;
import com.example.uidesign.data.database.Entity_Like;
import com.example.uidesign.ui.chat.ChatActivity;
import com.example.uidesign.ui.notifications.NotificationsFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private final LikeListener likeListener=new LikeListener();
    private final CommentListener commentListener=new CommentListener();

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
                        String fromName=obj.getString("fromName");
                        String content=obj.getString("msg");
                        long nowDate=obj.getLong("nowDate");
                        AppDatabase appDatabase=DatabaseManager.getAppDatabase();
                        if(appDatabase==null)
                        {
                            return;
                        }
                        if(bInChat&&
                                (currentChatWith==to
                                        ||currentChatWith==from
                                )
                        )
                        {
                            ChatMsg chatMsg=new ChatMsg(from,to,content, new Date(nowDate));
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
                        appDatabase.dao_chatMsg().insertAll(entity_chatMsg);
                        int otherUid=(LogginedUser.getInstance().getUid() == to ? from : to);
                        if(appDatabase.dao_contact().isContactExisted(LogginedUser.getInstance().getUid()
                                , otherUid) == 1)
                        {
                            Entity_Contact entity_contact=new Entity_Contact();
                            entity_contact.user_uid=LogginedUser.getInstance().getUid();
                            entity_contact.other_uid=otherUid;
                            entity_contact.other_nick_name=fromName;
                            entity_contact.latest_content=content;
                            entity_contact.date=nowDate;
                            appDatabase.dao_contact().setLatestContent(entity_contact);
                        }else
                        {
                            Entity_Contact entity_contact=new Entity_Contact();
                            entity_contact.user_uid=LogginedUser.getInstance().getUid();
                            entity_contact.other_uid=otherUid;
                            entity_contact.other_nick_name=fromName;
                            entity_contact.latest_content=content;
                            entity_contact.date=nowDate;
                            appDatabase.dao_contact().insertAll(entity_contact);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }).on("receiveConfLike", likeListener)
                    .on("receiveDisLike", likeListener)
                    .on("receiveConfCom",commentListener)
                    .on("receiveDisCom",commentListener);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public class LikeListener implements Emitter.Listener{

        @Override
        public void call(Object... args) {
            JSONObject obj = (JSONObject)args[0];
            try{
                int from=obj.getInt("from");
                String fromName=obj.getString("fromName");
                long nowDate=obj.getLong("nowDate");
                Entity_Like entity_like=new Entity_Like();
                entity_like.from=from;
                entity_like.fromName=fromName;
                entity_like.to=LogginedUser.getInstance().getUid();
                entity_like.date=nowDate;
                DatabaseManager.getAppDatabase().dao_like().insertAll(entity_like);
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public class CommentListener implements Emitter.Listener{

        @Override
        public void call(Object... args) {
            JSONObject obj = (JSONObject)args[0];
            try{
                int from=obj.getInt("from");
                String fromName=obj.getString("fromName");
                String com=obj.getString("com");
                long nowDate=obj.getLong("nowDate");
                Entity_Comment entity_comment=new Entity_Comment();
                entity_comment.from=from;
                entity_comment.to=LogginedUser.getInstance().getUid();
                entity_comment.fromName=fromName;
                entity_comment.content=com;
                entity_comment.date=nowDate;
                DatabaseManager.getAppDatabase().dao_comment().insertAll(entity_comment);
            }catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    }

    public Socket getSocket(){return socket;}
}
