package com.example.uidesign.data.database;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {Entity_ChatMsg.class,Entity_Comment.class,Entity_Like.class,Entity_Contact.class}, version = 6, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract Dao_ChatMsg dao_chatMsg();
    public abstract Dao_Comment dao_comment();
    public abstract Dao_Contact dao_contact();
    public abstract Dao_Like dao_like();
}
