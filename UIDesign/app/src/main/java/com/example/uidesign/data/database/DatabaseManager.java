package com.example.uidesign.data.database;

import android.content.Context;

import androidx.room.Room;

public class DatabaseManager {
    private static boolean bInitial=false;
    private static AppDatabase appDatabase;
    public static AppDatabase getAppDatabase(){
        if(bInitial)
        {
            return appDatabase;
        }else
        {
            return null;
        }
    }
    public static void initialDatabase(Context context)
    {
        appDatabase= Room.databaseBuilder(context, AppDatabase.class, "appdatabase").build();
        bInitial=true;
    }

}
