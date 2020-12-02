package com.example.uidesign.ui.sign_up;

import android.os.Bundle;

import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivitySignUpBinding;

public class SignUpActivity extends BaseActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}