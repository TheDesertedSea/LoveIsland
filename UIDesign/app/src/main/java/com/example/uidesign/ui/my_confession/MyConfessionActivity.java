package com.example.uidesign.ui.my_confession;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityMyConfessionBinding;

public class MyConfessionActivity extends BaseActivity {

    private ActivityMyConfessionBinding binding;
    private Activity thisActivity=this;

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

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisActivity.finish();
            }
        });

    }
}