package com.example.quanlychitieu.ui.cash_wallet

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notepad.model.model.Money
import com.example.notepad.repository.Repository
import com.example.quanlychitieu.model.Spending
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class CashWalletViewModel(context: Context) : ViewModel() {
    private val repository: Repository by lazy {
        Repository(context)
    }

    private val _money: MutableLiveData<Money> = MutableLiveData()
    val money: LiveData<Money> = _money
    private val _listSpeding: MutableLiveData<List<Spending>> = MutableLiveData(arrayListOf())
    val listSpending: LiveData<List<Spending>> = _listSpeding

    fun getMoney() {
        viewModelScope.launch {
            val getMoney = async {
                repository.getMoney().let {
                    if (it?.isNotEmpty() == true) {
                        _money.postValue(it.last())
                    } else {
                        _money.postValue(Money(money = 0))
                    }
                }
            }
            getMoney.await()
        }
    }

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

class CashWalletViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CashWalletViewModel::class.java)) {
            return CashWalletViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}