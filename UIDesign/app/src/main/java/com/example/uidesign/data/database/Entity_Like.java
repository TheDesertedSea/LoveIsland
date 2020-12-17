package com.example.uidesign.data.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"from","to"})
public class Entity_Like {
    public int from;
    public int to;

    @ColumnInfo(name = "fromName")
    public String fromName;

    @ColumnInfo(name = "date")
    public long date;

}
