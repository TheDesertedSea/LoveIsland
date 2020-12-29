package com.example.uidesign.ui.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uidesign.R;
import com.example.uidesign.data.ChatMsg;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import com.example.uidesign.data.LogginedUser;

public class ChatMsgAdapter extends RecyclerView.Adapter<ChatMsgAdapter.ViewHolder>{

    private List<ChatMsg> chatMsgList;
    private String myName;
    private String otherName;
    private Context context;
    private ChatActivity.ChatActivityHandler handler;

    private static String HOST="192.168.1.105";
    private String baseIconUrl="http://"+HOST+":30010/user/userPortrait/";

    static class ViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout leftLayout;
        ConstraintLayout rightLayout;
        TextView leftContent;
        TextView rightContent;
        TextView leftName;
        TextView rightName;
        TextView leftDate;
        TextView rightDate;
        CircleImageView leftIcon;
        CircleImageView rightIcon;

        public ViewHolder(@NonNull View view){
            super(view);
            leftLayout=view.findViewById(R.id.chat_layout_left);
            rightLayout=view.findViewById(R.id.chat_layout_right);
            leftContent=view.findViewById(R.id.chat_content_left);
            rightContent=view.findViewById(R.id.chat_content_right);
            leftName=view.findViewById(R.id.nick_name_left);
            rightName=view.findViewById(R.id.nick_name_right);
            leftDate=view.findViewById(R.id.date_left);
            rightDate=view.findViewById(R.id.date_right);
            leftIcon=view.findViewById(R.id.icon_left);
            rightIcon=view.findViewById(R.id.icon_right);
        }
    }

    public ChatMsgAdapter (List<ChatMsg> msgList, String myName, String otherName, Context context, ChatActivity.ChatActivityHandler handler){
        chatMsgList = msgList;
        this.myName=myName;
        this.otherName=otherName;
        this.context=context;
        this.handler=handler;
    }

    @NonNull
    @Override
    public ChatMsgAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //ViewHolder通常出现在适配器里，为的是listview滚动的时候快速设置值，而不必每次都重新创建很多对象，从而提升性能。
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,parent,false);
        //LayoutInflat.from()从一个Context中，获得一个布局填充器，这样你就可以使用这个填充器来把xml布局文件转为View对象了。
        //LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);这样的方法来加载布局msg_item.xml
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMsgAdapter.ViewHolder holder, int position) {
        ChatMsg chatMsg=chatMsgList.get(position);
        if(chatMsg.getTo()==LogginedUser.getInstance().getUid())
        {
            holder.rightLayout.setVisibility(View.GONE);
            Glide.with(context).load(baseIconUrl+chatMsg.getFrom()).into(holder.leftIcon);
            holder.leftIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message=handler.obtainMessage();
                    message.what=300;
                    message.arg1=chatMsg.getFrom();
                    handler.sendMessage(message);
                }
            });
            holder.leftName.setText(otherName);
            holder.leftDate.setText(chatMsg.getDate().toString());
            holder.leftContent.setText(chatMsg.getContent());
        }else
        {
            holder.leftLayout.setVisibility(View.GONE);
            Glide.with(context).load(baseIconUrl+chatMsg.getFrom()).into(holder.rightIcon);
            holder.rightName.setText(myName);
            holder.rightDate.setText(chatMsg.getDate().toString());
            holder.rightContent.setText(chatMsg.getContent());
        }

    }

    @Override
    public int getItemCount() {
        return chatMsgList.size();
    }
}
