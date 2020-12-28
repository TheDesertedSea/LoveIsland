package com.example.uidesign.data.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class Entity_Contact {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name="user_uid")
    public int user_uid;

    @ColumnInfo(name="other_uid")
    public int other_uid;

    @ColumnInfo(name="other_nick_name")
    public String other_nick_name;

    @ColumnInfo(name="latest_content")
    public String latest_content;

    @ColumnInfo(name="date")
    public long date;
}
