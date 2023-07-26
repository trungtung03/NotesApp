package com.example.quanlychitieu.ui.history_revenue_expenditure.spending_money

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notepad.repository.Repository
import com.example.quanlychitieu.model.Spending
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class SpendingMoneyViewModel(context: Context) : ViewModel() {
    private val repository: Repository = Repository(context)
    private val _listSpeding: MutableLiveData<List<Spending>> = MutableLiveData(arrayListOf())
    val listSpending: LiveData<List<Spending>> = _listSpeding
    fun getListSpending() {
        viewModelScope.launch {
            val getData = async {
                repository.getListSpending().let {
                    if (it?.isNotEmpty() == true) {
                        _listSpeding.postValue(it)
                    } else _listSpeding.postValue(listOf())
                }
            }
            awaitAll(getData)

        }

    }

}

class SpendingMoneyViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpendingMoneyViewModel::class.java)) {
            return SpendingMoneyViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}