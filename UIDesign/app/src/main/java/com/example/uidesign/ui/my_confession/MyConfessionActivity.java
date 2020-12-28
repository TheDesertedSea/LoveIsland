package com.example.uidesign.ui.my_confession;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uidesign.R;
import com.example.uidesign.adapter.ConfessionListAdapter;
import com.example.uidesign.adapter.MyConfessionAdapter;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityMyConfessionBinding;
import com.example.uidesign.ui.confession.ConfessionFragment;
import com.example.uidesign.ui.confession.ConfessionItem;
import com.example.uidesign.ui.item_detail.ItemDetailActivity;
import com.scwang.smart.refresh.layout.api.RefreshLayout;

import java.util.ArrayList;

public class MyConfessionActivity extends BaseActivity {

    private ActivityMyConfessionBinding binding;

//    private final MyConfessionActivity thisContext = this;
    private LogginedUser Me = LogginedUser.getInstance();

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
//        refreshLayout.autoRefresh();

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