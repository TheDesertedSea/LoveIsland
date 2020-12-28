package com.example.uidesign.net;

import android.os.Message;
import android.util.Log;


import com.example.uidesign.BaseApplication;
import com.example.uidesign.data.ChatMsg;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.data.database.AppDatabase;
import com.example.uidesign.data.database.Contact;
import com.example.uidesign.data.database.DatabaseManager;
import com.example.uidesign.data.database.Entity_ChatMsg;
import com.example.uidesign.data.database.Entity_Comment;
import com.example.uidesign.data.database.Entity_Contact;
import com.example.uidesign.data.database.Entity_Like;
import com.example.uidesign.ui.chat.ChatActivity;
import com.example.uidesign.ui.notifications.NotificationsFragment;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;
import java.util.List;


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
    private DataOutputStream dataOutputStream;
    private DataInputStream dataInputStream;
    public int currentChatWith;
    public ChatActivity.ChatActivityHandler chatActivityHandler;
    public NotificationsFragment.NotificationsFragmentHandler notificationsFragmentHandler;
    public boolean bInChat=false;
    public boolean bInNotifications=false;
    private boolean bIsRunning=false;


    public void connect(String host, int port)
    {
        Log.v("socket-connect",host+":"+port);
        try {
            socket = new Socket(host, port);
            InputStream inputStream =socket.getInputStream();
            dataInputStream=new DataInputStream(inputStream);
            OutputStream outputStream=socket.getOutputStream();
            dataOutputStream=new DataOutputStream(outputStream);

            //发送上线消息
            SocketMsg socketMsg=new SocketMsg();
            socketMsg.type="online";
            socketMsg.from=LogginedUser.getInstance().getUid();
            Gson gson=new Gson();
            String json=gson.toJson(socketMsg);
            dataOutputStream.writeUTF(json);

            bIsRunning=true;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    UserSocketManager userSocketManager=UserSocketManager.getInstance();
                    String receive="";
                    while(userSocketManager.bIsRunning)
                    {
                        try {
                            receive=userSocketManager.dataInputStream.readUTF();
                            Gson gson1=new Gson();
                            SocketMsg socketMsg1=gson1.fromJson(receive,SocketMsg.class);
                            Log.v("socketMsg",receive);
                            Log.v("chat",String.valueOf(currentChatWith));
                            switch (socketMsg1.type)
                            {
                                case "receiveMsg":
                                    int from=socketMsg1.from;
                                    int to=socketMsg1.to;
                                    String fromName=socketMsg1.fromName;
                                    String content=socketMsg1.msg;
                                    long nowDate=socketMsg1.nowDate;
                                    AppDatabase appDatabase=DatabaseManager.getAppDatabase();
                                    if(appDatabase==null)
                                    {
                                        return;
                                    }
                                    if(bInChat&& (currentChatWith==from))
                                    {
                                        Log.v("here","here");
                                        ChatMsg chatMsg=new ChatMsg(from,to,content, new Date(nowDate));
                                        Message message=chatActivityHandler.obtainMessage();
                                        message.what=100;
                                        message.obj=chatMsg;
                                        chatActivityHandler.sendMessage(message);
                                    }

                                    Entity_ChatMsg entity_chatMsg=new Entity_ChatMsg();
                                    entity_chatMsg.from=from;
                                    entity_chatMsg.to=to;
                                    entity_chatMsg.content=content;
                                    entity_chatMsg.date=nowDate;
                                    appDatabase.dao_chatMsg().insertAll(entity_chatMsg);
                                    int otherUid=(LogginedUser.getInstance().getUid() == to ? from : to);
                                    if(bInNotifications)
                                    {
                                        Contact contact=new Contact();
                                        contact.uid=otherUid;
                                        contact.latestMsg=content;
                                        contact.nickName=fromName;
                                        contact.date=new Date(nowDate);
                                        Message message=notificationsFragmentHandler.obtainMessage();
                                        message.what=100;
                                        message.obj=contact;
                                    }
                                    List<Entity_Contact> temp=appDatabase.dao_contact().
                                            isContactExisted(LogginedUser.getInstance().getUid()
                                                    , otherUid);
                                    Log.v("here","here");
                                    if(temp.size() == 1)
                                    {
                                        Entity_Contact entity_contact=new Entity_Contact();
                                        entity_contact.id=temp.get(0).id;
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
                                    break;
                                case "receiveConfLike":
                                case "receiveDisLike":
                                    int from1=socketMsg1.from;
                                    String fromName1=socketMsg1.fromName;
                                    long nowDate1=socketMsg1.nowDate;
                                    Entity_Like entity_like=new Entity_Like();
                                    entity_like.from=from1;
                                    entity_like.fromName=fromName1;
                                    entity_like.to=LogginedUser.getInstance().getUid();
                                    entity_like.date=nowDate1;
                                    DatabaseManager.getAppDatabase().dao_like().insertAll(entity_like);
                                    break;
                                case "receiveConfCom":
                                case "receiveDisCom":
                                    int from2=socketMsg1.from;
                                    String fromName2=socketMsg1.fromName;
                                    String com=socketMsg1.msg;
                                    long nowDate2=socketMsg1.nowDate;
                                    Entity_Comment entity_comment=new Entity_Comment();
                                    entity_comment.from=from2;
                                    entity_comment.to=LogginedUser.getInstance().getUid();
                                    entity_comment.fromName=fromName2;
                                    entity_comment.content=com;
                                    entity_comment.date=nowDate2;
                                    DatabaseManager.getAppDatabase().dao_comment().insertAll(entity_comment);
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
//            Log.v("socket-connect",String.valueOf(socket.connected()));
//            socket.on("receiveMsg", new Emitter.Listener() {
//                @Override
//                public void call(Object... args) {
//                    JSONObject obj = (JSONObject)args[0];
//                    try {
//                        int from=obj.getInt("from");
//                        int to=obj.getInt("to");
//                        String fromName=obj.getString("fromName");
//                        String content=obj.getString("msg");
//                        long nowDate=obj.getLong("nowDate");
//                        AppDatabase appDatabase=DatabaseManager.getAppDatabase();
//                        if(appDatabase==null)
//                        {
//                            return;
//                        }
//                        if(bInChat&&
//                                (currentChatWith==to
//                                        ||currentChatWith==from
//                                )
//                        )
//                        {
//                            ChatMsg chatMsg=new ChatMsg(from,to,content, new Date(nowDate));
//                            Message message=chatActivityHandler.obtainMessage();
//                            message.what=100;
//                            message.obj=chatMsg;
//                            chatActivityHandler.sendMessage(message);
//                        }
//
//                        Entity_ChatMsg entity_chatMsg=new Entity_ChatMsg();
//                        entity_chatMsg.from=from;
//                        entity_chatMsg.to=to;
//                        entity_chatMsg.content=content;
//                        entity_chatMsg.date=nowDate;
//                        assert DatabaseManager.getAppDatabase() != null;
//                        appDatabase.dao_chatMsg().insertAll(entity_chatMsg);
//                        int otherUid=(LogginedUser.getInstance().getUid() == to ? from : to);
//                        if(bInNotifications)
//                        {
//                            Contact contact=new Contact();
//                            contact.uid=otherUid;
//                            contact.latestMsg=content;
//                            contact.nickName=fromName;
//                            contact.date=new Date(nowDate);
//                            Message message=chatActivityHandler.obtainMessage();
//                            message.what=100;
//                            message.obj=contact;
//                        }
//                        if(appDatabase.dao_contact().isContactExisted(LogginedUser.getInstance().getUid()
//                                , otherUid) == 1)
//                        {
//
//                            Entity_Contact entity_contact=new Entity_Contact();
//                            entity_contact.user_uid=LogginedUser.getInstance().getUid();
//                            entity_contact.other_uid=otherUid;
//                            entity_contact.other_nick_name=fromName;
//                            entity_contact.latest_content=content;
//                            entity_contact.date=nowDate;
//                            appDatabase.dao_contact().setLatestContent(entity_contact);
//
//                        }else
//                        {
//                            Entity_Contact entity_contact=new Entity_Contact();
//                            entity_contact.user_uid=LogginedUser.getInstance().getUid();
//                            entity_contact.other_uid=otherUid;
//                            entity_contact.other_nick_name=fromName;
//                            entity_contact.latest_content=content;
//                            entity_contact.date=nowDate;
//                            appDatabase.dao_contact().insertAll(entity_contact);
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }).on("receiveConfLike", likeListener)
//                    .on("receiveDisLike", likeListener)
//                    .on("receiveConfCom",commentListener)
//                    .on("receiveDisCom",commentListener);
//
//            socket.connect();
//        }catch(Exception e)
//        {
//            e.printStackTrace();
//        }
    }

//    public class LikeListener implements Emitter.Listener{
//
//        @Override
//        public void call(Object... args) {
//            JSONObject obj = (JSONObject)args[0];
//            try{
//                int from=obj.getInt("from");
//                String fromName=obj.getString("fromName");
//                long nowDate=obj.getLong("nowDate");
//                Entity_Like entity_like=new Entity_Like();
//                entity_like.from=from;
//                entity_like.fromName=fromName;
//                entity_like.to=LogginedUser.getInstance().getUid();
//                entity_like.date=nowDate;
//                DatabaseManager.getAppDatabase().dao_like().insertAll(entity_like);
//            }catch (JSONException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }

//    public class CommentListener implements Emitter.Listener{
//
//        @Override
//        public void call(Object... args) {
//            JSONObject obj = (JSONObject)args[0];
//            try{
//                int from=obj.getInt("from");
//                String fromName=obj.getString("fromName");
//                String com=obj.getString("com");
//                long nowDate=obj.getLong("nowDate");
//                Entity_Comment entity_comment=new Entity_Comment();
//                entity_comment.from=from;
//                entity_comment.to=LogginedUser.getInstance().getUid();
//                entity_comment.fromName=fromName;
//                entity_comment.content=com;
//                entity_comment.date=nowDate;
//                DatabaseManager.getAppDatabase().dao_comment().insertAll(entity_comment);
//            }catch (JSONException e)
//            {
//                e.printStackTrace();
//            }
//        }
//    }

    public Socket getSocket(){return socket;}

    public void disconnect()
    {
        bIsRunning=false;
        if(socket==null)
        {
            return;
        }

        //发送下线消息
        SocketMsg socketMsg=new SocketMsg();
        socketMsg.type="offline";
        socketMsg.from=LogginedUser.getInstance().getUid();
        Gson gson=new Gson();
        String json=gson.toJson(socketMsg);
        try
        {
            dataOutputStream.writeUTF(json);
            socket.close();
        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public DataOutputStream getDataOutputStream(){return dataOutputStream;}
}
