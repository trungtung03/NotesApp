package com.example.notepad.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.quanlychitieu.model.Spending

@Dao
interface SpendingDao {
    @Query("SELECT * FROM spending")
    fun getAll():List<Spending>
    @Insert
    suspend fun insertAll(vararg users: Spending)

    @Delete
    suspend  fun delete(user: Spending)
}