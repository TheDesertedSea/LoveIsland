package com.example.uidesign.ui.entry;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;

import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivityEntryBinding;
import com.romainpiel.shimmer.Shimmer;

public class EntryActivity extends BaseActivity {

    private ActivityEntryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar=getSupportActionBar();


        Shimmer shimmer = new Shimmer();
        shimmer.start(binding.TitleEntryActivity);

    }
}