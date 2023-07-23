package com.example.notepad.fragment

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notepad.MainApp
import com.example.notepad.MyAlarmManager
import com.example.notepad.NotesDatabaseHelper
import com.example.notepad.R
import com.example.notepad.activity.NotesArchiveActivity
import com.example.notepad.activity.NotesRecycleActivity
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

    @SuppressLint("ClickableViewAccessibility")
    private fun actionView() {
        mDatabaseHelper = MainApp.getInstant()?.mDatabaseHelper

        addDataToList()
        mNoteAdapter = NotesAdapter(requireActivity(),
            onClickItem = {
                val mIntent = Intent(activity, NotesArchiveActivity::class.java)
                if (mList.let { it6 ->
                        it6.getOrNull(
                            mNoteAdapter.getListItem().indexOf(it)
                        )?.passwordNote == ""
                    }) {
                    mIntent.putExtra("position_archive", mNoteAdapter.getListItem().indexOf(it))
                    startActivity(mIntent)
                    activity?.overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
                    activity?.finish()
                } else {
                    val mDialog = activity?.let { it1 -> Dialog(it1) }
                    mDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    mDialog?.setContentView(R.layout.dialog_password_custom)
                    val mWindow = mDialog?.window ?: return@NotesAdapter
                    mWindow.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )
                    mWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    val mWindowAttribute = mWindow.attributes
                    mWindowAttribute.gravity = Gravity.BOTTOM
                    mWindow.attributes = mWindowAttribute
                    mDialog.setCancelable(true)
                    mDialog.findViewById<TextView>(R.id.Text1).text = "${
                        mList.let { it5 ->
                            it5.getOrNull(
                                mNoteAdapter.getListItem().indexOf(it)
                            )?.title?.trim()
                        }
                    }"
                    mDialog.findViewById<TextView>(R.id.TextTitle).text =
                        "Bạn cần nhập mật khẩu để xem hoặc chỉnh sửa ghi chú"
                    val noThanks = mDialog.findViewById<Button>(R.id.ButtonNoPass)
                    val setPass = mDialog.findViewById<Button>(R.id.ButtonSetPass)
                    val password = mDialog.findViewById<EditText>(R.id.EditTextPasswordNote)
                    noThanks.text = "Đóng"
                    setPass.text = "Truy cập"
                    var isHidePass = 0
                    password!!.setOnTouchListener { view, motionEvent ->
                        val DRAWABLE_RIGHT = 2
                        if (motionEvent.action == MotionEvent.ACTION_UP) {
                            if (motionEvent.rawX >= (password.right - password.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                                isHidePass++
                                password.inputType = InputType.TYPE_CLASS_TEXT
                                password.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    0,
                                    R.drawable.visibility_off,
                                    0
                                )
                                if (isHidePass > 1) {
                                    password.inputType =
                                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                                    password.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        0,
                                        R.drawable.visibility,
                                        0
                                    )
                                    isHidePass = 0
                                }
                                return@setOnTouchListener true
                            }
                        }
                        return@setOnTouchListener false
                    }
                    noThanks.setOnClickListener {
                        noThanks.setBackgroundResource(R.drawable.bg_btn_set_pass)
                        noThanks.setTextColor(Color.BLACK)
                        setPass.setBackgroundResource(R.drawable.bg_btn_no_pass)
                        setPass.setTextColor(resources.getColor(R.color.Grey))
                        password.setText("")
                        mDialog.dismiss()
                    }

                    setPass.setOnClickListener { _ ->
                        noThanks.setBackgroundResource(R.drawable.bg_btn_no_pass)
                        noThanks.setTextColor(resources.getColor(R.color.Grey))
                        setPass.setBackgroundResource(R.drawable.bg_btn_set_pass)
                        setPass.setTextColor(Color.BLACK)
                        if (password.text.isEmpty()) {
                            createCustomToast(
                                R.drawable.warning,
                                resources.getString(R.string.pass_not_null)
                            )
                            Handler().postDelayed({
                                setPass.setBackgroundResource(R.drawable.bg_btn_no_pass)
                                setPass.setTextColor(resources.getColor(R.color.Grey))
                            }, 1)
                        } else {
                            mList.let { it3 ->
                                if (password.text.toString().trim() != it3.getOrNull(
                                        mNoteAdapter.getListItem().indexOf(it)
                                    )!!.passwordNote.toString()
                                        .trim()
                                ) {
                                    createCustomToast(
                                        R.drawable.warning,
                                        "Mật khẩu bạn vừa nhập không đúng"
                                    )
                                    Handler().postDelayed({
                                        setPass.setBackgroundResource(R.drawable.bg_btn_no_pass)
                                        setPass.setTextColor(resources.getColor(R.color.Grey))
                                    }, 1)
                                } else {
                                    mIntent.putExtra("position_archive", mNoteAdapter.getListItem().indexOf(it))
                                    startActivity(mIntent)
                                    activity?.overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
                                    activity?.finish()
                                }
                            }
                        }
                    }
                    mDialog.show()
                }
            }, onClickClose = {
                if (mList.let { it6 ->
                        it6.getOrNull(
                            mNoteAdapter.getListItem().indexOf(it)
                        )?.passwordNote == ""
                    }) {
                    mList.let { it7 ->
                        val notesModel = NotesModel()
                        val takeNoteActivity = it7.getOrNull(mNoteAdapter.getListItem().indexOf(it))
                        cancelPending(takeNoteActivity!!.milliSeconds)
                        notesModel.takeNoteID = takeNoteActivity.takeNoteID
                        notesModel.milliSeconds = -1
                        notesModel.timeSet = ""
                        mDatabaseHelper!!.deleteTimeSet(notesModel, Table.type_archive)
                        mDatabaseHelper?.getAllNotes(Table.type_archive)
                    }
                } else {
                    val mDialog = activity?.let { it1 -> Dialog(it1) }
                    mDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    mDialog?.setContentView(R.layout.dialog_password_custom)
                    val mWindow = mDialog?.window ?: return@NotesAdapter
                    mWindow.setLayout(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.WRAP_CONTENT
                    )
                    mWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    val mWindowAttribute = mWindow.attributes
                    mWindowAttribute.gravity = Gravity.BOTTOM
                    mWindow.attributes = mWindowAttribute
                    mDialog.setCancelable(true)
                    mDialog.findViewById<TextView>(R.id.Text1).text = "${
                        mList.let { it5 ->
                            it5.getOrNull(
                                mNoteAdapter.getListItem().indexOf(it)
                            )?.title?.trim()
                        }
                    }"
                    mDialog.findViewById<TextView>(R.id.TextTitle).text =
                        "Bạn cần nhập mật khẩu để xóa thời gian đã hẹn"
                    val noThanks = mDialog.findViewById<Button>(R.id.ButtonNoPass)
                    val setPass = mDialog.findViewById<Button>(R.id.ButtonSetPass)
                    val password = mDialog.findViewById<EditText>(R.id.EditTextPasswordNote)
                    noThanks.text = "Đóng"
                    setPass.text = "Xóa thời gian"
                    var isHidePass = 0
                    password!!.setOnTouchListener { view, motionEvent ->
                        val DRAWABLE_RIGHT = 2
                        if (motionEvent.action == MotionEvent.ACTION_UP) {
                            if (motionEvent.rawX >= (password.right - password.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                                isHidePass++
                                password.inputType = InputType.TYPE_CLASS_TEXT
                                password.setCompoundDrawablesWithIntrinsicBounds(
                                    0,
                                    0,
                                    R.drawable.visibility_off,
                                    0
                                )
                                if (isHidePass > 1) {
                                    password.inputType =
                                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                                    password.setCompoundDrawablesWithIntrinsicBounds(
                                        0,
                                        0,
                                        R.drawable.visibility,
                                        0
                                    )
                                    isHidePass = 0
                                }
                                return@setOnTouchListener true
                            }
                        }
                        return@setOnTouchListener false
                    }
                    noThanks.setOnClickListener {
                        noThanks.setBackgroundResource(R.drawable.bg_btn_set_pass)
                        noThanks.setTextColor(Color.BLACK)
                        setPass.setBackgroundResource(R.drawable.bg_btn_no_pass)
                        setPass.setTextColor(resources.getColor(R.color.Grey))
                        password.setText("")
                        mDialog.dismiss()
                    }

                    setPass.setOnClickListener { _ ->
                        noThanks.setBackgroundResource(R.drawable.bg_btn_no_pass)
                        noThanks.setTextColor(resources.getColor(R.color.Grey))
                        setPass.setBackgroundResource(R.drawable.bg_btn_set_pass)
                        setPass.setTextColor(Color.BLACK)
                        if (password.text.isEmpty()) {
                            createCustomToast(
                                R.drawable.warning,
                                resources.getString(R.string.pass_not_null)
                            )
                            Handler().postDelayed({
                                setPass.setBackgroundResource(R.drawable.bg_btn_no_pass)
                                setPass.setTextColor(resources.getColor(R.color.Grey))
                            }, 1)
                        } else {
                            mList.let { it3 ->
                                Log.d(
                                    "pass_note", it3.getOrNull(
                                        mNoteAdapter.getListItem().indexOf(it)
                                    )?.passwordNote?.toString()!!.trim() + password.text.trim()
                                )
                                if (password.text.toString().trim() != it3.getOrNull(
                                        mNoteAdapter.getListItem().indexOf(it)
                                    )!!.passwordNote.toString()
                                        .trim()
                                ) {
                                    createCustomToast(
                                        R.drawable.warning,
                                        "Mật khẩu bạn vừa nhập không đúng"
                                    )
                                    Handler().postDelayed({
                                        setPass.setBackgroundResource(R.drawable.bg_btn_no_pass)
                                        setPass.setTextColor(resources.getColor(R.color.Grey))
                                    }, 1)
                                } else {
                                    mList.let { it2 ->
                                        val notesModel = NotesModel()
                                        val takeNoteActivity = it2.getOrNull(mNoteAdapter.getListItem().indexOf(it))
                                        cancelPending(takeNoteActivity!!.milliSeconds)
                                        notesModel.takeNoteID = takeNoteActivity.takeNoteID
                                        notesModel.milliSeconds = -1
                                        notesModel.timeSet = ""
                                        mDatabaseHelper!!.deleteTimeSet(notesModel, Table.type_archive)
                                        mDatabaseHelper?.getAllNotes(Table.type_archive)
                                    }
                                    mDialog.dismiss()
                                }
                            }
                        }
                    }
                    mDialog.show()
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