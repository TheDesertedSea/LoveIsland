package com.example.loveislandapp.ui.login;

import android.util.Patterns;

public class UsernameChecker {
    public class CheckResult
    {
        boolean valid;
        String errorInfo;
        public CheckResult(boolean v,String ei)
        {
            valid=v;
            errorInfo=ei;
        }
    }

    public CheckResult isUserNameValid(String username) {
        if (username == null) {
            return new CheckResult(false,"用户名不能为空");
        }
        if (Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            return new CheckResult(true,"");
        }else
        {
            return new CheckResult(false,"请输入正确的邮箱格式");
        }
    }
}
