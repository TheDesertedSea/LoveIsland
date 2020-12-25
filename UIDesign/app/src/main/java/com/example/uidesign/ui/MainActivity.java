package com.example.uidesign.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.example.uidesign.BaseApplication;
import com.example.uidesign.R;
import com.example.uidesign.data.UserSocketManager;
import com.example.uidesign.databinding.ActivityMainBinding;

import androidx.appcompat.app.ActionBar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends BaseActivity {

    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ActionBar actionBar=getSupportActionBar();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_confession, R.id.navigation_discussion,R.id.navigation_lucky_card,
                R.id.navigation_notifications,R.id.navigation_personal_center)
                .build();
        NavController navController = Navigation.findNavController(this,R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        UserSocketManager.getInstance().connect("successContent.host",43,(BaseApplication) getApplication());

    }

}