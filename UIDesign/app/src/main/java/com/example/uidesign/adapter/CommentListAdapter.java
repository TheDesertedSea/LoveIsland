package com.example.uidesign.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uidesign.R;
import com.example.uidesign.data.Comment;
import com.example.uidesign.ui.item_detail.ItemDetailActivity;

import java.util.ArrayList;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.InnerHolder> {
    private ArrayList<Comment> mData;

    private ItemDetailActivity thisContext;
    private final String HOST="";
    private final String baseIconUrl="http://"+HOST+":30010/user/userPortrait/";

    //构造方法
    public CommentListAdapter (ItemDetailActivity context, ArrayList<Comment> data) {
        this.mData = data;
        this.thisContext = context;
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_comment_recyclerview, null);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        holder.setData(mData.get(position), position);
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
        private int mPosition;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);

            avatar = itemView.findViewById(R.id.comment_avatar);
            username = itemView.findViewById(R.id.comment_username);
            context = itemView.findViewById(R.id.comment_context);
        }

        public void setData(Comment comment, int position) {
            this.mPosition = position;
            Glide.with(thisContext).load(baseIconUrl + comment.from).into(avatar);
            username.setText(comment.fromName);
            context.setText(comment.com);
        }
    }



}
