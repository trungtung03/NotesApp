package com.example.notepad.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notepad.MainApp
import com.example.notepad.MyAlarmManager
import com.example.notepad.activity.DetailedNotesActivity
import com.example.notepad.NotesDatabaseHelper
import com.example.notepad.R
import com.example.notepad.activity.TakeNoteActivity
import com.example.notepad.adapter.NotesAdapter
import com.example.notepad.base.BaseFragment
import com.example.notepad.custom.Table
import com.example.notepad.databinding.FragmentNotesBinding
import com.example.notepad.model.NotesModel
import kotlin.random.Random

class NotesFragment : BaseFragment<FragmentNotesBinding>() {

    companion object {
        fun newInstance() = NotesFragment()
    }

    private lateinit var mBinding: FragmentNotesBinding
    private lateinit var mNoteAdapter: NotesAdapter
    private var mDatabaseHelper: NotesDatabaseHelper? = null
    private var mListData = arrayListOf<NotesModel>()
    private lateinit var list: List<NotesModel>
    override fun initView(view: View) {
        mBinding = FragmentNotesBinding.bind(view)
        actionView()
    }

    override fun getBinding(): FragmentNotesBinding {
        mBinding = FragmentNotesBinding.inflate(layoutInflater)
        return mBinding
    }

    private fun actionView() {
        mBinding.AddNotes.setOnClickListener {
            openActivity(TakeNoteActivity::class.java)
        }

        mDatabaseHelper = MainApp.getInstant()?.mDatabaseHelper

        addDataToList()
        mNoteAdapter = NotesAdapter(requireActivity(), onClickItem = {
            val mIntent = Intent(activity, DetailedNotesActivity::class.java)
            mIntent.putExtra("position_detail", mNoteAdapter.getListItem().indexOf(it))
            startActivity(mIntent)
            activity?.overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
            activity?.finish()
        }, onClickClose = {
            list.let { it2 ->
                val notesModel = NotesModel()
                val takeNoteActivity = it2.getOrNull(mNoteAdapter.getListItem().indexOf(it))
                cancelPending(takeNoteActivity!!.milliSeconds)
                notesModel.takeNoteID = takeNoteActivity.takeNoteID
                notesModel.milliSeconds = -1
                notesModel.timeSet = ""
                mDatabaseHelper!!.deleteTimeSet(notesModel,"note")
                mDatabaseHelper?.getAllNotes(Table.type_note)
            }
        })

        mBinding.RecyclerViewNotes.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        mBinding.RecyclerViewNotes.adapter = mNoteAdapter
    }

    private fun addDataToList() {
        mDatabaseHelper?.getLiveData(Table.type_note)?.observe(viewLifecycleOwner) {
            mListData.clear()
            list = it
            mListData.addAll(it)
            mNoteAdapter.setData(mListData)
            if (it.size > 0) {
                mBinding.ImageNotebook.visibility = View.GONE
            } else {
                mBinding.ImageNotebook.visibility = View.VISIBLE
            }
        }
    }

    private fun cancelPending(requestCode: Int) {
        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val mIntent = Intent(requireContext(), MyAlarmManager::class.java)
        val pendingCancel = PendingIntent.getBroadcast(
            requireContext(),
            requestCode,
            mIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        alarmManager.cancel(pendingCancel)
        pendingCancel.cancel()
    }
}