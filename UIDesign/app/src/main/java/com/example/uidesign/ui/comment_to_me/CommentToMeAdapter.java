package com.example.uidesign.ui.comment_to_me;

import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.uidesign.ProjectSettings;
import com.example.uidesign.R;
import com.example.uidesign.data.Comment;
import com.example.uidesign.net.NetSettings;
import com.example.uidesign.tool.CommentAndLikeAdapterSend;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentToMeAdapter extends RecyclerView.Adapter<CommentToMeAdapter.ViewHolder>{
    private List<Comment> commentList;
    private Context context;
    private CommentToMeActivity.CommentToMeActivityHandler handler;

    private final String baseIconUrl="http://"+ NetSettings.HOST_1 +":"+NetSettings.PORT_1+"/user/userPortrait/";

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_or_thumb_to_me_item,parent,false);
        //LayoutInflat.from()从一个Context中，获得一个布局填充器，这样你就可以使用这个填充器来把xml布局文件转为View对象了。
        //LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);这样的方法来加载布局msg_item.xml
        return new CommentToMeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment=commentList.get(position);
        if(!ProjectSettings.UI_TEST) {
            Glide.with(context)
                    .load(baseIconUrl + comment.from)
                    .diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.icon);
        }
        holder.nickName.setText(comment.fromName);
        holder.date.setText(comment.nowDate.toString());
        holder.content.setText("评论了你： "+comment.com);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message=handler.obtainMessage();
                message.what=200;
                message.arg1=comment.postID;
                CommentAndLikeAdapterSend commentAndLikeAdapterSend=new CommentAndLikeAdapterSend();
                commentAndLikeAdapterSend.nickname=comment.fromName;
                commentAndLikeAdapterSend.type=comment.type;
                message.obj=commentAndLikeAdapterSend;
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
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

    public CommentToMeAdapter(List<Comment> comments, Context context, CommentToMeActivity.CommentToMeActivityHandler handler)
    {
        commentList=comments;
        this.context=context;
        this.handler=handler;
    }
}
