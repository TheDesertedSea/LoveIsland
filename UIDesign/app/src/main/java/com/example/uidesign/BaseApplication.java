package com.example.uidesign;

import android.app.Application;

import com.example.uidesign.data.UserSocketManager;
import com.example.uidesign.data.database.DatabaseManager;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class BaseApplication extends Application {

    private Socket mSocket;

    public Socket getSocket() {
        return mSocket;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(DatabaseManager.getAppDatabase()==null)
        {
            DatabaseManager.initialDatabase(this);

        }
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
        if(DatabaseManager.getAppDatabase()!=null) {
            DatabaseManager.getAppDatabase().close();
        }
        if(mSocket!=null)
        {
            mSocket.disconnect();
        }
    }
}
