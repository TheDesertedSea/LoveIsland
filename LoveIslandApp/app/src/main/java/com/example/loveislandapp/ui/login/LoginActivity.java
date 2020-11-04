package com.example.loveislandapp.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.loveislandapp.R;
import com.example.loveislandapp.databinding.ActivityLoginBinding;
import com.example.loveislandapp.http.LoginHttp;
import com.example.loveislandapp.ui.personalCenter.PersonalCenterActivity;
import com.example.loveislandapp.ui.signup.SignupActivity;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context=this;

        //视图绑定
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        View rootView=binding.getRoot();
        setContentView(rootView);

        //格式检查器
        UsernameChecker usernameChecker=new UsernameChecker();
        final UsernameChecker.CheckResult[] usernameCheckResult = new UsernameChecker.CheckResult[1];
        PasswordChecker passwordChecker=new PasswordChecker();
        final PasswordChecker.CheckResult[] passwordCheckResult = new PasswordChecker.CheckResult[1];

        //绑定文本输入监听器
        binding.UsernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                usernameCheckResult[0] =usernameChecker.isUserNameValid(binding.UsernameInput.getText().toString());
                if(!usernameCheckResult[0].valid)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.UsernameInput.setError(usernameCheckResult[0].errorInfo);
                        }
                    });
                }
            }
        });

        binding.PasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordCheckResult[0]=passwordChecker.isPasswordValid(binding.PasswordInput.getText().toString());
                if(!passwordCheckResult[0].valid)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.UsernameInput.setError(passwordCheckResult[0].errorInfo);
                        }
                    });
                }
            }
        });

        //绑定按钮监听器
        binding.LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(usernameCheckResult[0].valid&&passwordCheckResult[0].valid))
                {
                    return;
                }
                //使按钮不可见，防止再次登录或进入注册活动
                binding.LoginButton.setVisibility(View.INVISIBLE);
                binding.SignupButton.setVisibility(View.INVISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LoginHttp.LoginResult loginResult=new LoginHttp()
                                .Login(binding.UsernameInput.getText().toString(),
                                        binding.PasswordInput.getText().toString());
                        //登录成功
                        if(loginResult.success)
                        {
                            if(loginResult.logined)
                            {
                                runOnUiThread(
                                        new Runnable(){
                                            @Override
                                            public void run() {
                                                Toast.makeText(context,"该账号已在另一台设备上登录，已强制下线",Toast.LENGTH_LONG)
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
                                    finish();
                                }
                            });
                        }else//登录失败
                        {
                            if(loginResult.exception)
                            {
                                runOnUiThread(
                                        new Runnable(){
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),"登录失败",Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        }
                                );
                            }else
                            {
                                runOnUiThread(
                                        new Runnable(){
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),"用户名或密码错误",Toast.LENGTH_SHORT)
                                                        .show();
                                            }
                                        }
                                );
                            }
                        }
                    }
                }).start();
            }
        });

        binding.SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, SignupActivity.class);
                startActivity(intent);
            }
        });


    }
}