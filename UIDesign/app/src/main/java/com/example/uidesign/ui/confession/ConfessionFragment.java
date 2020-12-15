package com.example.uidesign.ui.confession;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.example.uidesign.R;
import com.example.uidesign.databinding.FragmentConfessionBinding;
import com.example.uidesign.ui.item_edit.ItemEditActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

public class ConfessionFragment extends Fragment {

    private FragmentConfessionBinding binding;

    private Button EditItemButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentConfessionBinding.inflate(getLayoutInflater());
        RefreshLayout refreshLayout = (RefreshLayout) binding.refreshLayout;
        refreshLayout.setRefreshHeader(new ClassicsHeader(getContext()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_confession, container, false);

        //点击按钮编辑表白帖
        EditItemButton = root.findViewById(R.id.add_Button);
        EditItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到编辑页面
                Intent intent = new Intent(getActivity(), ItemEditActivity.class);
                intent.setAction("edit");   //这个intent的action叫做"edit"
                getActivity().startActivity(intent);
            }
        });

        return root;
    }

    protected void Refresh() {

    }
}