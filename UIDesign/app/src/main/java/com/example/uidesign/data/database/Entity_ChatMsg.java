package com.example.uidesign.data.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(primaryKeys = {"from", "to"})
public class Entity_ChatMsg {

    public int from;


    public int to;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "date")
    public long date;
}
