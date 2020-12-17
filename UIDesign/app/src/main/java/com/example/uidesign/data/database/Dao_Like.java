package com.example.uidesign.data.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface Dao_Like {
    @Insert
    void insertAll(Entity_Like... entity_likes);

    @Query("SELECT * FROM Entity_Like WHERE `to` LIKE:queryUid")
    List<Entity_Like> getLikes(int queryUid);

}
