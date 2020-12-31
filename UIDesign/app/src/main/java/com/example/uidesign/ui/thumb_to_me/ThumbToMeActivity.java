package com.example.uidesign.ui.thumb_to_me;

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
import com.example.uidesign.data.Like;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.data.database.DatabaseManager;
import com.example.uidesign.data.database.Entity_Comment;
import com.example.uidesign.data.database.Entity_Like;
import com.example.uidesign.net.NetGetConfession;
import com.example.uidesign.net.NetGetDiscussion;
import com.example.uidesign.tool.CommentAndLikeAdapterSend;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityThumbToMeBinding;
import com.example.uidesign.ui.comment_to_me.CommentToMeAdapter;
import com.example.uidesign.ui.item_detail.ItemDetailActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ThumbToMeActivity extends BaseActivity {

    private ActivityThumbToMeBinding binding;
    private Context thisContext=this;
    private Activity thisActivity=this;

    private List<Like> likeList;
    private List<Entity_Like> entity_likes;
    private ThumbToMeAdapter thumbToMeAdapter;
    private ThumbToMeActivity.ThumbToMeActivityHandler thumbToMeActivityHandler=new ThumbToMeActivityHandler();

    public class ThumbToMeActivityHandler extends Handler
    {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.v("msg-arg","msg-arg"+msg.arg1);
            int arg1=msg.arg1;
            CommentAndLikeAdapterSend obj=(CommentAndLikeAdapterSend)msg.obj;
            switch (msg.what)
            {
                case 100:
                    for(Entity_Like e:entity_likes)
                    {
                        Like like=new Like();
                        like.from=e.from;
                        like.fromName=e.fromName;
                        like.nowDate=new Date(e.date);
                        like.postID=e.postID;
                        Log.v("id","id"+e.postID);
                        like.type=e.type;
                        likeList.add(like);
                    }

                    thumbToMeAdapter=new ThumbToMeAdapter(likeList,thisContext,thumbToMeActivityHandler);
                    binding.likeRecyclerView.setAdapter(thumbToMeAdapter);
                    break;
                case 200:
                    if(obj.type.equals("receiveConfLike"))
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
        binding=ActivityThumbToMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        likeList=new ArrayList<Like>();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(thisContext);
        binding.likeRecyclerView.setLayoutManager(linearLayoutManager);
        new Thread(new Runnable() {
            @Override
            public void run() {
                entity_likes= DatabaseManager.getAppDatabase().dao_like().getLikes(LogginedUser.getInstance().getUid());
                Log.v("like_list",String.valueOf(entity_likes.size()));
                Message message=thumbToMeActivityHandler.obtainMessage();
                message.what=100;
                thumbToMeActivityHandler.sendMessage(message);
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