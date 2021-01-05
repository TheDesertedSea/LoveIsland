package com.example.uidesign.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.uidesign.ProjectSettings;
import com.example.uidesign.R;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.net.NetSettings;
import com.example.uidesign.net.SocketMsg;
import com.example.uidesign.net.UserSocketManager;
import com.example.uidesign.ui.confession.ConfessionFragment;
import com.example.uidesign.ui.confession.ConfessionItem;
import com.example.uidesign.ui.item_detail.ItemDetailActivity;
import com.example.uidesign.ui.personal_page.PersonalPageActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

public class ConfessionListAdapter extends RecyclerView.Adapter<ConfessionListAdapter.InnerHolder> {
    private ArrayList<ConfessionItem> mData;
    private OnItemClickListener mOnItemClickListener;

    private ConfessionFragment thisFragment;
    private final String baseIconUrl="http://"+ NetSettings.HOST_1 +":"+NetSettings.PORT_1+"/user/userPortrait/";

    //构造方法
    public ConfessionListAdapter(ConfessionFragment fragment, ArrayList<ConfessionItem> data) {
        this.thisFragment = fragment;
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

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.mPosition);
                }
            }
        });

        holder.mLikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放动画
                if(mData.get(position).like_or_not==1)
                {
                    ((ImageButton)v).setImageResource(R.drawable.ic_like_non_24dp);
                } else if (mData.get(position).like_or_not == 0) {
                    ((ImageButton)v).setImageResource(R.drawable.ic_liked_24dp);

                    //告诉服务器端点赞
                    if(!ProjectSettings.UI_TEST) {
                        SocketMsg temp = new SocketMsg();
                        temp.type = "sendConfLike";
                        temp.from = LogginedUser.getInstance().getUid();
                        temp.fromName = LogginedUser.getInstance().getNickName();
                        temp.postID = mData.get(position).confessionID;
                        temp.nowDate = System.currentTimeMillis();
                        Gson gson = new Gson();
                        String sendString = gson.toJson(temp);

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    UserSocketManager.getInstance().getDataOutputStream().writeUTF(sendString);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }


            }
        });

        holder.mCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到详情页面
                Intent intent = new Intent(thisFragment.getContext(), ItemDetailActivity.class);
                //发送帖子id
                intent.putExtra("type","confession");
                intent.putExtra("postID", mData.get(position).confessionID);
                intent.putExtra("uid", mData.get(position).uid);
                intent.putExtra("content", mData.get(position).content_text);
                thisFragment.getActivity().startActivity(intent);
            }
        });

        holder.mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(thisFragment.getContext(), PersonalPageActivity.class);
                intent.putExtra("uid",mData.get(position).uid);
                thisFragment.getActivity().startActivity(intent);
            }
        });

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

//    public void setOnItemLikeClickListener(ItemInnerLikeListener mItemInnerLikeListener) {
//        this.mItemInnerLikeListener = mItemInnerLikeListener;
//    }
//
//    public void setOnItemCommentClickListener(ItemInnerCommentListener mItemInnerCommentListener) {
//        this.mItemInnerCommentListener = mItemInnerCommentListener;
//    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        private ImageView mAvatar;
        private TextView mUsername;
        private TextView mContentText;
        private TextView timeText;
//        private ImageView mContentImage;
        private ImageButton mLikeButton;
        private ImageButton mCommentButton;
        private int mPosition;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);

            //找到条目的控件
            mAvatar = (ImageView) itemView.findViewById(R.id.item_title_avatar);
            mUsername = (TextView) itemView.findViewById(R.id.item_title_username);
            mContentText = (TextView) itemView.findViewById(R.id.item_content_text);
            timeText=itemView.findViewById(R.id.time);
//            mContentImage = (ImageView) itemView.findViewById(R.id.item_content_image);
            mLikeButton = (ImageButton) itemView.findViewById(R.id.item_like);
            mCommentButton = (ImageButton) itemView.findViewById(R.id.item_comment);
        }

        //用于设置数据
        public void setData(ConfessionItem confessionItem, int position) {

            this.mPosition = position;
            //开始设置数据
            if(!ProjectSettings.UI_TEST) {
                Glide.with(thisFragment).load(baseIconUrl + confessionItem.uid).diskCacheStrategy(DiskCacheStrategy.NONE).into(mAvatar);
            }
            mUsername.setText(confessionItem.title_username);
            mContentText.setText(confessionItem.content_text);
            timeText.setText(confessionItem.time.toString());
            if (confessionItem.like_or_not == 1) {

                mLikeButton.setImageResource(R.drawable.ic_liked_24dp);
            }
//            mContentImage.setImageResource(confessionItem.content_imageId);
        }
    }
}
