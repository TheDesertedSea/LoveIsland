package com.example.uidesign.data;

import java.util.Date;

public class ChatMsg {

    public String getContent(){
        return content;
    }
    public Date getDate(){return date;}
    public int getFrom(){return from;}
    public int getTo(){return to;}

    private final String content;
    private final Date date;
    private final int from;
    private final int to;

    public ChatMsg(int from,int to,String content,Date date){
        this.from=from;
        this.to=to;
        this.content=content;
        this.date=date;
    }
}
