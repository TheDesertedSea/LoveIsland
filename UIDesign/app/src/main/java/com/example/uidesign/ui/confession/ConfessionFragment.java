package com.example.uidesign.ui.confession;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.uidesign.R;
import com.example.uidesign.adapter.ConfessionListAdapter;
import com.example.uidesign.data.LogginedUser;
import com.example.uidesign.data.UserInfo;
import com.example.uidesign.net.NetGetConfession;
import com.example.uidesign.net.NetPersonalCenter;
import com.example.uidesign.ui.item_detail.ItemDetailActivity;
import com.example.uidesign.ui.item_edit.ItemEditActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class ConfessionFragment extends Fragment {

//    private ConfessionFragment confessionFragment = this;
    private LogginedUser Me = LogginedUser.getInstance();

    private FloatingActionButton EditItemButton;

    private RecyclerView confessionList;
    private ArrayList<ConfessionItem> listData;
    private ConfessionListAdapter mAdapter;
    private RefreshLayout refreshLayout;

    private UserInfo addUserInfo = new UserInfo();




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_confession, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        //点击按钮编辑表白帖
        EditItemButton = root.findViewById(R.id.add_Button);
        EditItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到编辑页面
                Intent intent = new Intent(getActivity(), ItemEditActivity.class);
                intent.putExtra("name", "confession");
                getActivity().startActivity(intent);
            }
        });


        //下拉刷新
        //找到控件
        confessionList = (RecyclerView) root.findViewById(R.id.recyclerView);
        refreshLayout = (RefreshLayout) root.findViewById(R.id.refreshLayout);

        //准备数据
        //从服务器获取初始数据
        initData();

        //初始化item点击事件
        initListener();

        //处理下拉刷新
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NetGetConfession netGetConfession = new NetGetConfession();
                        NetGetConfession.ResponseClass mResponseClass = new NetGetConfession.ResponseClass();
                        mResponseClass = netGetConfession.getConfession(Me.getConfession_MaxID(), Me.getUid());
                        if (mResponseClass != NetGetConfession.FAIL) {
                            //更新现在最大的表白帖id
                            Me.setConfession_MaxID(mResponseClass.maxID);
                            //把取得的数据更新到数据集中
                            ArrayList<NetGetConfession.ResponseItem> mResponseItemList = mResponseClass.confessionArray;
                            listData.clear();
                            for (NetGetConfession.ResponseItem i : mResponseItemList) {
                                ConfessionItem addingItem = new ConfessionItem();
                                addingItem.confessionID = i.confessionID;
                                addingItem.uid = i.uid;
                                addingItem.content_text = i.confCont;
                                addingItem.like_or_not = i.bool_like;
                                addingItem.title_username = i.nickname;
                                addingItem.time=new Date(i.confTime);
//                                //通过获得的uid去取得用户名
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        NetPersonalCenter mNetPersonalCenter = new NetPersonalCenter();
//                                        addUserInfo = mNetPersonalCenter.getUserInfo(i.uid);
//                                        addingItem.title_username = addUserInfo.nickname;
//                                    }
//                                }).start();

                                listData.add(0,addingItem);
//                                Log.v("ConfessionListAdapter", "like" + i.bool_like);
                            }
                        } else {
                            refreshLayout.finishRefresh(false);
//                            Toast.makeText(thisContext, NetGetConfession.FAIL_INFO, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).start();

                //更新UI
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //让更新停止,更新列表
                        refreshLayout.finishRefresh();
                        mAdapter.notifyDataSetChanged();
                        confessionList.scrollToPosition(listData.size()-1);
                    }
                }, 2000);
            }
        });

    }

    //设置item点击的监听事件，点击后跳转到详情页
    private void initListener() {
        mAdapter.setOnItemClickListener(new ConfessionListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Log.v(TAG, "点击item");
                //处理点击item的事件，跳转到item详情页
                Intent intent = new Intent(getActivity(), ItemDetailActivity.class);
                //发送帖子id
                intent.putExtra("type","confession");
                intent.putExtra("postID", listData.get(position).confessionID);
                intent.putExtra("uid", listData.get(position).uid);
                intent.putExtra("content", listData.get(position).content_text);
                intent.putExtra("likeOrNot",listData.get(position).like_or_not);
                intent.putExtra("nickname",listData.get(position).title_username);
                getActivity().startActivity(intent);
            }
        });
    }

    //初始化数据
    private void initData() {
        //创建数据集合
        listData = new ArrayList<ConfessionItem>();

        //自动刷新，从服务器请求表白帖数据初始化
        refreshLayout.autoRefresh();

        //Recyclerview设置样式/布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        confessionList.setLayoutManager(layoutManager);
        //设置item的分割线
//        confessionList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        //创建适配器
        mAdapter = new ConfessionListAdapter(this, listData);
        //适配器设置到Recyclerview里面去
        confessionList.setAdapter(mAdapter);
    }

}