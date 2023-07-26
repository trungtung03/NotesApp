package com.example.notepad.ui.add.add_spending

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notepad.model.model.Money
import com.example.quanlychitieu.model.Spending
import com.example.notepad.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class AddSpendingViewModel(context: Context) : ViewModel() {
    private val repository: Repository = Repository(context)

    private val _money: MutableLiveData<Money> = MutableLiveData()
    val money: LiveData<Money> = _money

    fun getMoney() {
        viewModelScope.launch {
            val getMoney = async {
                repository.getMoney().let {
                    if (it.isNotEmpty()) {
                        _money.postValue(it.last())
                    } else {
                        _money.postValue(Money(money = 0))
                    }
                }
            }
            getMoney.await()
        }
    }

    fun setSpending(spending: Spending) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setSpending(spending)
        }
    }

    fun setMoney(money: Money, onBackInvoked: () -> Unit) {
        viewModelScope.launch {
            val add = async {
                repository.addMoney(money)
            }
            awaitAll(add)
            onBackInvoked.invoke()
        }
    }


}


class AddSpendingViewModelFactory(val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddSpendingViewModel::class.java)) {
            return AddSpendingViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}