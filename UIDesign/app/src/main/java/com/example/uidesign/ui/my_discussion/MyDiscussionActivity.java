package com.example.uidesign.ui.my_discussion;

import android.content.Intent;
import android.os.Bundle;

import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityMyDiscussionBinding;

public class MyDiscussionActivity extends BaseActivity {

    private ActivityMyDiscussionBinding binding;

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
    }
}