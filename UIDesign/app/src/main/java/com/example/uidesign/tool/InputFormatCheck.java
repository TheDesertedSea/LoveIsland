package com.example.uidesign.tool;

public class InputFormatCheck {

    //检查结果码
    public static final int OK=0;
    public static final int PWD_OVER_LENGTH=11;
    public static final int PWD_BELOW_LENGTH=12;
    public static final int PWD_FORMAT_WRONG=13;
    public static final int USERNAME_FORMAT_WRONG=21;
    //报错信息
    public static final String ERROR_PWD_OVER_LENGTH="密码长度超过16";
    public static final String ERROR_PWD_BELOW_LENGTH="密码长度少于6";
    public static final String ERROR_PWD_FORMAT_WRONG="密码格式错误";
    public static final String ERROR_USERNAME_FORMAT_WRONG="用户名格式错误，使用电子邮件地址";
    //格式设置
    public static final int PWD_MAX_LENGTH=16;
    public static final int PWD_MIN_LENGTH=6;
    public int checkPassword(String pwd)
    {
        if(pwd.length()>PWD_MAX_LENGTH)
        {
            return PWD_OVER_LENGTH;
        }
        if(pwd.length()<PWD_MIN_LENGTH)
        {
            return PWD_BELOW_LENGTH;
        }
        return OK;
    }

    public int checkUsername(String username)
    {
        return OK;
    }

}
