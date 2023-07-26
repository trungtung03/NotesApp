package com.example.notepad.local
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.notepad.MainApp
import com.example.quanlychitieu.local.dao.CollectDao
import com.example.quanlychitieu.local.dao.MoneyDao
import com.example.notepad.local.dao.SpendingDao
import com.example.notepad.model.model.Collect
import com.example.notepad.model.model.Money
import com.example.quanlychitieu.model.Spending

@Database(
    entities = [Money::class, Spending::class, Collect::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getDatabaseDao(): MoneyDao
    abstract fun getSpendingDao(): SpendingDao
    abstract fun getCollectDao(): CollectDao

    companion object {
        private var instance: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance =
                    Room.databaseBuilder(
                        context, AppDatabase::class.java, "database-name"
                    ).allowMainThreadQueries().build()

            }
            return instance!!
        }
    }
}