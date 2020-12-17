package com.example.uidesign.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface Dao_Comment {
    @Insert
    void insertAll(Entity_Comment... entity_comments);

    @Query("SELECT * FROM Entity_Comment WHERE `to` LIKE:queryUid")
    List<Entity_Comment> getComments(int queryUid);

}
