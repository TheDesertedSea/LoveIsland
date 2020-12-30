package com.example.uidesign.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uidesign.data.database.Contact;
import com.example.uidesign.R;
import com.example.uidesign.net.NetSettings;
import com.example.uidesign.ui.chat.ChatActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{
    private List<Contact> contactList;
    private Context context;
    private NotificationsFragment.NotificationsFragmentHandler handler;

    private final String baseIconUrl="http://"+ NetSettings.HOST_1 +":"+NetSettings.PORT_1+"/user/userPortrait/";
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_or_thumb_to_me_item,parent,false);
        //LayoutInflat.from()从一个Context中，获得一个布局填充器，这样你就可以使用这个填充器来把xml布局文件转为View对象了。
        //LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);这样的方法来加载布局msg_item.xml
        return new ContactAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact=contactList.get(position);
        Glide.with(context)
                .load(baseIconUrl+contact.uid)
                .into(holder.icon);
        holder.nickName.setText(contact.nickName);
        holder.date.setText(contact.date.toString());
        holder.content.setText(contact.latestMsg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message=handler.obtainMessage();
                message.what=300;
                message.arg1=contact.uid;
                message.obj=contact.nickName;
                handler.sendMessage(message);

            }
        });
    }



    @Override
    public int getItemCount() {
        return contactList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView icon;
        TextView nickName;
        TextView date;
        TextView content;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon=itemView.findViewById(R.id.writer_icon);
            nickName=itemView.findViewById(R.id.nick_name);
            date=itemView.findViewById(R.id.comment_or_like_date);
            content=itemView.findViewById(R.id.content_brief);
        }
    }

    public ContactAdapter(List<Contact> contacts, Context context, NotificationsFragment.NotificationsFragmentHandler handler)
    {
        contactList=contacts;
        this.context=context;
        this.handler=handler;
    }


}
