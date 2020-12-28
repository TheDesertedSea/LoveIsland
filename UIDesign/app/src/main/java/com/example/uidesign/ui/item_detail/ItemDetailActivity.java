package com.example.uidesign.ui.item_detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.uidesign.adapter.CommentListAdapter;
import com.example.uidesign.data.Comment;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.data.UserInfo;
import com.example.uidesign.net.NetGetCertainConfession;
import com.example.uidesign.net.NetGetCertainDiscussion;
import com.example.uidesign.net.NetPersonalCenter;
import com.example.uidesign.net.SocketMsg;
import com.example.uidesign.net.UserSocketManager;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityItemDetailBinding;
import com.example.uidesign.ui.confession.ConfessionItem;
import com.example.uidesign.ui.discussion.DiscussionItem;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

public class ItemDetailActivity extends BaseActivity {

    private ActivityItemDetailBinding binding;
    private String identifyString;
    private int postID;
    private ArrayList<Comment> comments;

    private int uidOfPost;
    private UserInfo UserInfoOfPost;
    private String ContextOfPost;

    private final String HOST="";
    private final String baseIconUrl="http://"+HOST+":30010/user/userPortrait/";

    private RecyclerView commentList;
    private CommentListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        identifyString = intent.getStringExtra("type");
        postID = intent.getIntExtra("postID", -1);

        //得到帖子详细信息，并展示
        uidOfPost = intent.getIntExtra("uid", -1);
        NetPersonalCenter netPersonalCenter1 = new NetPersonalCenter();
        UserInfoOfPost = netPersonalCenter1.getUserInfo(uidOfPost);
        ContextOfPost = intent.getStringExtra("content");
        binding.username.setText(UserInfoOfPost.nickName);
        Glide.with(this).load(baseIconUrl + uidOfPost).into(binding.avatar);
        binding.content.setText(ContextOfPost);

        //通过帖子id获取帖子的评论列表，并展示
        if (identifyString == "confession") {
            NetGetCertainConfession netGetCertainConfession = new NetGetCertainConfession();
            NetGetCertainConfession.ResponseClass mResponseClass = new NetGetCertainConfession.ResponseClass();
            mResponseClass = netGetCertainConfession.getComment(postID, LogginedUser.getInstance().getUid());
            if (mResponseClass != NetGetCertainConfession.FAIL) {
                comments = new ArrayList<Comment>();
                Comment temp = new Comment();
                UserInfo ownerOfComment = new UserInfo();
                for (NetGetCertainConfession.ResponseItem i : mResponseClass.commentArray) {
                    temp.commentID = i.commentID;
                    temp.from = i.uid;
                    //通过uid获得用户名
                    NetPersonalCenter netPersonalCenter = new NetPersonalCenter();
                    ownerOfComment = netPersonalCenter.getUserInfo(i.uid);
                    temp.fromName = ownerOfComment.nickName;
                    temp.com = i.content;
                    temp.nowDate = i.time;
                    comments.add(temp);
                }
            }
        } else if (identifyString == "discussion") {
            NetGetCertainDiscussion netGetCertainDiscussion = new NetGetCertainDiscussion();
            NetGetCertainDiscussion.ResponseClass mResponseClass = new NetGetCertainDiscussion.ResponseClass();
            mResponseClass = netGetCertainDiscussion.getComment(postID, LogginedUser.getInstance().getUid());
            if (mResponseClass != NetGetCertainDiscussion.FAIL) {
                comments = new ArrayList<Comment>();
                Comment temp = new Comment();
                UserInfo ownerOfComment = new UserInfo();
                for (NetGetCertainDiscussion.ResponseItem i : mResponseClass.commentArray) {
                    temp.commentID = i.commentID;
                    temp.from = i.uid;
                    //通过uid获得用户名
                    NetPersonalCenter netPersonalCenter = new NetPersonalCenter();
                    ownerOfComment = netPersonalCenter.getUserInfo(i.uid);
                    temp.fromName = ownerOfComment.nickName;
                    temp.com = i.content;
                    temp.nowDate = i.time;
                    comments.add(temp);
                }
            }
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        commentList.setLayoutManager(layoutManager);
        commentList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new CommentListAdapter(this, comments);
        commentList.setAdapter(mAdapter);

        //发表评论
        binding.sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (identifyString == "confession") {
                    SocketMsg temp = new SocketMsg();
                    temp.type = "shielding";
                    temp.from = LogginedUser.getInstance().getUid();
                    temp.fromName = LogginedUser.getInstance().getNickName();
                    //被评论帖子的id
                    temp.postID = postID;
                    temp.msg = binding.editComment.getText().toString();
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
                } else if (identifyString == "discussion") {
                    SocketMsg temp = new SocketMsg();
                    temp.type = "sendDisCom";
                    temp.from = LogginedUser.getInstance().getUid();
                    temp.fromName = LogginedUser.getInstance().getNickName();
                    //被评论帖子的id
                    temp.postID = postID;
                    temp.msg = binding.editComment.getText().toString();
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
        });
    }
}