package com.example.uidesign.net;

public class NetLogin {
    private static final String format=":30010/login/login?mailbox=****&password=****&time=****";
    private static final String serverIp="";
    private static final String baseUrl=serverIp+":30010/login/login";

    //结果码
    public static final int OK=0;
    public static final int DUPLICATE_LOGIN=1;
    public static final int INFO_WRONG=2;
    public static final int OTHER_FAIL=3;

    //报错信息
    public static final String ERROR_DUPLICATE_LOGIN="重复登录，已将之前登录设备下线";
    public static final String ERROR_INFO_WRONG="用户名或密码错误";
    public static final String ERROR_OTHER_FAIL="登录失败";

    public int login(String username,String password)
    {

        return OK;
    }
}
