package com.example.notepad.model.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Money(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "money") val money: Int?
)