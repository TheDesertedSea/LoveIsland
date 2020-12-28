package com.example.uidesign.data.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity()
public class Entity_Like {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "from")
    public int from;

    @ColumnInfo(name = "to")
    public int to;

    @ColumnInfo(name = "fromName")
    public String fromName;

    @ColumnInfo(name = "date")
    public long date;

}
