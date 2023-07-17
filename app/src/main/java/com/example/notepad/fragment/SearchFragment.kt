package com.example.notepad.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notepad.MainApp
import com.example.notepad.NotesDatabaseHelper
import com.example.notepad.R
import com.example.notepad.activity.DetailedNotesActivity
import com.example.notepad.activity.MainActivity
import com.example.notepad.activity.NotesArchiveActivity
import com.example.notepad.activity.NotesRecycleActivity
import com.example.notepad.adapter.NotesAdapter
import com.example.notepad.base.BaseFragment
import com.example.notepad.custom.Table
import com.example.notepad.databinding.FragmentSearchBinding


class SearchFragment : BaseFragment<FragmentSearchBinding>(), View.OnClickListener {

    companion object {
        fun newInstance() = SearchFragment()

        @SuppressLint("StaticFieldLeak")
        lateinit var mBinding: FragmentSearchBinding

        @SuppressLint("StaticFieldLeak")
        lateinit var mNoteAdapter: NotesAdapter
        var table = Table.type_note
        var mNotesDatabaseHelper: NotesDatabaseHelper? = null
    }

    private lateinit var key: String
    private lateinit var destinationClass: Class<*>

    override fun initView(view: View) {
        mBinding = FragmentSearchBinding.bind(view)
        actionView()
    }

    override fun getBinding(): FragmentSearchBinding {
        mBinding = FragmentSearchBinding.inflate(layoutInflater)
        return mBinding
    }

    @SuppressLint("ResourceAsColor")
    private fun actionView() {
        mBinding.btnNote.isEnabled = true
        mBinding.btnNote.setOnClickListener(this)
        mBinding.btnRecycle.setOnClickListener(this)
        mBinding.btnArchive.setOnClickListener(this)
        mNotesDatabaseHelper = MainApp.getInstant()?.mDatabaseHelper
        mNoteAdapter = NotesAdapter(requireActivity(),
            onClickItem = {
                val mIntent = Intent(activity, destinationClass)
                MainActivity.mListData.let { it2 ->
                    val index = it2.getOrNull(mNoteAdapter.getListItem().indexOf(it))
                    Log.d("alo ", index.toString())
                    Log.d("alo ", index?.takeNoteID.toString())
                    mIntent.putExtra(key, index?.takeNoteID)
                    startActivity(mIntent)
                    activity?.overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
                }
            }, onClickClose = {})

        mBinding.RecyclerSearch.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mBinding.RecyclerSearch.adapter = mNoteAdapter

        if (mBinding.btnNote.isEnabled) {
            mBinding.btnNote.setBackgroundResource(R.drawable.bg_btn_fgm)
            table = Table.type_note
            key = "search_detail"
            destinationClass = DetailedNotesActivity::class.java
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_note -> {
                mBinding.btnNote.setBackgroundResource(R.drawable.bg_btn_fgm)
                mBinding.btnRecycle.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                mBinding.btnArchive.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                table = Table.type_note
                key = "search_detail"
                destinationClass = DetailedNotesActivity::class.java
            }

            R.id.btn_recycle -> {
                mBinding.btnRecycle.setBackgroundResource(R.drawable.bg_btn_fgm)
                mBinding.btnNote.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                mBinding.btnArchive.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                table = Table.type_recycle
                key = "search_recycle"
                destinationClass = NotesRecycleActivity::class.java
            }

            R.id.btn_archive -> {
                mBinding.btnArchive.setBackgroundResource(R.drawable.bg_btn_fgm)
                mBinding.btnRecycle.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                mBinding.btnNote.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                table = Table.type_archive
                key = "search_archive"
                destinationClass = NotesArchiveActivity::class.java
            }
        }
    }
}