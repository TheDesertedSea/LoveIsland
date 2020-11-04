package com.example.loveislandapp.ui.login;

public class PasswordChecker {
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

    public CheckResult isPasswordValid(String password) {
        if(password != null && password.trim().length() > 5) {
            return new CheckResult(true, "");
        }else
        {
            return new CheckResult(false,"密码长度小于6");
        }
    }
}
