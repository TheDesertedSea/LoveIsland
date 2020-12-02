package com.example.uidesign.ui.thumb_to_me;

import android.os.Bundle;

import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityThumbToMeBinding;

public class ThumbToMeActivity extends BaseActivity {

    private ActivityThumbToMeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityThumbToMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}