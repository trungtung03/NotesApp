package com.example.notepad.activity

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_MUTABLE
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.notepad.MainApp
import com.example.notepad.MyAlarmManager
import com.example.notepad.model.NotesModel
import com.example.notepad.NotesDatabaseHelper
import com.example.notepad.R
import com.example.notepad.base.BaseActivity
import com.example.notepad.custom.DatePickerDialog
import com.example.notepad.custom.Table
import com.example.notepad.custom.TimePickerDialog
import com.example.notepad.databinding.ActivityTakeNoteBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

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
    private lateinit var timeSet: String

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

    @SuppressLint("SimpleDateFormat")
    override fun initView() {
        mBinding = ActivityTakeNoteBinding.inflate(layoutInflater)

        setSupportActionBar(mBinding.ToolbarTakeNotes)

        mDatabaseHelper = MainApp.getInstant()?.mDatabaseHelper

        mBinding.ButtonBack.setOnClickListener {
            setDataToBundle("note")
        }

        mBinding.TextViewDateTime.text =
            SimpleDateFormat("EEE d MMM yyyy").format(Calendar.getInstance().time).toString().trim()

        dateInstance = SimpleDateFormat("dd/MM/yyyy").format(Calendar.getInstance().time)
        timeInstance = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_take_note, menu)
        return true
    }

    private fun setDataToBundle(table: String) {
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
        } else {
            notesModel.image = ""
        }

        if (dateMilli < 0) {
            notesModel.milliSeconds = -1
            notesModel.timeSet = ""
        } else {
            notesModel.milliSeconds = dateMilli.toInt()
            notesModel.timeSet = timeSet
        }
        mDatabaseHelper?.insertNote(notesModel, table)
        mDatabaseHelper?.getAllNotes(Table.type_note)
        setTime(
            dateMilli,
            dateMilli.toInt(),
            mBinding.EditTextTitle.text.toString(),
            mDatabaseHelper?.getLiveData(Table.type_note)?.value?.first()?.takeNoteID?.toInt() ?: 0,
        )
        openActivity(
            MainActivity::
            class.java
        )
        finish()
    }

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

            }

            R.id.take_note_set_time -> {
                val datePicker = DatePickerDialog()
                datePicker.setListener(this@TakeNoteActivity)
                datePicker.show(supportFragmentManager, "DatePicker")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated(
        "Deprecated in Java",
        replaceWith = ReplaceWith(
            "super.onBackPressed()",
            "androidx.appcompat.app.AppCompatActivity"
        )
    )
    override fun onBackPressed() {
        super.onBackPressed()
        setDataToBundle("note")
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
            createCustomToast(R.drawable.warning, "Không được chọn giờ nhỏ hơn giờ hiện tại")
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