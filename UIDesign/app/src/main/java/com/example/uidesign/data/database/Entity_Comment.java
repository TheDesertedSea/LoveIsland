package com.example.uidesign.data.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"from","to"})
public class Entity_Comment {
    public int from;
    public int to;

    @ColumnInfo(name = "content")
    public String content;

    @ColumnInfo(name = "date")
    public long date;
}
