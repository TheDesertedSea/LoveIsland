package com.example.uidesign.data.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Entity_ChatMsg {
    @PrimaryKey
    public int from;

    @PrimaryKey
    public int to;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "date")
    public long date;
}
