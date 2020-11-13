package com.example.loveislandapp.ui.entry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.loveislandapp.R;
import com.example.loveislandapp.data.CachedLoginInfo;
import com.example.loveislandapp.data.LoginedUser;
import com.example.loveislandapp.http.LoginHttp;
import com.example.loveislandapp.ui.login.LoginActivity;
import com.example.loveislandapp.ui.personalCenter.PersonalCenterActivity;

//app进入界面进行自动登录
public class EntryActivity extends AppCompatActivity {

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context=this;

        setContentView(R.layout.activity_entry);

        //获取登录信息缓存
        CachedLoginInfo cachedLoginInfo=new CachedLoginInfo(getApplicationContext());

        new Thread(new Runnable() {
            @Override
            public void run() {
                LoginHttp loginHttp=new LoginHttp();
                LoginHttp.LoginResult loginResult=loginHttp.Login(cachedLoginInfo.getUsername(),cachedLoginInfo.getPassword());

                if(loginResult.success)  //登录成功
                {
                    //保存已登录用户信息
                    LoginedUser loginedUser=LoginedUser.getInstance();
                    loginedUser.setUsername(cachedLoginInfo.getUsername());

                    if(loginResult.logined)  //重复登录
                    {
                        runOnUiThread(
                                new Runnable(){
                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),"该账号已在另一台设备上登录，已强制下线",Toast.LENGTH_LONG)
                                        .show();
                                    }
                                }
                        );
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent=new Intent(getApplicationContext(), PersonalCenterActivity.class);
                            startActivity(intent);
                            //结束进入活动生命周期
                            finish();
                        }
                    });
                }else  //自动登录失败，进入手动登录
                {
                    cachedLoginInfo.clear();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent=new Intent(context, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        }).start();
    }
}