package com.example.loveislandapp.data;

//已登录用户信息
public class LoginedUser {
    private static volatile LoginedUser instance;

    private String username;
    private String nickname;
    private String uid;

    public static LoginedUser getInstance()
    {
        if(instance==null)
        {
            instance=new LoginedUser();
        }
        return instance;
    }

    public void setUsername(String username)
    {
        this.username=username;
    }

    public void setNickname(String nickname)
    {
        this.nickname=nickname;
    }

    public void setUid(String uid){this.uid=uid;}

    public String getUsername()
    {
        return username;
    }

    public String getNickname()
    {
        return nickname;
    }

    public String getUid(){return uid;}
}
