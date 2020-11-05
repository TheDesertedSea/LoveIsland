package com.example.loveislandapp.ui.personalCenter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import com.example.loveislandapp.R;
import com.example.loveislandapp.databinding.ActivityPersonalCenterBinding;
import com.example.loveislandapp.http.PersonalInfoHttp;

public class PersonalCenterActivity extends AppCompatActivity {

    private ActivityPersonalCenterBinding binding;
    private Context context;
    private PersonalInfoHttp personalInfoHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
    }
}