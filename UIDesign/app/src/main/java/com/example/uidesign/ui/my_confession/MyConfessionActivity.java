package com.example.uidesign.ui.my_confession;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uidesign.R;
import com.example.uidesign.adapter.ConfessionListAdapter;
import com.example.uidesign.adapter.MyConfessionAdapter;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.net.NetGetUserConfession;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityMyConfessionBinding;
import com.example.uidesign.ui.confession.ConfessionFragment;
import com.example.uidesign.ui.confession.ConfessionItem;
import com.example.uidesign.ui.item_detail.ItemDetailActivity;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;

public class MyConfessionActivity extends BaseActivity {

    private ActivityMyConfessionBinding binding;
    private Activity thisActivity=this;

//    private final MyConfessionActivity thisContext = this;
    private LogginedUser Me = LogginedUser.getInstance();

    private int ouid;

    private RecyclerView confessionList;
    private ArrayList<ConfessionItem> listData;
    private MyConfessionAdapter mAdapter;
//    private RefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMyConfessionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent lastIntent=getIntent();
        int uid=lastIntent.getIntExtra("uid",-1);
        boolean sex=lastIntent.getBooleanExtra("sex",true);
        boolean me=lastIntent.getBooleanExtra("me",true);

        ouid = uid;

        if(!me)
        {
            if(sex)
            {
                binding.myConfessionTitle.setText("他的帖子");
            }else
            {
                binding.myConfessionTitle.setText("她的帖子");
            }

        }

        confessionList = binding.recyclerView;

        initData();

        initListener();

    }

    //设置item点击的监听事件，点击后跳转到详情页
    private void initListener() {
        mAdapter.setOnItemClickListener(new MyConfessionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //处理点击item的事件，跳转到item详情页
                Intent intent = new Intent(MyConfessionActivity.this, ItemDetailActivity.class);
                intent.putExtra("type","confession");
                intent.putExtra("postID", listData.get(position).confessionID);
                intent.putExtra("uid", listData.get(position).uid);
                intent.putExtra("content", listData.get(position).content_text);
                intent.putExtra("likeOrNot",listData.get(position).like_or_not);
                intent.putExtra("nickname",listData.get(position).title_username);
                MyConfessionActivity.this.startActivity(intent);
            }
        });
    }

    //初始化数据
    private void initData() {
        //创建数据集合
        listData = new ArrayList<ConfessionItem>();
//
//        //自动刷新，从服务器请求表白帖数据初始化
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetGetUserConfession netGetUserConfession = new NetGetUserConfession();
                NetGetUserConfession.ResponseClass mResponseClass = new NetGetUserConfession.ResponseClass();
                mResponseClass = netGetUserConfession.getConfession(ouid, Me.getUid());
                if (mResponseClass != NetGetUserConfession.FAIL) {

                    for (NetGetUserConfession.ResponseItem i : mResponseClass.confessionArray) {
                        ConfessionItem addingItem = new ConfessionItem();
                        addingItem.uid = i.uid;
                        addingItem.title_username = i.nickname;
                        addingItem.confessionID = i.confessionID;
                        addingItem.content_text = i.confCont;
                        addingItem.like_or_not = i.bool_like;

                        listData.add(0,addingItem);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        binding.recyclerView.scrollToPosition(listData.size()-1);
                    }
                });
            }
        }).start();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisActivity.finish();
            }
        });

        //Recyclerview设置样式/布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        confessionList.setLayoutManager(layoutManager);
        //设置item的分割线
        confessionList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //创建适配器
        mAdapter = new MyConfessionAdapter(MyConfessionActivity.this, listData);
        //适配器设置到Recyclerview里面去
        confessionList.setAdapter(mAdapter);
    }
}