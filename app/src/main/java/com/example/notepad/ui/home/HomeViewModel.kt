package com.example.notepad.ui.home

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.notepad.model.model.Collect
import com.example.notepad.model.model.Money
import com.example.notepad.repository.Repository
import com.example.quanlychitieu.model.Spending
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.math.BigDecimal

class HomeViewModel(context: Context) : ViewModel() {
    private val repository: Repository = Repository(context)

    private val _money: MutableLiveData<Money> = MutableLiveData()
    val money: LiveData<Money> = _money
    val _float: MutableLiveData<Pair<List<Float>, List<Double>>> = MutableLiveData()
    val _Float: LiveData<Pair<List<Float>, List<Double>>> = _float

    private val _listColum: MutableLiveData<Pair<List<Triple<String, Float, Int>>, List<Double>>> =
        MutableLiveData()
    val listColum: LiveData<Pair<List<Triple<String, Float, Int>>, List<Double>>> = _listColum

    private val _listSpeding: MutableLiveData<List<Spending>> = MutableLiveData(arrayListOf())
    val listSpending: LiveData<List<Spending>> = _listSpeding

    private val _listCollect: MutableLiveData<List<Collect>> = MutableLiveData(arrayListOf())
    val listCollect: LiveData<List<Collect>> = _listCollect



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

    fun getTotalExpenditure() {
        viewModelScope.launch {
            var list: ArrayList<Triple<String, Float, Int>> = arrayListOf()
            var list2: ArrayList<Float> = arrayListOf()
            val listTotalMoney: ArrayList<Double> = arrayListOf()
            async {
                var total = 0
                repository.getListCollect().forEach {
                    if (it.money ?: 0 > 0) total += it.money!!
                }
                listTotalMoney.add(getMillionVND(total.toDouble()))
            }.await()
            async {
                var total = 0
                repository.getListSpending().forEach {
                    if (it.money ?: 0 > 0) total += it.money!!
                }
                listTotalMoney.add(getMillionVND(total.toDouble()))
            }.await()
            if (listTotalMoney[0] - listTotalMoney[1] > 0) {
                listTotalMoney.add(listTotalMoney[0] - listTotalMoney[1])
                _money.postValue(Money(money= ((listTotalMoney[0] - listTotalMoney[1])*1000000.0).toInt()))
            } else {
                listTotalMoney.add(getMillionVND(0.0))
                _money.postValue(Money(money= 0))
            }
            when (getComparisonMoneyCollectAndSpending(listTotalMoney)) {
                1 -> {
                    if (listTotalMoney[0] > 0) {
                        list.add(Triple("Tổng Thu", 100f, Color.BLUE))
                        list2.add(100f)
                    } else {
                        list2.add(1f)
                        list.add(Triple("Tổng Thu", 1f, Color.BLUE))
                    }
                    if (listTotalMoney[1] > 0) {
                        list2.add(
                            getTotalRevenueVersusExpenditure(
                                listTotalMoney[1],
                                listTotalMoney[2]
                            ).toFloat()
                        )
                        list.add(
                            Triple(
                                "Tổng Chi", getTotalRevenueVersusExpenditure(
                                    listTotalMoney.get(1),
                                    listTotalMoney.get(0)
                                ).toFloat(),
                                Color.GREEN
                            )
                        )
                    } else {
                        list2.add(1f)
                        list.add(
                            Triple(
                                "Tổng Chi",
                                1f,
                                Color.GREEN
                            )
                        )
                    }
                    list.add(
                        Triple(
                            "Tiết kiệm",
                            getTotalRevenueVersusExpenditure(
                                listTotalMoney.get(1),
                                listTotalMoney.get(0)
                            ).toFloat(),
                            Color.LTGRAY
                        )
                    )
                    list2.add(
                        getTotalRevenueVersusExpenditure(
                            listTotalMoney.get(1),
                            listTotalMoney.get(0)
                        ).toFloat()
                    )
                    _float.postValue(Pair(list2, listTotalMoney))
                    _listColum.postValue(Pair(list, listTotalMoney))
                }

                0 -> {
                    if (listTotalMoney.get(0) > 0) {
                        list.add(Triple("Tổng Thu", 100f, Color.BLUE))
                        list2.add(100f)
                    } else {
                        list.add(Triple("Tổng Thu", 1f, Color.BLUE))
                        list2.add(1f)
                    }
                    if (listTotalMoney.get(1) > 0) {
                        list.add(
                            Triple(
                                "Tổng Chi",
                                100f,
                                Color.GREEN
                            )
                        )
                        list2.add(100f)
                    } else {
                        list.add(
                            Triple(
                                "Tổng Chi",
                                1f,
                                Color.GREEN
                            )
                        )
                        list2.add(1f)
                    }
                    list.add(
                        Triple(
                            "Tiết kiệm",
                            getTotalRevenueVersusExpenditure(
                                listTotalMoney.get(0),
                                listTotalMoney.get(2)
                            ).toFloat(),
                            Color.LTGRAY
                        )
                    )
                    list2.add(
                        getTotalRevenueVersusExpenditure(
                            listTotalMoney.get(0),
                            listTotalMoney.get(2)
                        ).toFloat()
                    )
                    _float.postValue(Pair(list2, listTotalMoney))
                    _listColum.postValue(Pair(list, listTotalMoney))
                }

                -1 -> {
                    if (listTotalMoney.get(0) > 0) {
                        list.add(
                            Triple(
                                "Tổng Thu", getTotalRevenueVersusExpenditure(
                                    listTotalMoney.get(1),
                                    listTotalMoney.get(2)
                                ).toFloat(), Color.BLUE
                            )
                        )
                        list2.add(
                            getTotalRevenueVersusExpenditure(
                                listTotalMoney.get(1),
                                listTotalMoney.get(2)
                            ).toFloat()
                        )
                    } else {
                        list.add(Triple("Tổng Thu", 1f, Color.BLUE))
                        list2.add(1f)
                    }

                    if (listTotalMoney.get(1) > 0) {
                        list.add(
                            Triple(
                                "Tổng Chi",
                                100f,
                                Color.GREEN
                            )
                        )
                        list2.add(100f)
                    } else {
                        list.add(
                            Triple(
                                "Tổng Chi",
                                1f,
                                Color.GREEN
                            )
                        )
                        list2.add(1f)
                    }
                    list.add(
                        Triple(
                            "Tiết kiệm",
                            getTotalRevenueVersusExpenditure(
                                listTotalMoney.get(1),
                                listTotalMoney.get(2)
                            ).toFloat(),
                            Color.LTGRAY
                        )
                    )
                    list2.add(
                        getTotalRevenueVersusExpenditure(
                            listTotalMoney[1],
                            listTotalMoney[2]
                        ).toFloat()
                    )
                    _float.postValue(Pair(list2, listTotalMoney))
                    _listColum.postValue(Pair(list, listTotalMoney))
                }
            }
        }
    }

    fun getTotalRevenueVersusExpenditure(totalA: Double, totalB: Double): Double {
        return if (totalB > 0) {
            totalA / totalB * 100
        } else 1.0
    }


    fun getComparisonMoneyCollectAndSpending(listTotalMoney: ArrayList<Double>): Int {
        return (listTotalMoney.getOrNull(0) ?: 0.0).compareTo((listTotalMoney.getOrNull(1) ?: 0.0))
    }


    fun getMillionVND(money: Double): Double {
        val millions = money / 1000000.0

        return BigDecimal(millions).setScale(3, BigDecimal.ROUND_HALF_UP).toDouble()
    }

}

class HomeViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}