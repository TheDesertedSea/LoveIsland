package com.example.uidesign.ui.login;

import androidx.appcompat.app.ActionBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.uidesign.data.CachedLoginData;
import com.example.uidesign.net.NetLogin;
import com.example.uidesign.tool.InputFormatCheck;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.ui.MainActivity;
import com.example.uidesign.databinding.ActivityLoginBinding;
import com.example.uidesign.ui.entry.EntryActivity;
import com.example.uidesign.ui.sign_up.SignUpActivity;
import com.royrodriguez.transitionbutton.TransitionButton;

public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding binding;
    private final Activity thisActivity=this;
    private final Context thisContext=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.LoginButtonLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.LoginButtonLoginActivity.startAnimation();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NetLogin netLogin=new NetLogin();
                        int result=netLogin.login(binding.usernameInputText.getText().toString(),
                                binding.passwordInputText.getText().toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CachedLoginData cachedLoginData=new CachedLoginData(thisContext);
                                switch(result)
                                {
                                    case NetLogin.OK:
                                        cachedLoginData.saveCachedLoginData(binding.usernameInputText.getText().toString(),
                                                binding.passwordInputText.getText().toString());
                                        binding.LoginButtonLoginActivity.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                            @Override
                                            public void onAnimationStopEnd() {
                                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                startActivity(intent);
                                                thisActivity.finish();
                                            }
                                        });
                                        break;
                                    case NetLogin.DUPLICATE_LOGIN:
                                        cachedLoginData.saveCachedLoginData(binding.usernameInputText.getText().toString(),
                                                binding.passwordInputText.getText().toString());
                                        binding.LoginButtonLoginActivity.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                            @Override
                                            public void onAnimationStopEnd() {
                                                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                                startActivity(intent);
                                                thisActivity.finish();
                                            }
                                        });
                                        Toast.makeText(thisContext,NetLogin.ERROR_DUPLICATE_LOGIN,Toast.LENGTH_SHORT).show();
                                        break;
                                    case NetLogin.INFO_WRONG:
                                        cachedLoginData.saveCachedLoginData("",
                                                "");
                                        binding.LoginButtonLoginActivity.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                                        Toast.makeText(thisContext,NetLogin.ERROR_INFO_WRONG,Toast.LENGTH_SHORT).show();
                                        break;
                                    case NetLogin.OTHER_FAIL:
                                        cachedLoginData.saveCachedLoginData("",
                                                "");
                                        binding.LoginButtonLoginActivity.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                                        Toast.makeText(thisContext,NetLogin.ERROR_OTHER_FAIL,Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });

                    }
                }).start();
            }
        });

        binding.passwordInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                InputFormatCheck inputFormatCheck=new InputFormatCheck();
                int result=inputFormatCheck.checkPassword(binding.passwordInputText.getText().toString());
                switch(result)
                {
                    case InputFormatCheck.OK:
                        return;
                    case InputFormatCheck.PWD_MAX_LENGTH:
                        binding.usernameInputText.setError(InputFormatCheck.ERROR_PWD_OVER_LENGTH);
                        break;
                    case InputFormatCheck.PWD_BELOW_LENGTH:
                        binding.usernameInputText.setError(InputFormatCheck.ERROR_PWD_BELOW_LENGTH);
                        break;
                    case InputFormatCheck.PWD_FORMAT_WRONG:
                        binding.usernameInputText.setError(InputFormatCheck.ERROR_PWD_FORMAT_WRONG);
                }

            }
        });

        binding.usernameInputText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                InputFormatCheck inputFormatCheck=new InputFormatCheck();
                int result=inputFormatCheck.checkUsername(binding.usernameInputText.getText().toString());
                switch(result)
                {
                    case InputFormatCheck.OK:
                        return;
                    case InputFormatCheck.USERNAME_FORMAT_WRONG:
                        binding.passwordInputText.setError(InputFormatCheck.ERROR_USERNAME_FORMAT_WRONG);
                }
            }
        });

        binding.SignUpTextViewLoginActivity.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        binding.SignUpTextViewLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisContext, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}