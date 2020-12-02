package com.example.uidesign.ui.item_detail;

import android.os.Bundle;

import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityItemDetailBinding;

public class ItemDetailActivity extends BaseActivity {

    private ActivityItemDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityItemDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}