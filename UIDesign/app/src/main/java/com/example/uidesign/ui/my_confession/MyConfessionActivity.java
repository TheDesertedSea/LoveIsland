package com.example.uidesign.ui.my_confession;

import android.os.Bundle;

import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityMyConfessionBinding;

public class MyConfessionActivity extends BaseActivity {

    private ActivityMyConfessionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMyConfessionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}