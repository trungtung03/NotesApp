package com.example.notepad.ui.add.add_spending

import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.notepad.base.base.BaseFragmentWithBinding
import com.example.notepad.databinding.FragmentAddSpendingBinding
import com.example.notepad.model.model.Money
import com.example.quanlychitieu.model.Spending
import com.example.quanlychitieu.utils.Utils
import com.example.quanlychitieu.utils.click

class AddSpendingFragment : BaseFragmentWithBinding<FragmentAddSpendingBinding>() {

    companion object {
        fun newInstance() = AddSpendingFragment()
    }

    private lateinit var viewModel: AddSpendingViewModel

    private var money: Int = 0

    override fun getViewBinding(inflater: LayoutInflater): FragmentAddSpendingBinding {

        return FragmentAddSpendingBinding.inflate(inflater)
    }


    override fun init() {
        viewModel = ViewModelProvider(this, AddSpendingViewModelFactory(this.requireContext())).get(
            AddSpendingViewModel::class.java
        )
    }

    override fun initData() {
        viewModel.getMoney()
        viewModel.money.observe(viewLifecycleOwner) {
            this.money = it.money ?: 0
        }

    }

    override fun initAction() {
        binding.save.click {
            if (binding.content.text?.trim().isNullOrEmpty() || binding.category.text?.trim()
                    .isNullOrEmpty() || binding.money.text?.trim().isNullOrEmpty()
            ) {
                Toast.makeText(context, "Vui Lòng Nhập đầy đủ thông tin ", Toast.LENGTH_LONG).show()
            } else {
                if (!Utils.isValidDate(binding.date.text?.trim().toString())) {
                    Toast.makeText(
                        context,
                        "Vui lòng nhập ngày tháng năm theo định dạng ngày/tháng/năm",
                        Toast.LENGTH_LONG
                    ).show()

                } else {
                    val spending = Spending(
                        money = binding.money.getMoney(),
                        title = binding.category.text.toString(),
                        content = binding.content.text.toString(),
                        date = binding.date.text.toString()
                    )
                    viewModel.setSpending(spending)
                    val money = Money(money = this.money.minus(binding.money.getMoney()))
                    viewModel.setMoney(money) {
                        Toast.makeText(requireContext(),"Đã thêm thành công", Toast.LENGTH_LONG).show()
                        mainActivity.homeFragment.onBackNavigation?.invoke()
                    }

                }
            }
        }
    }


}