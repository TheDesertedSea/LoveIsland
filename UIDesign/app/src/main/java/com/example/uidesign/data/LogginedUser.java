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

    private int confession_MaxID = 0;
    private int discussion_MaxID = 0;

    public int getUid() {
        return uid;
    }

    public String getNickName(){
        return nickName;
    }

    public String getToken(){ return token;}

    public int getConfession_MaxID() {
        return this.confession_MaxID;
    }

    public int getDiscussion_MaxID() {
        return this.discussion_MaxID;
    }

    public void setUid(int e)
    {
        uid=e;
    }

    public void setNickName(String e)
    {
        nickName=e;
    }

    public void setToken(String e){token=e;}

    public void setConfession_MaxID(int confession_MaxID) {
        this.confession_MaxID = confession_MaxID;
    }

    public void setDiscussion_MaxID(int discussion_MaxID) {
        this.discussion_MaxID = discussion_MaxID;
    }
}
