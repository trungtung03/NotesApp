package com.example.notepad.ui.add.add_collect

import android.view.LayoutInflater
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.notepad.base.base.BaseFragmentWithBinding
import com.example.notepad.databinding.FragmentAddCollectMoneyBinding
import com.example.notepad.model.model.Collect
import com.example.notepad.model.model.Money
import com.example.quanlychitieu.utils.Utils
import com.example.quanlychitieu.utils.click

class AddCollectMoneyFragment : BaseFragmentWithBinding<FragmentAddCollectMoneyBinding>() {

    companion object {
        fun newInstance() = AddCollectMoneyFragment()
    }

    private var money: Int = 0


    private lateinit var viewModel: AddCollectMoneyViewModel
    override fun getViewBinding(inflater: LayoutInflater): FragmentAddCollectMoneyBinding {
        viewModel = ViewModelProvider(this, AddCollectMoneyViewModelFactory(requireContext())).get(
            AddCollectMoneyViewModel::class.java
        )
        return FragmentAddCollectMoneyBinding.inflate(inflater)
    }


    override fun init() {

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
                    val collect = Collect(
                        money = binding.money.getMoney(),
                        title = binding.category.text.toString(),
                        content = binding.content.text.toString(),
                        date = binding.date.text.toString()
                    )
                    viewModel.setCollect(collect)
                    val money = Money(money = this.money + binding.money.getMoney())
                    viewModel.setMoney(money) {
                        Toast.makeText(requireContext(), "Đã thêm thành công", Toast.LENGTH_LONG)
                            .show()
                        mainActivity.homeFragment.onBackNavigation?.invoke()
                    }

                }
            }
        }
    }


}