package com.example.uidesign.ui;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

//活动收集器
public class ActivityCollector {
    private static ActivityCollector instance;  //使用单例模式

    public ActivityCollector()
    {
        activities=new ArrayList<>();
    }

    public static ActivityCollector getInstance()
    {
        if(instance==null)
        {
            instance=new ActivityCollector();
        }
        return instance;
    }

    private List<Activity> activities;  //活动列表

    public void add(Activity activity)
    {
        activities.add(activity);
    }

    public void remove(Activity activity)
    {
        activities.remove(activity);
    }

    public void finishAll()
    {
        for(Activity e:activities)
        {
            if(!e.isFinishing())
            {
                e.finish();
            }
        }
    }

}
