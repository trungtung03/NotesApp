package com.example.notepad.ui.cash_wallet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.databinding.ItemSpendingBinding
import com.example.quanlychitieu.model.Spending

class CashWalletAdapter : RecyclerView.Adapter<CashWalletAdapter.ViewHolder>() {
    private var listSpending: List<Spending> = arrayListOf()

    inner class ViewHolder(private val binding: ItemSpendingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(spending: Spending) {
            binding.title.text = "Hạng mục: ".plus(spending.title)
            binding.date.text = "Ngày: ".plus(spending.date)
            binding.money.text = "- ".plus(spending.money.toString()).plus("VND")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSpendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listSpending.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listSpending[position])

    }

    fun setData(listData: List<Spending>) {
        listSpending = listData
        notifyDataSetChanged()
    }

}