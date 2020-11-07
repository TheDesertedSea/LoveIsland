package com.example.loveislandapp.ui.personalCenter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.loveislandapp.R;
import com.example.loveislandapp.data.LoginedUser;
import com.example.loveislandapp.databinding.ActivityLoginBinding;
import com.example.loveislandapp.databinding.ActivityPersonalCenterBinding;
import com.example.loveislandapp.http.LoginHttp;
import com.example.loveislandapp.http.PersonalInfoHttp;
import com.example.loveislandapp.ui.personalCenter.edit.EditPersonalInfoActivity;

import java.io.ByteArrayOutputStream;

public class PersonalCenterActivity extends AppCompatActivity {

    private static final String baseUrl
            = "http://192.168.1.102:30010/usericon/";
    private LoginedUser loginedUser=LoginedUser.getInstance();

    private ActivityPersonalCenterBinding binding;
    private Context context;
    private PersonalInfoHttp personalInfoHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context=this;

        //视图绑定
        binding= ActivityPersonalCenterBinding.inflate(getLayoutInflater());
        View rootView=binding.getRoot();
        setContentView(rootView);

        //需信息加载完毕才能进行编辑
        binding.EditInfoButton.setEnabled(false);

        personalInfoHttp=new PersonalInfoHttp();

        //加载用户头像
        String iconUrl=baseUrl+loginedUser.getUid();

        Glide.with(context)
                .load(iconUrl)
                .into(binding.UserIcon);


        new Thread(new Runnable() {
            @Override
            public void run() {
                PersonalInfoHttp.PersonalInfoResult personalInfoResult= personalInfoHttp.getPersonalInfo(loginedUser.getUid());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(personalInfoResult.sex)
                        {
                            Glide.with(context)
                                    .load(R.drawable.male)
                                    .into(binding.MaleFemaleImage);
                        }else
                        {
                            Glide.with(context)
                                    .load(R.drawable.female)
                                    .into(binding.MaleFemaleImage);
                        }
                        binding.NickNamePersonalCenter.setText(loginedUser.getNickname());
                        binding.SchoolPersonalCenter.setText(personalInfoResult.school);
                        binding.Introduction.setText(personalInfoResult.introduction);
                        binding.EditInfoButton.setEnabled(true);
                    }
                });

            }
        }).start();

        //进行编辑将已加载个人信息传递过去
        binding.EditInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, EditPersonalInfoActivity.class);
                Drawable iconDrawable=binding.UserIcon.getDrawable();
                Bitmap bitmap=drawableToBitmap(iconDrawable);
                byte[] bytes = bitmap2Bytes(bitmap);
                intent.putExtra("icon",bytes);
                intent.putExtra("nickname",binding.NickNamePersonalCenter.getText().toString());
                intent.putExtra("school",binding.SchoolPersonalCenter.getText().toString());
                intent.putExtra("introduction",binding.Introduction.getText().toString());
                startActivity(intent);
            }
        });

    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if(drawable==null)
        {
            return null;
        }
        Bitmap bitmap;
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        System.out.println("Drawable转Bitmap");
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        bitmap = Bitmap.createBitmap(w,h,config);
        //注意，下面三行代码要用到，否在在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    private byte[] bitmap2Bytes(Bitmap bm){
        if(bm==null)
        {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }
}