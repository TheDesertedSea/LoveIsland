package com.example.uidesign.data;

public class LogginedUser {
    private static LogginedUser instance;
    public static LogginedUser getInstance()
    {
        if(instance==null)
        {
            instance=new LogginedUser();
        }
        return instance;
    }

    private LogginedUser(){}

    private int uid;
    private String nickName;
    private String token;

    public int getUid() {
        return uid;
    }

    public String getNickName(){
        return nickName;
    }

    public String getToken(){ return token;}

    public void setUid(int e)
    {
        uid=e;
    }

    public void setNickName(String e)
    {
        nickName=e;
    }

    public void setToken(String e){token=e;}
}
