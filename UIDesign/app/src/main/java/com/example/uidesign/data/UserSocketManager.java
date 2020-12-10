package com.example.uidesign.data;

import java.io.IOException;

import io.socket.client.IO;
import io.socket.client.Socket;


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
            socket= IO.socket("http://"+host+String.valueOf(port));
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public Socket getSocket(){return socket;}
}
