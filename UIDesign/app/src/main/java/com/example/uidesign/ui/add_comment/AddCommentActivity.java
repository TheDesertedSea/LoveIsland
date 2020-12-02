package com.example.uidesign.ui.add_comment;

import android.os.Bundle;

import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityAddCommentBinding;

public class AddCommentActivity extends BaseActivity {

    private ActivityAddCommentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAddCommentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}