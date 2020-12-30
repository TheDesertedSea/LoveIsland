package com.example.uidesign.ui.thumb_to_me;

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
import com.example.uidesign.R;
import com.example.uidesign.data.Like;
import com.example.uidesign.net.NetSettings;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ThumbToMeAdapter extends RecyclerView.Adapter<ThumbToMeAdapter.ViewHolder>{
    private List<Like> likeList;
    private Context context;
    private ThumbToMeActivity.ThumbToMeActivityHandler thumbToMeActivityHandler;

    private final String baseIconUrl="http://"+ NetSettings.HOST_1 +":"+NetSettings.PORT_1+"/user/userPortrait/";

    @NonNull
    @Override
    public ThumbToMeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_or_thumb_to_me_item,parent,false);
        //LayoutInflat.from()从一个Context中，获得一个布局填充器，这样你就可以使用这个填充器来把xml布局文件转为View对象了。
        //LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item,parent,false);这样的方法来加载布局msg_item.xml
        return new ThumbToMeAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbToMeAdapter.ViewHolder holder, int position) {
        Like like=likeList.get(position);
        Glide.with(context)
                .load(baseIconUrl+like.from)
                .diskCacheStrategy(DiskCacheStrategy.NONE).into(holder.icon);
        holder.nickName.setText(like.fromName);
        holder.date.setText(like.nowDate.toString());
        holder.content.setText("在你的帖子下赞了你");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message message=thumbToMeActivityHandler.obtainMessage();
                message.what=200;
                message.arg1=like.postID;
                Log.v("adapter-id","id"+like.postID+" "+message.arg1);
                thumbToMeActivityHandler.sendMessage(message);
            }
        });
    }

    @Override
    public int getItemCount() {
        return likeList.size();
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

    public ThumbToMeAdapter(List<Like> likes, Context context, ThumbToMeActivity.ThumbToMeActivityHandler thumbToMeActivityHandler)
    {
        likeList=likes;
        this.context=context;
        this.thumbToMeActivityHandler=thumbToMeActivityHandler;
    }
}
