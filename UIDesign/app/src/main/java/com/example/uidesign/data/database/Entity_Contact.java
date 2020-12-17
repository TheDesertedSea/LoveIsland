package com.example.uidesign.data.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"user_uid","other_uid"})
public class Entity_Contact {
    public int user_uid;

    public int other_uid;

    @ColumnInfo(name="latest_content")
    public String latest_content;
}
