package com.example.uidesign.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface Dao_Contact {
    @Insert
    void insertAll(Entity_Contact... entity_contacts);

    @Query("SELECT * FROM Entity_Contact WHERE user_uid LIKE:queryUid")
    List<Entity_Comment> getContacts(int queryUid);

    @Update
    void setLatestContent(List<Entity_Contact> entity_contacts);
}
