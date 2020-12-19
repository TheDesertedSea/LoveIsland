package com.example.uidesign.net;

import com.example.uidesign.data.ColdBootItem;

import java.util.ArrayList;
import java.util.List;

public class NetColdBoot {
    private static final boolean DEBUG=true;



    public List<ColdBootItem> getColdBootItem(int uid)
    {
        if(DEBUG)
        {
            ColdBootItem coldBootItem=new ColdBootItem();
            coldBootItem.cid=0;
            coldBootItem.choiceName="米饭";
            List<ColdBootItem> result=new ArrayList<ColdBootItem>();
            for(int i=0;i<10;++i)
            {
                result.add(coldBootItem);
            }
            return result;
        }
        return null;
    }

    public boolean sendColdBootItem(int uid,List<Integer> list)
    {
        if(DEBUG)
        {
            return true;
        }
        return false;
    }

}
