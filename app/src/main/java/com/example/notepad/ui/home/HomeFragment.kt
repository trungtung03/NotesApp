package com.example.notepad.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notepad.R
import com.example.notepad.base.base.BaseFragmentWithBinding
import com.example.notepad.databinding.FragmentHomeBinding
import com.example.notepad.ui.history_revenue_expenditure.collect_money.CollectMoneyAdapter
import com.example.notepad.ui.history_revenue_expenditure.spending_money.SpendingMoneyAdapter
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.text.DecimalFormat

class HomeFragment : BaseFragmentWithBinding<FragmentHomeBinding>() {

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel


    override fun getViewBinding(inflater: LayoutInflater): FragmentHomeBinding =
        FragmentHomeBinding.inflate(inflater).apply {
            viewModel = ViewModelProvider(
                this@HomeFragment,
                HomeViewModelFactory(requireContext())
            )[HomeViewModel::class.java]
        }


    override fun init() {
        viewModel.init()
        viewModel.getTotalExpenditure()
    }

    @SuppressLint("SetTextI18n")
    override fun initData() {
        viewModel._Float.observe(viewLifecycleOwner) {
            val entries = ArrayList<PieEntry>()
            val money = it.second
            val first = it.first
            val title = arrayListOf("Tổng thu", "Tổng chi", "Tiết kiệm")
            val colors = arrayListOf(
                resources.getColor(R.color.MediumVioletRed),
                resources.getColor(R.color.Cyan3),
                resources.getColor(R.color.DarkGoldenrod2)
            )
            for (i in 0 until it.first.size) {
                entries.add(PieEntry(first[i], "${title[i]}\n${money[i]} Tr"))
            }

            val dataSet = PieDataSet(entries, "")
            dataSet.setDrawValues(false)

            dataSet.colors = colors
            val pieData = PieData(dataSet)

            binding.chart.description.text = "Tổng thu, Tổng chi, Tiết kiệm"
            binding.chart.data = pieData
        }
        viewModel.money.observe(viewLifecycleOwner) {
            binding.viewTotalMoney.money.text =
                DecimalFormat("###,###,###").format(it.money ?: 0) + " VND"
        }

    }

    override fun initAction() {


    }

}