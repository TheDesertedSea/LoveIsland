package com.example.uidesign.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uidesign.R;
import com.example.uidesign.ui.confession.ConfessionFragment;
import com.example.uidesign.ui.confession.ConfessionItem;
import java.util.ArrayList;
import java.util.List;

public class ConfessionListAdapter extends RecyclerView.Adapter<ConfessionListAdapter.InnerHolder> {
    private ArrayList<ConfessionItem> mData;
    private OnItemClickListener mOnItemClickListener;

    private ConfessionFragment thisContext;
    private final String HOST="";
    private final String baseIconUrl="http://"+HOST+":30010/user/userPortrait/";

    //构造方法
    public ConfessionListAdapter(ConfessionFragment context, ArrayList<ConfessionItem> data) {
        this.thisContext = context;
        this.mData = data;
    }

    //用来创建条目View
    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //传进去的View是条目的界面
        //拿到view
        //创建内部holder
        View view = View.inflate(parent.getContext(), R.layout.item_confession_recyclerview, null);
        return new InnerHolder(view);
    }

    //用于绑定Holder，一般用来设置数据
    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //在这里设置数据
        holder.setData(mData.get(position), position);
    }

    //返回条目个数
    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    /*编写回调的步骤
     * 1.创建这个接口
     * 2.定义接口内部方法
     * 3.提供设置接口的方法（外部实现）
     * 4.接口方法的调用*/
    public void setOnItemClickListener(OnItemClickListener listener) {
        //设置监听（就是一个回调的接口）
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        private ImageView mAvatar;
        private TextView mUsername;
        private TextView mContentText;
        private ImageView mContentImage;
        private int mPosition;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);

            //找到条目的控件
            mAvatar = (ImageView) itemView.findViewById(R.id.item_title_avatar);
            mUsername = (TextView) itemView.findViewById(R.id.item_title_username);
            mContentText = (TextView) itemView.findViewById(R.id.item_content_text);
            mContentImage = (ImageView) itemView.findViewById(R.id.item_content_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mPosition);
                    }
                }
            });
        }

        //用于设置数据
        public void setData(ConfessionItem confessionItem, int position) {

            this.mPosition = position;
            //开始设置数据
            Glide.with(thisContext).load(baseIconUrl + confessionItem.uid).into(mAvatar);
            mUsername.setText(confessionItem.title_username);
            mContentText.setText(confessionItem.content_text);
            mContentImage.setImageResource(confessionItem.content_imageId);
        }
    }
}
