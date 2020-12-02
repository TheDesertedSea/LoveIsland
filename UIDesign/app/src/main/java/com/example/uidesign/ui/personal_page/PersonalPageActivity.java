package com.example.uidesign.ui.personal_page;


import android.os.Bundle;

import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityPersonalPageBinding;

public class PersonalPageActivity extends BaseActivity {

    private ActivityPersonalPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPersonalPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}