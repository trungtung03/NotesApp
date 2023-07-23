package com.example.notepad.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.example.notepad.base.recyclerview.BaseRecyclerViewAdapter
import com.example.notepad.base.recyclerview.BaseViewHolder
import com.example.notepad.databinding.ItemRcvListNoteBinding
import com.example.notepad.model.NotesModel

class NotesAdapter(
    private val context: Context,
    val onClickItem: (NotesModel) -> Unit,
    val onClickClose: (NotesModel) -> Unit
) :
    BaseRecyclerViewAdapter<NotesModel, NotesAdapter.ViewHolder>() {

    private var mListData: MutableList<NotesModel> = ArrayList()

    inner class ViewHolder(private val binding: ItemRcvListNoteBinding) :
        BaseViewHolder<NotesModel>(binding) {

        override fun bindViewHolder(data: NotesModel) {
            itemView.setOnClickListener {
                onClickItem.invoke(data)
            }
            binding.ButtonClearRcv.setOnClickListener {
                onClickClose.invoke(data)
            }
            binding.TextViewTimeRcv.text = data.timeNote
            if (data.milliSeconds > 0) {
                binding.layout7.visibility = VISIBLE
            } else if (data.milliSeconds <= 0) {
                binding.layout7.visibility = GONE
            }
            if(data.passwordNote != "") {
                binding.TextViewTitleRcv.visibility = GONE
                binding.TextViewNotesRcv.visibility = GONE
                binding.TextViewTimeRcv.visibility = GONE
                binding.ImageLockRcv.visibility = VISIBLE
            } else {
                if (data.notes.isNotEmpty() && data.title.isNotEmpty()) {
                    binding.TextViewTitleRcv.visibility = VISIBLE
                    binding.TextViewTitleRcv.text = data.title
                    binding.TextViewNotesRcv.visibility = VISIBLE
                    binding.TextViewNotesRcv.text = data.notes
                } else if (data.notes.isNotEmpty() && data.title.isEmpty()) {
                    binding.TextViewNotesRcv.visibility = VISIBLE
                    binding.TextViewNotesRcv.text = data.notes
                } else if (data.title.isNotEmpty() && data.notes.isEmpty()) {
                    binding.TextViewTitleRcv.visibility = VISIBLE
                    binding.TextViewTitleRcv.text = data.title
                }
                binding.TextViewTimeRcv.visibility = VISIBLE
                binding.ImageLockRcv.visibility = GONE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(mList: ArrayList<NotesModel>) {
        mListData = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val mBinding = ItemRcvListNoteBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindViewHolder(mListData[position])

    }

    override fun getListItem(): MutableList<NotesModel> = mListData
}