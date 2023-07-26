package com.example.notepad.base.base.recyclerview

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<T : Any, VB : ViewDataBinding>(
    private val binding: VB
) : RecyclerView.ViewHolder(binding.root) {

    private var _itemData: T? = null
        val itemData get() = _itemData


    open fun bind(itemData: T) {
        this._itemData = itemData
    }

    open fun onItemClickListener( callBack : ()-> Unit){
        itemView.setOnClickListener {
            callBack.invoke()
        }
    }
}
