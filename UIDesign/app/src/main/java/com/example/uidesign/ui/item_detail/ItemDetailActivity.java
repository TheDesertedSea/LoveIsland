package com.example.uidesign.ui.item_detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.uidesign.ProjectSettings;
import com.example.uidesign.R;
import com.example.uidesign.adapter.CommentListAdapter;
import com.example.uidesign.data.Comment;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.data.UserInfo;
import com.example.uidesign.net.NetGetCommentOfConfession;
import com.example.uidesign.net.NetGetCommentOfDiscussion;
import com.example.uidesign.net.NetSettings;
import com.example.uidesign.net.SocketMsg;
import com.example.uidesign.net.UserSocketManager;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityItemDetailBinding;
import com.example.uidesign.ui.item_edit.ItemEditActivity;
import com.example.uidesign.ui.personal_page.PersonalPageActivity;
import com.google.gson.Gson;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class ItemDetailActivity extends BaseActivity {

    private ActivityItemDetailBinding binding;
    private String identifyString;
    private int postID;
    private ArrayList<Comment> comments = new ArrayList<Comment>();

    private final String TAG = "ItemDetailActivity";

    private int uidOfPost;
    private UserInfo UserInfoOfPost;
    private String ContextOfPost;
    private String nicknameOfPost;
    private int likeOrNotOfPost;

    private final String baseIconUrl="http://"+ NetSettings.HOST_1 +":"+NetSettings.PORT_1+"/user/userPortrait/";

    private RecyclerView commentList;
    private CommentListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        identifyString = intent.getStringExtra("type");
//        Log.v(TAG, "type" + identifyString);
        postID = intent.getIntExtra("postID", -1);
//        Log.v(TAG, "postid" + postID);

        //得到帖子详细信息，并展示
        uidOfPost = intent.getIntExtra("uid", -1);
        nicknameOfPost = intent.getStringExtra("nickname");
        likeOrNotOfPost = intent.getIntExtra("likeOrNot", -1);
//        Log.v(TAG, "uid" + uidOfPost);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
////                Log.v(TAG, "进新进程");
//                NetPersonalCenter netPersonalCenter1 = new NetPersonalCenter();
////                Log.v(TAG, "new了一个");
//                UserInfoOfPost = netPersonalCenter1.getUserInfo(uidOfPost);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        binding.username.setText(UserInfoOfPost.nickname);
//                    }
//                });
//
////                Log.v(TAG, UserInfoOfPost.nickname);
////                Log.v(TAG, UserInfoOfPost.sex + "xingbie");
////                Log.v(TAG, "得到了返回结果");
//            }
//        }).start();

        ContextOfPost = intent.getStringExtra("content");

        Glide.with(this).load(baseIconUrl + uidOfPost).diskCacheStrategy(DiskCacheStrategy.NONE).into(binding.avatar);
        binding.content.setText(ContextOfPost);

        //看是否已经点赞
        if (likeOrNotOfPost == 1) {
            binding.like.setImageResource(R.drawable.ic_liked_24dp);
        }

        //通过帖子id获取帖子的评论列表，并展示
        binding.refreshLayout.autoRefresh();

        commentList = binding.commentList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        commentList.setLayoutManager(layoutManager);
        commentList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new CommentListAdapter(this, comments);
        commentList.setAdapter(mAdapter);

        //处理下拉刷新.ing
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
//                        if (comments != null) {
//                            comments.clear();
//                        } else {
//                            comments = new ArrayList<Comment>();
//                        }
                        if (identifyString.equals("confession")) {
                            NetGetCommentOfConfession netGetCommentOfConfession = new NetGetCommentOfConfession();
                            NetGetCommentOfConfession.ResponseClass mResponseClass = new NetGetCommentOfConfession.ResponseClass();
                            mResponseClass = netGetCommentOfConfession.getComment(postID, LogginedUser.getInstance().getUid());
                            if (mResponseClass != NetGetCommentOfConfession.FAIL) {
                                //更新现在最大的表白帖id
//                            LogginedUser.getInstance().setConfession_MaxID(mResponseClass.maxID);
                                //把取得的数据更新到数据集中
                                ArrayList<NetGetCommentOfConfession.ResponseItem> mResponseItemList = mResponseClass.commentArray;

                                for (NetGetCommentOfConfession.ResponseItem i : mResponseItemList) {
                                    Comment addingItem = new Comment();
                                    addingItem.from = i.uid;
                                    addingItem.fromName = i.nickname;
                                    addingItem.commentID = i.confession_commentID;
                                    addingItem.com = i.ccCont;
                                    addingItem.postID = i.confessionID;
                                    addingItem.nowDate = new Date(i.ccTime);

                                    comments.add(addingItem);
                                    Log.v("TAG", "add");
                                }
                            } else {
                                refreshLayout.finishRefresh(false);
                            }
                        } else if (identifyString.equals("discussion")) {
                            NetGetCommentOfDiscussion netGetCommentOfDiscussion = new NetGetCommentOfDiscussion();
                            NetGetCommentOfDiscussion.ResponseClass mResponseClass = new NetGetCommentOfDiscussion.ResponseClass();
                            mResponseClass = netGetCommentOfDiscussion.getComment(LogginedUser.getInstance().getConfession_MaxID(), LogginedUser.getInstance().getUid());
                            if (mResponseClass != NetGetCommentOfDiscussion.FAIL) {
                                //更新现在最大的表白帖id
//                            LogginedUser.getInstance().setConfession_MaxID(mResponseClass.maxID);
                                //把取得的数据更新到数据集中
                                ArrayList<NetGetCommentOfDiscussion.ResponseItem> mResponseItemList = mResponseClass.commentArray;

                                for (NetGetCommentOfDiscussion.ResponseItem i : mResponseItemList) {
                                    Comment addingItem = new Comment();
                                    addingItem.from = i.uid;
                                    addingItem.fromName = i.nickname;
                                    addingItem.commentID = i.discuss_commentID;
                                    addingItem.com = i.dcCont;
                                    addingItem.postID = i.discussID;
                                    addingItem.nowDate = new Date(i.dcTime);

                                    comments.add(addingItem);
                                }
                            } else {
                                refreshLayout.finishRefresh(false);
                            }
                        }

                    }
                }).start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //让更新停止,更新列表
                        binding.refreshLayout.finishRefresh();
                        Log.v("CommentListAdapter", "结束刷新");
                        mAdapter.notifyDataSetChanged();
                    }
                }, 2000);

            }
        });



        //发表评论
        binding.sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.v(TAG, "fabiao comment");
                Log.v(TAG, identifyString);
                if (identifyString.equals("confession")) {
                    SocketMsg temp = new SocketMsg();
                    temp.type = "sendConfCom";
                    temp.from = LogginedUser.getInstance().getUid();
                    temp.fromName = LogginedUser.getInstance().getNickName();
                    //被评论帖子的id
                    temp.postID = postID;
                    temp.msg = binding.editComment.getText().toString();
                    temp.nowDate = System.currentTimeMillis();
                    Gson gson = new Gson();
                    String sendString = gson.toJson(temp);

                    if(!ProjectSettings.UI_TEST) {
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
                    //把键盘收下
                    binding.editComment.setText("");
                    binding.editComment.clearFocus();
                    InputMethodManager imm = (InputMethodManager) ItemDetailActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ItemDetailActivity.this.getWindow().getDecorView().getWindowToken(), 0);
                    //刷新评论列表
                    binding.refreshLayout.autoRefresh();
                } else if (identifyString.equals("discussion")) {
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
                    if(!ProjectSettings.UI_TEST) {
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
                    //把键盘收下
                    binding.editComment.setText("");
                    binding.editComment.clearFocus();
                    InputMethodManager imm = (InputMethodManager) ItemDetailActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(ItemDetailActivity.this.getWindow().getDecorView().getWindowToken(), 0);
                    //刷新评论列表
                    binding.refreshLayout.autoRefresh();
                }
            }
        });

        binding.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ItemDetailActivity.this, PersonalPageActivity.class);
                intent.putExtra("uid",uidOfPost);
                ItemDetailActivity.this.startActivity(intent);
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ItemDetailActivity.this.finish();
            }
        });
        
    }
}