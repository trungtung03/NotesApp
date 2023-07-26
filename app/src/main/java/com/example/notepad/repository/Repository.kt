package com.example.notepad.repository

import android.content.Context
import com.example.notepad.local.AppDatabase
import com.example.notepad.model.model.Collect
import com.example.notepad.model.model.Money
import com.example.quanlychitieu.model.Spending


class Repository(context: Context) {
    private val databaseLocal: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }


    suspend fun addMoney(money: Money) = databaseLocal.getDatabaseDao().insertAll(money)
    suspend fun setSpending(spending: Spending) = databaseLocal.getSpendingDao().insertAll(spending)
    suspend fun setCollect(collect: Collect) = databaseLocal.getCollectDao().insertAll(collect)

    fun getListCollect(): List<Collect> = databaseLocal.getCollectDao().getAll()
    fun getMoney(): List<Money> = databaseLocal.getDatabaseDao().getAll()
    fun getListSpending(): List<Spending> = databaseLocal.getSpendingDao().getAll()

}