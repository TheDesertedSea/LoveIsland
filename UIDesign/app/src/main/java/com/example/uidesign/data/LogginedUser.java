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
    private String uid;
    private String nickName;

    public String getUid() {
        return uid;
    }

    public String getNickName(){
        return nickName;
    }

    public void setUid(String e)
    {
        uid=e;
    }

    public void setNickName(String e)
    {
        nickName=e;
    }
}
