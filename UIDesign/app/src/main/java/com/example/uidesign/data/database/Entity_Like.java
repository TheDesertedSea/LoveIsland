package com.example.uidesign.data.database;

import androidx.room.Entity;

@Entity(primaryKeys = {"from","to"})
public class Entity_Like {
    public int from;
    public int to;

}
