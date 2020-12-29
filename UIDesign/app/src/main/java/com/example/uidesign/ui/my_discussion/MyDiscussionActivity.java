package com.example.uidesign.ui.my_discussion;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uidesign.adapter.MyConfessionAdapter;
import com.example.uidesign.adapter.MyDiscussionAdapter;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityMyDiscussionBinding;
import com.example.uidesign.ui.confession.ConfessionItem;
import com.example.uidesign.ui.discussion.DiscussionItem;
import com.example.uidesign.ui.item_detail.ItemDetailActivity;
import com.example.uidesign.ui.my_confession.MyConfessionActivity;

import java.util.ArrayList;

public class MyDiscussionActivity extends BaseActivity {

    private ActivityMyDiscussionBinding binding;

    private LogginedUser Me = LogginedUser.getInstance();

    private RecyclerView discussionList;
    private ArrayList<DiscussionItem> listData;
    private MyDiscussionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMyDiscussionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent lastIntent=getIntent();
        int uid=lastIntent.getIntExtra("uid",-1);
        boolean sex=lastIntent.getBooleanExtra("sex",true);
        boolean me=lastIntent.getBooleanExtra("me",true);

        if(!me)
        {
            if(sex)
            {
                binding.myDiscussionTitle.setText("他的帖子");
            }else
            {
                binding.myDiscussionTitle.setText("她的帖子");
            }

        }

        discussionList = binding.recyclerView;

        initData();

        initListener();
    }

    //设置item点击的监听事件，点击后跳转到详情页
    private void initListener() {
        mAdapter.setOnItemClickListener(new MyDiscussionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //处理点击item的事件，跳转到item详情页
                Intent intent = new Intent(MyDiscussionActivity.this, ItemDetailActivity.class);
                MyDiscussionActivity.this.startActivity(intent);
            }
        });
    }

    //初始化数据
    private void initData() {
        //创建数据集合
        listData = new ArrayList<DiscussionItem>();
//
//        //自动刷新，从服务器请求表白帖数据初始化
//        refreshLayout.autoRefresh();

        //Recyclerview设置样式/布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        discussionList.setLayoutManager(layoutManager);
        //设置item的分割线
        discussionList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        //创建适配器
        mAdapter = new MyDiscussionAdapter(MyDiscussionActivity.this, listData);
        //适配器设置到Recyclerview里面去
        discussionList.setAdapter(mAdapter);
    }
}