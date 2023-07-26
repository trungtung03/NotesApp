package com.example.quanlychitieu.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.notepad.model.model.Collect

@Dao
interface CollectDao {
    @Query("SELECT * FROM collect")
    fun getAll(): List<Collect>
    @Insert
    suspend fun insertAll(vararg collect: Collect)

    @Delete
    suspend  fun delete(collect: Collect)
}