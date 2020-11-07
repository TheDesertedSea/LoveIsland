package com.example.loveislandapp.ui.personalCenter.edit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.loveislandapp.R;
import com.example.loveislandapp.data.LoginedUser;
import com.example.loveislandapp.databinding.ActivityEditPersonalInfoBinding;
import com.example.loveislandapp.databinding.ActivityPersonalCenterBinding;
import com.example.loveislandapp.http.PersonalInfoHttp;

public class EditPersonalInfoActivity extends AppCompatActivity {

    public static int CHOOSE_PHOTO=2;

    private static final String baseUrl
            = "http://192.168.1.102:30010/usericon/";
    private LoginedUser loginedUser=LoginedUser.getInstance();

    private ActivityEditPersonalInfoBinding binding;
    private Context context;
    private PersonalInfoHttp personalInfoHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context=this;
        binding= ActivityEditPersonalInfoBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        personalInfoHttp=new PersonalInfoHttp();

        //将个人中心的数据拷贝过来
        Intent intent=getIntent();
        byte[] appIcons = intent.getByteArrayExtra("appIcon");
        if(appIcons!=null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(appIcons, 0, appIcons.length);
            binding.UserIcon.setImageBitmap(bitmap);
        }

        binding.EditTextNickname.setText(intent.getStringExtra("nickname"));
        binding.EditTextSchool.setText(intent.getStringExtra("school"));
        binding.EditTextIntroduction.setText(intent.getStringExtra("introduction"));

        binding.EditIconButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(EditPersonalInfoActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    ,1);
                }else {
                    openAlbum();
                }
            }
        });

        binding.SaveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PersonalInfoHttp.EditResult editResult=personalInfoHttp
                                .setPersonalInfo(
                                        binding.EditTextNickname.getText().toString(),
                                binding.EditTextSchool.getText().toString(),
                                binding.EditTextIntroduction.getText().toString());

                        if(editResult.success)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,"编辑成功!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else if(editResult.nickNameUsed)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,"昵称已被使用，编辑失败",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context,"编辑失败",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

            }
        });


    }

    private void openAlbum()
    {
        Intent intent=new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    openAlbum();
                }else
                {
                    Toast.makeText(context,"你拒绝了访问相册权限",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}