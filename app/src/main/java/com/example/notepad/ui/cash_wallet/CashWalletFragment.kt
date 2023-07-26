package com.example.notepad.ui.cash_wallet

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.notepad.base.base.BaseFragmentWithBinding
import com.example.notepad.databinding.FragmentCashWalletBinding
import com.example.quanlychitieu.ui.cash_wallet.CashWalletViewModel
import com.example.quanlychitieu.ui.cash_wallet.CashWalletViewModelFactory
import com.example.quanlychitieu.utils.click


class CashWalletFragment : BaseFragmentWithBinding<FragmentCashWalletBinding>() {

    companion object {
        fun newInstance() = CashWalletFragment()
    }

    private lateinit var cashWalletAdapter: CashWalletAdapter
    private lateinit var viewModel: CashWalletViewModel
    override fun getViewBinding(inflater: LayoutInflater): FragmentCashWalletBinding =
        FragmentCashWalletBinding.inflate(inflater).apply {
            viewModel = ViewModelProvider(
                this@CashWalletFragment,
                CashWalletViewModelFactory(requireContext())
            )[CashWalletViewModel::class.java]
        }


    override fun init() {
        setToolbar(binding.toolbar)
        cashWalletAdapter = CashWalletAdapter()
        binding.rvList.adapter = cashWalletAdapter
    }

    override fun initData() {
        viewModel.getListSpending()
        viewModel.getMoney()
        viewModel.money.observe(viewLifecycleOwner) {
            binding.money.text = (it.money ?: 0).toString().plus(" VND")
        }
        viewModel.listSpending.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.texterr.visibility = View.GONE
                val data = it.reversed()
                cashWalletAdapter.setData(data)
            } else {
                binding.texterr.visibility = View.VISIBLE
                cashWalletAdapter.setData(listOf())
            }
        }
    }

    override fun initAction() {
        binding.fab.click {
//            mainActivity.addFragment(this, ChangeFragment.newInstance())
        }

    }
}