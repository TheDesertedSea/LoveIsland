package com.example.loveislandapp.InputChecker;

//密码输入格式检查器
public class PasswordChecker {
    public class CheckResult
    {
        public boolean valid;
        public String errorInfo;

        public CheckResult(boolean v,String ei)
        {
            valid=v;
            errorInfo=ei;
        }
    }

    public CheckResult isPasswordValid(String password) {
        if(password != null && password.trim().length() > 5) {
            return new CheckResult(true, null);
        }else
        {
            return new CheckResult(false,"密码长度小于6");
        }
    }

    //返回一个默认结果
    public CheckResult getInitialCheckReuslt()
    {
        return new CheckResult(false,"");
    }
}
