package com.example.notepad.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notepad.MainApp
import com.example.notepad.MyAlarmManager
import com.example.notepad.NotesDatabaseHelper
import com.example.notepad.R
import com.example.notepad.activity.NotesArchiveActivity
import com.example.notepad.adapter.NotesAdapter
import com.example.notepad.base.BaseFragment
import com.example.notepad.custom.Table
import com.example.notepad.databinding.FragmentArchiveBinding
import com.example.notepad.model.NotesModel

class ArchiveFragment : BaseFragment<FragmentArchiveBinding>() {

    companion object {
        fun newInstance() = ArchiveFragment()
    }

    private lateinit var mBinding: FragmentArchiveBinding
    private lateinit var mNoteAdapter: NotesAdapter
    private var mDatabaseHelper: NotesDatabaseHelper? = null
    private val mListData = arrayListOf<NotesModel>()
    private lateinit var mList: List<NotesModel>

    override fun initView(view: View) {
        mBinding = FragmentArchiveBinding.bind(view)
        actionView()
    }

    override fun getBinding(): FragmentArchiveBinding {
        mBinding = FragmentArchiveBinding.inflate(layoutInflater)
        return mBinding
    }

    private fun actionView() {
        mDatabaseHelper = MainApp.getInstant()?.mDatabaseHelper

        addDataToList()
        mNoteAdapter = NotesAdapter(requireActivity(),
            onClickItem = {
                val mIntent = Intent(activity, NotesArchiveActivity::class.java)
                mIntent.putExtra("position_archive", mNoteAdapter.getListItem().indexOf(it))
                startActivity(mIntent)
                activity?.overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
                activity?.finish()
            }, onClickClose = {
                mList.let { it2 ->
                    val notesModel = NotesModel()
                    val takeNoteActivity = it2.getOrNull(mNoteAdapter.getListItem().indexOf(it))
                    cancelPending(takeNoteActivity!!.milliSeconds)
                    notesModel.takeNoteID = takeNoteActivity.takeNoteID
                    notesModel.milliSeconds = -1
                    notesModel.timeSet = ""
                    mDatabaseHelper!!.deleteTimeSet(notesModel, "archive")
                    mDatabaseHelper?.getAllNotes(Table.type_archive)
                }
            })

        mBinding.RecyclerArchive.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        mBinding.RecyclerArchive.adapter = mNoteAdapter
    }

    private fun addDataToList() {
        mDatabaseHelper?.getLiveData(Table.type_archive)?.observe(viewLifecycleOwner) {
            mList = it
            mListData.clear()
            mListData.addAll(it)
            mNoteAdapter.setData(mListData)
            if (it.size > 0) {
                mBinding.ImageArchive.visibility = View.GONE
            } else {
                mBinding.ImageArchive.visibility = View.VISIBLE
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