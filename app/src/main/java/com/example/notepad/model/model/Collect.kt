package com.example.notepad.model.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Collect(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "money") val money: Int?,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "content") var content: String?,
    @ColumnInfo(name = "date") val date: String?
)