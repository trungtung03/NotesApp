package com.example.quanlychitieu.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.notepad.model.model.Money

@Dao
interface MoneyDao {
    @Query("SELECT * FROM money")
    fun getAll(): List<Money>

    @Insert
    suspend fun insertAll(vararg users: Money)

    @Delete
    suspend fun delete(user: Money)
}