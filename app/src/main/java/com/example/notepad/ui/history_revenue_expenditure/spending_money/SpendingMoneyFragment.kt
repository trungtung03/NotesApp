package com.example.notepad.ui.history_revenue_expenditure.spending_money

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notepad.base.base.BaseFragmentWithBinding
import com.example.notepad.databinding.FragmentSpendingMoneyBinding
import com.example.quanlychitieu.ui.history_revenue_expenditure.spending_money.SpendingMoneyViewModel
import com.example.quanlychitieu.ui.history_revenue_expenditure.spending_money.SpendingMoneyViewModelFactory


class SpendingMoneyFragment : BaseFragmentWithBinding<FragmentSpendingMoneyBinding>() {

    companion object {
        fun newInstance() = SpendingMoneyFragment()
    }

    private lateinit var adapter: SpendingMoneyAdapter

    private lateinit var viewModel: SpendingMoneyViewModel
    override fun getViewBinding(inflater: LayoutInflater): FragmentSpendingMoneyBinding =
        FragmentSpendingMoneyBinding.inflate(inflater).apply {
            viewModel =
                ViewModelProvider(
                    this@SpendingMoneyFragment,
                    SpendingMoneyViewModelFactory(requireContext())
                ).get(SpendingMoneyViewModel::class.java)
        }


    override fun init() {
        adapter = SpendingMoneyAdapter()
        binding.rvList.adapter = adapter
        binding.rvList.addItemDecoration(
            DividerItemDecoration(
                activity,
                LinearLayoutManager.VERTICAL
            )
        )

    }

    override fun initData() {
        viewModel.getListSpending()
        viewModel.listSpending.observe(viewLifecycleOwner) {
            adapter.setData(it.ifEmpty { listOf() })
            if (it.isNotEmpty()) {
                binding.rvList.visibility = View.VISIBLE
                binding.textErr.visibility = View.GONE
            } else {
                binding.rvList.visibility = View.GONE
                binding.textErr.visibility = View.VISIBLE
            }
        }
    }

    override fun initAction() {

    }

}