package com.example.uidesign;

import android.app.Application;

import com.example.uidesign.data.UserSocketManager;
import com.example.uidesign.data.database.DatabaseManager;

public class BaseApplication extends Application {
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
        if(UserSocketManager.getInstance().getSocket()!=null)
        {
            UserSocketManager.getInstance().getSocket().disconnect();
        }
    }
}
