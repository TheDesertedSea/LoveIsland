package com.example.loveislandapp.ui.signup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.loveislandapp.InputChecker.PasswordChecker;
import com.example.loveislandapp.InputChecker.UsernameChecker;
import com.example.loveislandapp.R;
import com.example.loveislandapp.databinding.ActivitySignupBinding;
import com.example.loveislandapp.http.SignupHttp;

public class SignupActivity extends AppCompatActivity {

    private ActivitySignupBinding binding;
    private Context context;
    private SignupHttp signupHttp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context=this;

        //视图绑定
        binding=ActivitySignupBinding.inflate(getLayoutInflater());
        View rootView=binding.getRoot();
        setContentView(rootView);

        signupHttp=new SignupHttp();

        //格式检查器
        UsernameChecker usernameChecker=new UsernameChecker();
        final UsernameChecker.CheckResult[] usernameCheckResult = new UsernameChecker.CheckResult[1];
        usernameCheckResult[0]=usernameChecker.getInitialCheckReuslt();
        PasswordChecker passwordChecker=new PasswordChecker();
        final PasswordChecker.CheckResult[] passwordCheckResult = new PasswordChecker.CheckResult[1];
        passwordCheckResult[0]=passwordChecker.getInitialCheckReuslt();

        binding.NewUsernameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                usernameCheckResult[0]=usernameChecker.isUserNameValid(binding.NewUsernameInput.getText().toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.NewUsernameInput.setError(usernameCheckResult[0].errorInfo);
                    }
                });
            }
        });

        binding.NewPasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordCheckResult[0]=passwordChecker.isPasswordValid(binding.NewPasswordInput.getText().toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.NewPasswordInput.setError(passwordCheckResult[0].errorInfo);
                    }
                });
            }
        });

        binding.NewPasswordInputRepeat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!binding.NewPasswordInput.getText().toString().equals(binding.NewPasswordInputRepeat.getText().toString()))
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.NewPasswordInputRepeat.setError("两次输入密码不一致");
                        }
                    });
                }else
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.NewPasswordInputRepeat.setError(null);
                        }
                    });
                }
            }
        });

        //男生、女生只能从中选一个
        binding.CheckMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    binding.CheckFemale.setVisibility(View.INVISIBLE);
                }else
                {
                    binding.CheckFemale.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.CheckFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    binding.CheckMale.setVisibility(View.INVISIBLE);
                }else
                {
                    binding.CheckMale.setVisibility(View.VISIBLE);
                }
            }
        });

        binding.SignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(usernameCheckResult[0].valid&&passwordCheckResult[0].valid
                        && binding.NewPasswordInput.getText().toString()
                        .equals(binding.NewPasswordInputRepeat.getText().toString())
                        &&(binding.CheckMale.isChecked()||binding.CheckFemale.isChecked()))
                        )
                {
                    return;
                }

                //使按钮不可见，防止再次登录或进入注册活动
                binding.SignupButton.setVisibility(View.INVISIBLE);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SignupHttp.SignupResult signupResult=signupHttp.signup(
                                binding.NewUsernameInput.getText().toString(),
                                binding.NewPasswordInput.getText().toString(),
                                binding.NewNickname.getText().toString(),
                                binding.CheckMale.isChecked(),
                                binding.SchoolInput.getText().toString()
                        );
                        if(signupResult.success)
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    finish(); //返回登录活动
                                }
                            });
                        }else
                        {
                            if(signupResult.nickNameUsed)
                            {
                                runOnUiThread(
                                        new Runnable(){
                                            @Override
                                            public void run() {
                                                Toast.makeText(context,"该昵称已被使用",Toast.LENGTH_SHORT)
                                                        .show();
                                                binding.SignupButton.setVisibility(View.VISIBLE);
                                            }
                                        }
                                );
                            }else if(signupResult.signuped)
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,"该邮箱地址已被注册",Toast.LENGTH_SHORT)
                                                .show();
                                        binding.SignupButton.setVisibility(View.VISIBLE);
                                    }
                                });
                            }else if(signupResult.exception)
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(context,"注册失败",Toast.LENGTH_SHORT)
                                                .show();
                                        binding.SignupButton.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    }
                }).start();
            }
        });
    }
}