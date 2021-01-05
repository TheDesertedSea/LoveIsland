package com.example.uidesign.ui.comment_to_me;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.uidesign.data.Comment;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.data.database.DatabaseManager;
import com.example.uidesign.data.database.Entity_Comment;
import com.example.uidesign.net.NetGetConfession;
import com.example.uidesign.net.NetGetDiscussion;
import com.example.uidesign.tool.CommentAndLikeAdapterSend;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityCommentToMeBinding;
import com.example.uidesign.ui.item_detail.ItemDetailActivity;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CommentToMeActivity extends BaseActivity {

    private ActivityCommentToMeBinding binding;
    private Context thisContext=this;
    private Activity thisActivity=this;

    private List<Comment> commentList;
    private List<Entity_Comment> entity_comments;
    private CommentToMeAdapter commentToMeAdapter;
    private CommentToMeActivityHandler commentToMeActivityHandler=new CommentToMeActivityHandler();

    public class CommentToMeActivityHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int arg1=msg.arg1;
            CommentAndLikeAdapterSend obj=(CommentAndLikeAdapterSend)msg.obj;
            switch (msg.what)
            {
                case 100:
                    for(Entity_Comment e:entity_comments)
                    {
                        Comment comment=new Comment();
                        comment.from=e.from;
                        comment.fromName=e.fromName;
                        comment.com=e.content;
                        comment.nowDate=new Date(e.date);
                        comment.type=e.type;
                        commentList.add(comment);
                    }

                    commentToMeAdapter=new CommentToMeAdapter(commentList,thisContext,commentToMeActivityHandler);
                    binding.commentRecyclerView.setAdapter(commentToMeAdapter);
                    break;
                case 200:

                    if(obj.type.equals("receiveConfCom"))
                    {
                        NetGetConfession netGetConfession=new NetGetConfession();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Log.v("msg-arg","msg-arg"+arg1);
                                NetGetConfession.SingleGetResponse response=netGetConfession
                                        .getSingleConfession(arg1,LogginedUser.getInstance().getUid());
                                Log.v("result",String.valueOf(response.success));
                                if(response.success==1)
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(thisContext, ItemDetailActivity.class);
                                            //发送帖子id
                                            intent.putExtra("type","confession");
                                            intent.putExtra("postID", response.Obj.confessionID);
                                            intent.putExtra("uid", response.Obj.uid);
                                            intent.putExtra("content", response.Obj.confCont);
                                            intent.putExtra("nickname",obj.nickname);
                                            intent.putExtra("likeOrNot",response.Obj.bool_like);
                                            startActivity(intent);
                                        }
                                    });
                                }

                            }
                        }).start();
                    }else
                    {
                        NetGetDiscussion netGetDiscussion=new NetGetDiscussion();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                NetGetDiscussion.SingleGetResponse response=netGetDiscussion.getSingleDiscussion(arg1);
                                if(response.success==1)
                                {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(thisContext, ItemDetailActivity.class);
                                            //发送帖子id
                                            intent.putExtra("type","confession");
                                            intent.putExtra("postID", response.Obj.discussID);
                                            intent.putExtra("uid", response.Obj.uid);
                                            intent.putExtra("content", response.Obj.disCont);
                                            startActivity(intent);
                                        }
                                    });
                                }

                            }
                        }).start();
                    }

            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCommentToMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.commentRecyclerView.setLayoutManager(new LinearLayoutManager(thisContext));
        binding.commentRecyclerView.setAdapter(null);
        commentList=new ArrayList<Comment>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                entity_comments= DatabaseManager.getAppDatabase().dao_comment().getComments(LogginedUser.getInstance().getUid());
                Message message=commentToMeActivityHandler.obtainMessage();
                message.what=100;
                commentToMeActivityHandler.sendMessage(message);
            }
        }).start();



        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisActivity.finish();
            }
        });

    }
}