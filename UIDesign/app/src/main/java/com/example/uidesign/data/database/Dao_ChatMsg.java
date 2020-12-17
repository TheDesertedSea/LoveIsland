package com.example.uidesign.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface Dao_ChatMsg {
    @Insert
    void insertAll(Entity_ChatMsg... entity_chatMsgs);

    @Query("SELECT * FROM ENTITY_CHATMSG WHERE (`from` LIKE:myUid AND `to` LIKE:otherUid)" +
            " OR (`from` LIKE:otherUid AND `to` LIKE:myUid)")
    List<Entity_ChatMsg> getChatMsgLog(int myUid,int otherUid);

}
