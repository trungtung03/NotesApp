package com.example.quanlychitieu.model

import android.icu.text.CaseMap.Title
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
data class Spending(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "money") val money: Int?,
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "content") var content : String?,
    @ColumnInfo(name = "date")val date: String?
)