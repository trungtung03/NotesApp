package com.example.notepad.ui.history_revenue_expenditure.spending_money

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.databinding.ItemSpendingBinding
import com.example.quanlychitieu.model.Spending

class SpendingMoneyAdapter : RecyclerView.Adapter<SpendingMoneyAdapter.ViewHolder>() {
    private var list: List<Spending> = arrayListOf()

    inner class ViewHolder(val spendingBinding: ItemSpendingBinding) :
        RecyclerView.ViewHolder(spendingBinding.root) {
        fun bind(spending: Spending) {
            spendingBinding.money.text = "-".plus(spending.money).plus(" VND")
            spendingBinding.title.text = "Hạng mục: ".plus(spending.title)
            spendingBinding.date.text = "Ngày: ".plus(spending.date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSpendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])

    }

    fun setData(list: List<Spending>) {
        this.list = list
        notifyDataSetChanged()
    }
}