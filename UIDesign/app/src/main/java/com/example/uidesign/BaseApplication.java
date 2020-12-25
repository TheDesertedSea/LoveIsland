package com.example.uidesign;

import android.app.Application;

import com.example.uidesign.data.database.DatabaseManager;
import com.example.uidesign.net.UserSocketManager;


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
        UserSocketManager.getInstance().disconnect();
    }
}
