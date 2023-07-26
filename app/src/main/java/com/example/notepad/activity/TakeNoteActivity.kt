package com.example.notepad.activity

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.TimePicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.notepad.MainApp
import com.example.notepad.MyAlarmManager
import com.example.notepad.NotesDatabaseHelper
import com.example.notepad.R
import com.example.notepad.base.BaseActivity
import com.example.notepad.custom.DatePickerDialog
import com.example.notepad.custom.Table
import com.example.notepad.custom.TimePickerDialog
import com.example.notepad.databinding.ActivityTakeNoteBinding
import com.example.notepad.model.NotesModel
import java.text.SimpleDateFormat
import java.util.*

@Suppress("DEPRECATION")
class TakeNoteActivity : BaseActivity(), android.app.DatePickerDialog.OnDateSetListener,
    android.app.TimePickerDialog.OnTimeSetListener {

    private lateinit var mBinding: ActivityTakeNoteBinding
    private var mDatabaseHelper: NotesDatabaseHelper? = null
    private var mUri: Uri? = null
    private val TITLE_INTENT_RESULT_LAUNCHER = "Select picture"
    private val MY_REQUEST_CODE = 10
    private val PERMISSION_FAIL = "Please allow the app to access the photo storage"
    private lateinit var dateInstance: String
    private lateinit var timeInstance: String
    private var dateMilli: Long = -1
    private var timeSet: String = ""
    private var pathImage: String = ""
    private var oldTime: String = ""
    private var password: EditText? = null
    private var passwordNotes: String = ""

    private val mActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            if (intent == null) {
                return@registerForActivityResult
            } else {
                mUri = intent.data
                Glide.with(this).load(getPath(uri = mUri)).into(mBinding.ImageTakeNotes)
                mBinding.ImageTakeNotes.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("Range")
    fun getPath(uri: Uri?): String? {
        var cursor: Cursor? = contentResolver.query(uri!!, null, null, null, null)
        cursor?.moveToFirst()
        var document_id: String? = cursor?.getString(0)
        document_id = document_id?.substring(document_id.lastIndexOf(":").plus(1))
        cursor?.close()
        cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null, MediaStore.Images.Media._ID + " = ? ", arrayOf(document_id), null
        )
        cursor?.moveToFirst()
        val path: String? = cursor?.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor?.close()
        return path
    }

    override fun setLayout(): View = mBinding.root

    @SuppressLint("SimpleDateFormat", "ClickableViewAccessibility")
    override fun initView() {
        mBinding = ActivityTakeNoteBinding.inflate(layoutInflater)

        setSupportActionBar(mBinding.ToolbarTakeNotes)

        mDatabaseHelper = MainApp.getInstant()?.mDatabaseHelper

        mBinding.ButtonBack.setOnClickListener {
            setDataToBundle(true)
            setTime(
                dateMilli,
                dateMilli.toInt(),
                "${resources.getString(R.string.note_notification)} ${
                    mBinding.EditTextTitle.text
                }",
                mDatabaseHelper?.getLiveData(Table.type_note)?.value?.first()?.takeNoteID?.toInt()
                    ?: 0,
            )
        }

        mBinding.TextViewDateTime.text =
            SimpleDateFormat("EEE d MMM yyyy").format(Calendar.getInstance().time).toString().trim()

        dateInstance = SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time)
        timeInstance = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)
        oldTime = SimpleDateFormat("dd/MM/yyyy HH:mm").format(Calendar.getInstance().time)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_take_note, menu)
        if (passwordNotes != "") {
            menu?.findItem(R.id.take_note_set_pass)?.title =
                resources.getString(R.string.edit_pass)
            menu?.findItem(R.id.take_note_delete_pass)?.isVisible = true
        } else {
            menu?.findItem(R.id.take_note_set_pass)?.title =
                resources.getString(R.string.set_pass)
            menu?.findItem(R.id.take_note_delete_pass)?.isVisible = false
        }
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setPassDialog(isCheckTable: Boolean, update: Boolean) {
        val mDialog = Dialog(this@TakeNoteActivity)
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog.setContentView(R.layout.dialog_password_custom)
        val mWindow = mDialog.window ?: return
        mWindow.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        mWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val mWindowAttribute = mWindow.attributes
        mWindowAttribute.gravity = Gravity.BOTTOM
        mWindow.attributes = mWindowAttribute
        mDialog.setCancelable(true)
        val noThanks = mDialog.findViewById<Button>(R.id.ButtonNoPass)
        val setPass = mDialog.findViewById<Button>(R.id.ButtonSetPass)
        password = mDialog.findViewById(R.id.EditTextPasswordNote)
        if (passwordNotes != "") {
            mDialog.findViewById<TextView>(R.id.Text1).text = "Thay đổi mật khẩu"
            mDialog.findViewById<TextView>(R.id.TextTitle).text = "Nhập mật khẩu mới"
        }
        var isHidePass = 0
        password!!.setOnTouchListener { view, motionEvent ->
            val DRAWABLE_RIGHT = 2
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                if (motionEvent.rawX >= (password!!.right - password!!.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    isHidePass++
                    password!!.inputType = InputType.TYPE_CLASS_TEXT
                    password!!.setCompoundDrawablesWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.visibility_off,
                        0
                    )
                    if (isHidePass > 1) {
                        password!!.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        password!!.setCompoundDrawablesWithIntrinsicBounds(
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
            password!!.setText("")
            setDataToBundle(isCheckTable)
            setTime(
                dateMilli,
                dateMilli.toInt(),
                "${resources.getString(R.string.note_notification)} ${
                    mBinding.EditTextTitle.text
                }",
                mDatabaseHelper?.getLiveData(Table.type_note)?.value?.first()?.takeNoteID?.toInt()
                    ?: 0,
            )
            mDialog.dismiss()
        }

        setPass.setOnClickListener {
            noThanks.setBackgroundResource(R.drawable.bg_btn_no_pass)
            noThanks.setTextColor(resources.getColor(R.color.Grey))
            setPass.setBackgroundResource(R.drawable.bg_btn_set_pass)
            setPass.setTextColor(Color.BLACK)
            if (password!!.text.isEmpty()) {
                createCustomToast(
                    R.drawable.warning,
                    resources.getString(R.string.pass_not_null)
                )
                Handler().postDelayed({
                    setPass.setBackgroundResource(R.drawable.bg_btn_no_pass)
                    setPass.setTextColor(resources.getColor(R.color.Grey))
                }, 1)
            } else {
                passwordNotes = password!!.text.toString().trim()
                if (!isCheckTable && !update) {
                    setDataToBundle(isCheckTable)
                } else if (!isCheckTable && update) {
                    invalidateOptionsMenu()
                }
                mDialog.dismiss()
            }
        }
        mDialog.show()
    }

    private fun setDataToBundle(insert: Boolean) {
        val notesModel = NotesModel()
        if (mBinding.EditTextTakeNotes.text!!.trim()
                .isNotEmpty() && mBinding.EditTextTitle.text.trim().isNotEmpty()
        ) {
            notesModel.title = mBinding.EditTextTitle.text.toString().trim()
            notesModel.notes = mBinding.EditTextTakeNotes.text.toString().trim()
        } else if (mBinding.EditTextTakeNotes.text!!.trim()
                .isEmpty() && mBinding.EditTextTitle.text.trim().isEmpty()
        ) {
            notesModel.title = getString(R.string.empty_note_content)
            notesModel.notes = ""
        } else if (mBinding.EditTextTakeNotes.text!!.isNotEmpty() && mBinding.EditTextTitle.text!!.isEmpty()) {
            notesModel.title = ""
            notesModel.notes = mBinding.EditTextTakeNotes.text.toString().trim()
        } else if (mBinding.EditTextTitle.text!!.isNotEmpty() && mBinding.EditTextTakeNotes.text!!.isEmpty()) {
            notesModel.notes = ""
            notesModel.title = mBinding.EditTextTitle.text.toString().trim()
        }
        notesModel.timeNote = mBinding.TextViewDateTime.text.toString().trim()
        if (mUri != null) {
            notesModel.image = getPath(mUri)
            pathImage = getPath(mUri).toString()
        } else {
            notesModel.image = ""
            pathImage = ""
        }
        if (passwordNotes != "") {
            notesModel.passwordNote = passwordNotes
        } else {
            notesModel.passwordNote = ""
        }
        notesModel.timeOld = oldTime

        if (dateMilli < 0) {
            notesModel.milliSeconds = -1
            notesModel.timeSet = ""
        } else {
            notesModel.milliSeconds = dateMilli.toInt()
            notesModel.timeSet = timeSet
        }
        if (insert) {
            mDatabaseHelper?.insertNote(notesModel, Table.type_note)
            mDatabaseHelper?.getAllNotes(Table.type_note)
        } else {
            mDatabaseHelper?.insertNote(notesModel, Table.type_archive)
            mDatabaseHelper?.getAllNotes(Table.type_archive)
        }
        openActivity(MainActivity::class.java)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.take_note_add_image -> {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    val arrPermission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(arrPermission, MY_REQUEST_CODE)
                }
            }

            R.id.take_note_delete -> {
                openActivity(MainActivity::class.java)
            }

            R.id.take_note_archive -> {
                if (passwordNotes != "") {
                    setDataToBundle(false)
                } else {
                    setPassDialog(isCheckTable = false, update = false)
                }

            }

            R.id.take_note_set_time -> {
                val datePicker = DatePickerDialog()
                datePicker.setListener(this@TakeNoteActivity)
                datePicker.show(
                    supportFragmentManager,
                    DatePickerDialog::class.java.simpleName.toString()
                )
            }

            R.id.take_note_set_pass -> {
                setPassDialog(isCheckTable = false, update = true)
            }

            R.id.take_note_delete_pass -> {
                val mDialog = Dialog(this@TakeNoteActivity)
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                mDialog.setContentView(R.layout.dialog_password_custom)
                val mWindow = mDialog.window ?: return false
                mWindow.setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT
                )
                mWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                val mWindowAttribute = mWindow.attributes
                mWindowAttribute.gravity = Gravity.BOTTOM
                mWindow.attributes = mWindowAttribute
                mDialog.setCancelable(true)
                mDialog.findViewById<TextView>(R.id.Text1).text =
                    mBinding.EditTextTitle.text.toString().trim()
                mDialog.findViewById<TextView>(R.id.TextTitle).text =
                    "Vui lòng nhập mật khẩu vừa tạo để xóa mật khẩu của ghi chú"
                val noThanks = mDialog.findViewById<Button>(R.id.ButtonNoPass)
                val setPass = mDialog.findViewById<Button>(R.id.ButtonSetPass)
                password =
                    mDialog.findViewById(R.id.EditTextPasswordNote)
                noThanks.text = "Đóng"
                setPass.text = "Gỡ mật khẩu"

                var isHidePass = 0
                password!!.setOnTouchListener { view, motionEvent ->
                    val DRAWABLE_RIGHT = 2
                    if (motionEvent.action == MotionEvent.ACTION_UP) {
                        if (motionEvent.rawX >= (password!!.right - password!!.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                            isHidePass++
                            password!!.inputType = InputType.TYPE_CLASS_TEXT
                            password!!.setCompoundDrawablesWithIntrinsicBounds(
                                0,
                                0,
                                R.drawable.visibility_off,
                                0
                            )
                            if (isHidePass > 1) {
                                password!!.inputType =
                                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                                password!!.setCompoundDrawablesWithIntrinsicBounds(
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
                    password!!.setText("")
                    mDialog.dismiss()
                }

                setPass.setOnClickListener { _ ->
                    noThanks.setBackgroundResource(R.drawable.bg_btn_no_pass)
                    noThanks.setTextColor(resources.getColor(R.color.Grey))
                    setPass.setBackgroundResource(R.drawable.bg_btn_set_pass)
                    setPass.setTextColor(Color.BLACK)
                    if (password!!.text.isEmpty()) {
                        createCustomToast(
                            R.drawable.warning,
                            resources.getString(R.string.pass_not_null)
                        )
                        Handler().postDelayed({
                            setPass.setBackgroundResource(R.drawable.bg_btn_no_pass)
                            setPass.setTextColor(resources.getColor(R.color.Grey))
                        }, 1)
                    } else {
                        if (password!!.text.toString().trim() != passwordNotes) {
                            createCustomToast(
                                R.drawable.warning,
                                "Mật khẩu bạn vừa nhập không đúng"
                            )
                            Handler().postDelayed({
                                setPass.setBackgroundResource(R.drawable.bg_btn_no_pass)
                                setPass.setTextColor(resources.getColor(R.color.Grey))
                            }, 1)
                        } else {
                            passwordNotes = ""
                            mDialog.dismiss()
                            invalidateOptionsMenu()
                        }
                    }
                }
                mDialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated(
        "Deprecated in Java",
        replaceWith = ReplaceWith(
            "super.onBackPressed()",
            "androidx.appcompat.app.AppCompatActivity"
        )
    )
    override fun onBackPressed() {
        setDataToBundle(true)
        setTime(
            dateMilli,
            dateMilli.toInt(),
            "${resources.getString(R.string.note_notification)} ${
                mBinding.EditTextTitle.text
            }",
            mDatabaseHelper?.getLiveData(Table.type_note)?.value?.first()?.takeNoteID
                ?: 0,
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                createCustomToast(
                    R.drawable.warning,
                    PERMISSION_FAIL
                )
            }
        }
    }

    private fun openGallery() {
        val intent = Intent()
        with(mActivityResultLauncher) {
            "image/*".also { intent.type = it }
            Intent.ACTION_GET_CONTENT.also { intent.action = it }
            launch(
                Intent.createChooser(
                    intent,
                    TITLE_INTENT_RESULT_LAUNCHER
                )
            )
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val timePicker = TimePickerDialog()
        timePicker.setListener(this@TakeNoteActivity)
        timePicker.show(supportFragmentManager, "TakePicker")
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm").parse("$p1/${p2 + 1}/$p3 $timeInstance")
        dateInstance = "$p1/${p2 + 1}/$p3"
        timeSet = "$p1/${p2 + 1}/$p3 $timeInstance"
        dateMilli = date!!.time - Calendar.getInstance().timeInMillis
    }

    @SuppressLint("SimpleDateFormat")
    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm").parse("$dateInstance $p1:$p2")
        dateMilli = date!!.time - Calendar.getInstance().timeInMillis
        timeSet = "$dateInstance $p1:$p2"
        if (dateMilli < 0) {
            createCustomToast(
                R.drawable.warning,
                resources.getString(R.string.set_time_notification)
            )
        }
    }

    private fun setTime(timeData: Long, requestCode: Int, message: String, id: Int) {
        if (timeData > 0) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val mIntent = Intent(this@TakeNoteActivity, MyAlarmManager::class.java)
            mIntent.putExtra("message", message)
            mIntent.putExtra("id", id)
            Log.d("date_milli", "$requestCode $id $message")
            val pendingIntent = PendingIntent.getBroadcast(
                this@TakeNoteActivity,
                requestCode,
                mIntent,
                FLAG_MUTABLE
            )
            val timeMilli = System.currentTimeMillis() + timeData
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeMilli, pendingIntent)
        }
    }
}