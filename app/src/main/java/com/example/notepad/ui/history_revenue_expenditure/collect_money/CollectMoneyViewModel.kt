package com.example.notepad.ui.history_revenue_expenditure.collect_money

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notepad.model.model.Collect
import com.example.notepad.repository.Repository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class CollectMoneyViewModel(context: Context) : ViewModel() {
    private val repository: Repository = Repository(context)
    private val _listCollect: MutableLiveData<List<Collect>> = MutableLiveData(arrayListOf())
    val listCollect: LiveData<List<Collect>> = _listCollect
    fun getLisCollect() {
        viewModelScope.launch {
            val getData = async {
                repository.getListCollect().let {
                    if (it?.isNotEmpty() == true) {
                        _listCollect.postValue(it)
                    } else _listCollect.postValue(listOf())
                }
            }
            awaitAll(getData)
        }

    }
}

class CollectMoneyViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectMoneyViewModel::class.java)) {
            return CollectMoneyViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}