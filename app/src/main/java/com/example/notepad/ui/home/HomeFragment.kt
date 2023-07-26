package com.example.notepad.ui.home

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notepad.base.base.BaseFragmentWithBinding
import com.example.notepad.databinding.FragmentHomeBinding
import com.example.notepad.ui.history_revenue_expenditure.collect_money.CollectMoneyAdapter
import com.example.notepad.ui.history_revenue_expenditure.spending_money.SpendingMoneyAdapter


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
        viewModel.getListSpending()
        viewModel.getLisCollect()
        viewModel.getTotalExpenditure()


    }

    override fun initData() {

        viewModel.listColum.observe(viewLifecycleOwner) {
            binding.chart.setData(it.first, it.second)
        }
        viewModel.money.observe(viewLifecycleOwner) {
            binding.viewTotalMoney.money.text = (it.money ?: 0).toString().plus(" VND")
        }


    }

    override fun initAction() {


    }

}