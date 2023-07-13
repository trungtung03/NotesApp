package com.example.notepad.base.recyclerview

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T : Any, VH : BaseViewHolder<T>>() :
    RecyclerView.Adapter<VH>() {

    interface OnClickItem {
        fun isClickItem(view: View, position: Int, isCheck: Boolean)
    }

    abstract fun setData(mList: ArrayList<T>)
    abstract fun getListItem(): MutableList<T>
}