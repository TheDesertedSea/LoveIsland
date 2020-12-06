package com.example.uidesign.data;

import java.io.IOException;
import java.net.Socket;

public class UserSocketManager {
    private static UserSocketManager instance;
    public static UserSocketManager getInstance()
    {
        if(instance==null)
        {
            instance=new UserSocketManager();
        }
        return instance;
    }
    Socket socket;

    public void connect(String host,int port)
    {
        try
        {
            socket=new Socket(host,port);
        }catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public Socket getSocket(){return socket;}
}
