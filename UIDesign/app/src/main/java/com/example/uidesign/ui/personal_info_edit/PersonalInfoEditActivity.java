package com.example.uidesign.ui.personal_info_edit;

import android.os.Bundle;

import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityPersonalInfoEditBinding;

public class PersonalInfoEditActivity extends BaseActivity {

    private ActivityPersonalInfoEditBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPersonalInfoEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}