package com.example.loveislandapp.data;

//已登录用户信息
public class LoginedUser {
    private static volatile LoginedUser instance;

    private String username;
    private String nickname;

    public static LoginedUser getInstance()
    {
        if(instance==null)
        {
            instance=new LoginedUser();
        }
        return instance;
    }

    public void setUsername(String u)
    {
        username=u;
    }

    public void setNickname(String n)
    {
        nickname=n;
    }

    public String getUsername()
    {
        return username;
    }

    public String getNickname()
    {
        return nickname;
    }
}
