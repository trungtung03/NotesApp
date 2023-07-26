package com.example.notepad.ui.history_revenue_expenditure.collect_money

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.notepad.databinding.ItemSpendingBinding
import com.example.notepad.model.model.Collect

class CollectMoneyAdapter : RecyclerView.Adapter<CollectMoneyAdapter.ViewHolder>() {
    private var list: List<Collect> = arrayListOf()

    inner class ViewHolder(val spendingBinding: ItemSpendingBinding) :
        RecyclerView.ViewHolder(spendingBinding.root) {
        fun bind(collect: Collect) {
            spendingBinding.money.text = "+".plus(collect.money).plus(" VND")
            spendingBinding.title.text = "Hạng mục: ".plus(collect.title)
            spendingBinding.date.text = "Ngày: ".plus(collect.date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSpendingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list.get(position))
    }

    fun setData(list: List<Collect>) {
        this.list = list
        notifyDataSetChanged()
    }
}