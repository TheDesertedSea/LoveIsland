package com.example.uidesign.ui.item_edit;


import android.os.Bundle;

import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityItemEditBinding;

public class ItemEditActivity extends BaseActivity {

    private ActivityItemEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityItemEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}