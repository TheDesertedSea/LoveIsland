package com.example.uidesign.ui.my_discussion;

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
    }
}