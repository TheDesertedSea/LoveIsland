package com.example.uidesign.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.uidesign.R;
import com.example.uidesign.data.Comment;
import com.example.uidesign.net.NetSettings;
import com.example.uidesign.ui.item_detail.ItemDetailActivity;
import com.example.uidesign.ui.personal_page.PersonalPageActivity;

import java.util.ArrayList;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.InnerHolder> {
    private ArrayList<Comment> mData;

    private ItemDetailActivity thisActivity;
    private final String baseIconUrl="http://"+ NetSettings.HOST_1 +":"+NetSettings.PORT_1+"/user/userPortrait/";

    //构造方法
    public CommentListAdapter (ItemDetailActivity activity, ArrayList<Comment> data) {
        this.mData = data;
        this.thisActivity = activity;
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_comment_recyclerview, null);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        Log.v("CommentListAdapter", "进入bind view");
        holder.setData(mData.get(position), position);
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(thisActivity, PersonalPageActivity.class);
                intent.putExtra("uid",mData.get(position).from);
                thisActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        private ImageView avatar;
        private TextView username;
        private TextView context;
        private TextView timeText;
        private int mPosition;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.comment_avatar);
            username = itemView.findViewById(R.id.comment_username);
            context = itemView.findViewById(R.id.comment_context);
        }

        public void setData(Comment comment, int position) {
            Log.v("CommentListAdapter", "进入setdata");
            this.mPosition = position;
            Glide.with(thisActivity).load(baseIconUrl + comment.from).diskCacheStrategy(DiskCacheStrategy.NONE).into(avatar);
            username.setText(comment.fromName);
            context.setText(comment.com);
        }
    }



}
