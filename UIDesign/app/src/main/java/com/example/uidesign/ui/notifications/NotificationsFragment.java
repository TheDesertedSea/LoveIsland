package com.example.uidesign.ui.notifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;


import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.uidesign.R;
import com.example.uidesign.data.ChatMsg;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.data.database.Contact;
import com.example.uidesign.data.database.DatabaseManager;
import com.example.uidesign.data.database.Entity_Contact;
import com.example.uidesign.net.UserSocketManager;
import com.example.uidesign.ui.chat.ChatActivity;
import com.example.uidesign.ui.comment_to_me.CommentToMeActivity;
import com.example.uidesign.ui.thumb_to_me.ThumbToMeActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationsFragment extends Fragment {

    public class NotificationsFragmentHandler extends Handler
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case 100:
                    Contact contact=(Contact) msg.obj;
                    for(int i=0;i<contactList.size();++i)
                    {
                        if(contactList.get(i).uid==contact.uid)
                        {
                            contactList.set(i,contact);
                            contactAdapter.notifyItemChanged(i);
                            recyclerView.scrollToPosition(i);
                            return;
                        }
                    }
                    contactList.add(contact);
                    contactAdapter.notifyItemInserted(contactList.size()-1);
                    recyclerView.scrollToPosition(contactList.size()-1);
                    break;
                case 200:
                    contactList=new ArrayList<Contact>();
                    for(Entity_Contact e:entity_contacts)
                    {
                        Contact contact2=new Contact();
                        contact2.uid=e.other_uid;
                        contact2.nickName=e.other_nick_name;
                        contact2.latestMsg=e.latest_content;
                        contact2.date=new Date(e.date);
                        contactList.add(contact2);
                    }
                    contactAdapter=new ContactAdapter(contactList,thisContext,notificationsFragmentHandler);
                    recyclerView.addItemDecoration(new DividerItemDecoration(thisContext, DividerItemDecoration.VERTICAL));
                    recyclerView.setAdapter(contactAdapter);
                    UserSocketManager.getInstance().bInNotifications=true;
                    break;
                case 300:
                    Intent intent=new Intent(thisContext, ChatActivity.class);
                    intent.putExtra("user",msg.arg1);
                    intent.putExtra("nickname",(String)msg.obj);
                    startActivity(intent);
                    break;
            }
        }

    }

    private ImageButton commentToMeButton;
    private ImageButton likeToMeButton;
    private RecyclerView recyclerView;
    private List<Contact> contactList;
    private ContactAdapter contactAdapter;
    private List<Entity_Contact> entity_contacts;
    private NotificationsFragmentHandler notificationsFragmentHandler=new NotificationsFragmentHandler();

    private Context thisContext;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        thisContext=getContext();
        commentToMeButton=root.findViewById(R.id.button_c_to_m);
        likeToMeButton=root.findViewById(R.id.button_my_thumb);
        recyclerView=root.findViewById(R.id.contact_recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(thisContext);
        recyclerView.setLayoutManager(layoutManager);
        UserSocketManager.getInstance().notificationsFragmentHandler=notificationsFragmentHandler;

        new Thread(new Runnable() {
            @Override
            public void run() {
                entity_contacts=DatabaseManager.getAppDatabase().dao_contact().getContacts(LogginedUser.getInstance().getUid());
                Message message=notificationsFragmentHandler.obtainMessage();
                message.what=200;
                notificationsFragmentHandler.sendMessage(message);
            }
        }).start();


        commentToMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(thisContext, CommentToMeActivity.class);
                startActivity(intent);
            }
        });

        likeToMeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(thisContext, ThumbToMeActivity.class);
                startActivity(intent);
            }
        });

        return root;

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        UserSocketManager.getInstance().bInNotifications= !hidden;
    }
}