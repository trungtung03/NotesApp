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
import com.example.notepad.activity.DetailedNotesActivity
import com.example.notepad.activity.MainActivity
import com.example.notepad.activity.NotesArchiveActivity
import com.example.notepad.activity.NotesRecycleActivity
import com.example.notepad.adapter.NotesAdapter
import com.example.notepad.base.BaseFragment
import com.example.notepad.custom.Table
import com.example.notepad.databinding.FragmentSearchBinding
import com.example.notepad.model.NotesModel


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

    interface OnDataPassedListener {
        fun onDataPassed(isChangeData: Boolean)
    }

    private var dataPassedListener: OnDataPassedListener? = null

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dataPassedListener = context as OnDataPassedListener
    }

    @SuppressLint("ResourceAsColor", "ClickableViewAccessibility")
    private fun actionView() {
        mBinding.btnNote.isEnabled = true
        mBinding.btnNote.setOnClickListener(this)
        mBinding.btnRecycle.setOnClickListener(this)
        mBinding.btnArchive.setOnClickListener(this)
        mNotesDatabaseHelper = MainApp.getInstant()?.mDatabaseHelper
        mNoteAdapter = NotesAdapter(requireActivity(),
            onClickItem = {
                val mIntent = Intent(activity, destinationClass)
                if (MainActivity.mListData.let { it6 ->
                        it6.getOrNull(
                            mNoteAdapter.getListItem().indexOf(it)
                        )?.passwordNote == ""
                    }) {
                    MainActivity.mListData.let { it9 ->
                        val index = it9.getOrNull(mNoteAdapter.getListItem().indexOf(it))
                        mIntent.putExtra(key, index?.takeNoteID)
                        startActivityForResult(mIntent, 0)
                        activity?.overridePendingTransition(R.anim.slide_in, R.anim.fade_out)
                        activity?.finish()
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
                        MainActivity.mListData.let { it5 ->
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
                            MainActivity.mListData.let { it3 ->
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
                                    val index =
                                        it3.getOrNull(mNoteAdapter.getListItem().indexOf(it))
                                    mIntent.putExtra(key, index?.takeNoteID)
                                    startActivityForResult(mIntent, 0)
                                    activity?.overridePendingTransition(
                                        R.anim.slide_in,
                                        R.anim.fade_out
                                    )
                                    activity?.finish()
                                }
                            }
                        }
                    }
                    mDialog.show()
                }
            }, onClickClose = {
                if (MainActivity.mListData.let { it6 ->
                        it6.getOrNull(
                            mNoteAdapter.getListItem().indexOf(it)
                        )?.passwordNote == ""
                    }) {
                    MainActivity.mListData.let { it2 ->
                        val notesModel = NotesModel()
                        val searchNotesData = it2.getOrNull(mNoteAdapter.getListItem().indexOf(it))
                        cancelPending(searchNotesData!!.milliSeconds)
                        notesModel.takeNoteID = searchNotesData.takeNoteID
                        notesModel.milliSeconds = -1
                        notesModel.timeSet = ""
                        mNotesDatabaseHelper?.deleteTimeSet(notesModel, table)
                        mNotesDatabaseHelper?.getAllNotes(table)
                    }
                    dataPassedListener?.onDataPassed(true)
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
                        MainActivity.mListData.let { it5 ->
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
                            MainActivity.mListData.let { it3 ->
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
                                    MainActivity.mListData.let { it2 ->
                                        val notesModel = NotesModel()
                                        val searchNotesData = it2.getOrNull(mNoteAdapter.getListItem().indexOf(it))
                                        cancelPending(searchNotesData!!.milliSeconds)
                                        notesModel.takeNoteID = searchNotesData.takeNoteID
                                        notesModel.milliSeconds = -1
                                        notesModel.timeSet = ""
                                        mNotesDatabaseHelper?.deleteTimeSet(notesModel, table)
                                        mNotesDatabaseHelper?.getAllNotes(table)
                                    }
                                    dataPassedListener?.onDataPassed(true)
                                    mDialog.dismiss()
                                }
                            }
                        }
                    }
                    mDialog.show()
                }
            })

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
                setQuery(mBinding.btnRecycle, mBinding.btnArchive)
                mBinding.btnNote.setBackgroundResource(R.drawable.bg_btn_fgm)
                mBinding.btnRecycle.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                mBinding.btnArchive.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                table = Table.type_note
                key = "search_detail"
                destinationClass = DetailedNotesActivity::class.java
            }

            R.id.btn_recycle -> {
                setQuery(mBinding.btnNote, mBinding.btnArchive)
                mBinding.btnRecycle.setBackgroundResource(R.drawable.bg_btn_fgm)
                mBinding.btnNote.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                mBinding.btnArchive.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                table = Table.type_recycle
                key = "search_recycle"
                destinationClass = NotesRecycleActivity::class.java
            }

            R.id.btn_archive -> {
                setQuery(mBinding.btnNote, mBinding.btnRecycle)
                mBinding.btnArchive.setBackgroundResource(R.drawable.bg_btn_fgm)
                mBinding.btnRecycle.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                mBinding.btnNote.setBackgroundResource(R.drawable.bg_btn_fgm_default)
                table = Table.type_archive
                key = "search_archive"
                destinationClass = NotesArchiveActivity::class.java
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

    private fun setQuery(button: Button, button_: Button) {
        if (button.isEnabled || button_.isEnabled) {
            MainActivity.searchView.setQuery("", false)
        }
    }
}