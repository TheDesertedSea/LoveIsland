package com.example.uidesign.ui.comment_to_me;

import android.os.Bundle;

import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityCommentToMeBinding;

public class CommentToMeActivity extends BaseActivity {

    private ActivityCommentToMeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCommentToMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}