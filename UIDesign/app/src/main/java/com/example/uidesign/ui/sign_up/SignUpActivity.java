package com.example.uidesign.ui.sign_up;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.uidesign.net.NetSignUp;
import com.example.uidesign.tool.InputFormatCheck;
import com.example.uidesign.ui.BaseActivity;
import com.example.uidesign.databinding.ActivitySignUpBinding;

public class SignUpActivity extends BaseActivity {

    private ActivitySignUpBinding binding;
    private final Activity thisActivity=this;
    private final Context thisContext=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                InputFormatCheck inputFormatCheck=new InputFormatCheck();
                int result=inputFormatCheck.checkUsername(binding.emailInput.getText().toString());
                switch(result)
                {
                    case InputFormatCheck.OK:
                        return;
                    case InputFormatCheck.USERNAME_FORMAT_WRONG:
                        binding.emailInput.setError(InputFormatCheck.ERROR_USERNAME_FORMAT_WRONG);
                }

            }
        });

        binding.passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                InputFormatCheck inputFormatCheck=new InputFormatCheck();
                int result=inputFormatCheck.checkPassword(binding.passwordInput.getText().toString());
                switch(result)
                {
                    case InputFormatCheck.OK:
                        return;
                    case InputFormatCheck.PWD_MAX_LENGTH:
                        binding.passwordInput.setError(InputFormatCheck.ERROR_PWD_OVER_LENGTH);
                        break;
                    case InputFormatCheck.PWD_BELOW_LENGTH:
                        binding.passwordInput.setError(InputFormatCheck.ERROR_PWD_BELOW_LENGTH);
                        break;
                    case InputFormatCheck.PWD_FORMAT_WRONG:
                        binding.passwordInput.setError(InputFormatCheck.ERROR_PWD_FORMAT_WRONG);
                }
            }
        });

        binding.passwordTwiceInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!binding.passwordTwiceInput.getText().toString().equals(binding.passwordInput
                .getText().toString()))
                {
                    binding.passwordTwiceInput.setError("两次密码输入不一致");
                }
            }
        });

        binding.CheckMale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.CheckFemale.setEnabled(!isChecked);
            }
        });

        binding.CheckFemale.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                binding.CheckMale.setEnabled(!isChecked);
            }
        });

        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.signUpButton.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NetSignUp netSignUp=new NetSignUp();
                        int result=netSignUp.signUp(binding.emailInput.getText().toString(),
                                binding.passwordInput.getText().toString(),
                                binding.nickNameInput.getText().toString(),
                                binding.CheckFemale.isChecked(),binding.schoolInput.getText().toString());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (result)
                                {
                                    case NetSignUp.OK:
                                        Toast.makeText(thisContext,NetSignUp.OK_INFO,Toast.LENGTH_SHORT).show();
                                        thisActivity.finish();
                                        return;
                                    case NetSignUp.DUPLICATE_SIGN_UP:
                                        Toast.makeText(thisContext,NetSignUp.DUPLICATE_SIGN_UP_INFO,Toast.LENGTH_SHORT).show();
                                        binding.signUpButton.setEnabled(true);
                                    case NetSignUp.OTHER_FAIL:
                                        Toast.makeText(thisContext,NetSignUp.OTHER_FAIL_INFO,Toast.LENGTH_SHORT).show();
                                        binding.signUpButton.setEnabled(true);
                                }
                            }
                        });

                    }
                }).start();
            }
        });

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thisActivity.finish();
            }
        });
    }
}