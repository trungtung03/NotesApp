package com.example.notepad.activity

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
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
import android.view.View.VISIBLE
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.notepad.MainApp
import com.example.notepad.MyAlarmManager
import com.example.notepad.NotesDatabaseHelper
import com.example.notepad.R
import com.example.notepad.base.BaseActivity
import com.example.notepad.custom.DatePickerDialog
import com.example.notepad.custom.Table
import com.example.notepad.custom.TimePickerDialog
import com.example.notepad.databinding.ActivityDetailedNotesBinding
import com.example.notepad.model.NotesModel
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class DetailedNotesActivity : BaseActivity(), View.OnClickListener,
    android.app.DatePickerDialog.OnDateSetListener, android.app.TimePickerDialog.OnTimeSetListener {

    private lateinit var mBinding: ActivityDetailedNotesBinding
    private var mUri: Uri? = null
    private var mDatabaseHelper: NotesDatabaseHelper? = null
    private var noteID: Int = -1
    private var pathImage: String = ""
    private val MY_REQUEST_CODE = 10
    private val TITLE_INTENT_RESULT_LAUNCHER = "Select picture"
    private val PERMISSION_FAIL = "Please allow the app to access the photo storage"
    val notesModel = NotesModel()
    var dateMilli: Long? = -1
    lateinit var mList: List<NotesModel>
    var position = -1
    var position_search = -1
    var timeSet = ""
    var timeOld = ""
    var isCheckSetTime = false
    private lateinit var dateSet: String
    private lateinit var timeInstance: String
//    lateinit var callback: CallbackManager

    private val mActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            if (intent == null) {
                return@registerForActivityResult
            } else {
                mUri = intent.data
                Glide.with(this).load(getPath(uri = mUri)).into(mBinding.ImageDetailNotes)
                mBinding.ImageDetailNotes.visibility = VISIBLE
            }
        }
    }

    override fun setLayout(): View = mBinding.root

    @SuppressLint("SimpleDateFormat")
    override fun initView() {
        mBinding = ActivityDetailedNotesBinding.inflate(layoutInflater)
        setSupportActionBar(mBinding.ToolbarDetailNotes)

        position = intent.getIntExtra("position_detail", -1)
        position_search = intent.getIntExtra("search_detail", -1)

        mDatabaseHelper = MainApp.getInstant()?.mDatabaseHelper

        getData(position, position_search)

        mBinding.ButtonBackDetailNotes.setOnClickListener(this)

        notesModel.takeNoteID = noteID
        notesModel.title = mBinding.TextTitleDetailNotes.text.toString().trim()
        notesModel.image = pathImage
        notesModel.timeNote = mBinding.TextViewDateTimeDetailNotes.text.toString().trim()
        notesModel.notes = mBinding.TextDetailNotes.text.toString().trim()
        notesModel.milliSeconds = dateMilli?.toInt() ?: -1
        notesModel.timeSet = timeSet
        notesModel.timeOld = timeOld

        timeInstance = SimpleDateFormat("HH:mm").format(Calendar.getInstance().time)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail_note, menu)
        if (timeSet != "") {
            menu?.findItem(R.id.detail_note_set_time)?.title = "Cài lại giờ"
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.detail_note_add_image -> {
                if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        MY_REQUEST_CODE
                    )
                }
            }

            R.id.detail_note_delete -> {
                mList.let {
                    val detailsNoteId = it.getOrNull(position)
                    cancelPending(detailsNoteId?.milliSeconds?.toInt() ?: -1)
                }
                setDataToBundle(Table.type_recycle, Table.type_recycle)
                mDatabaseHelper?.deleteNoteByID(noteID, Table.type_note)
                mDatabaseHelper?.getAllNotes(Table.type_note)
                openActivity(MainActivity::class.java)
            }

            R.id.detail_note_archive -> {
                mList.let {
                    val detailsNoteId = it.getOrNull(position)
                    cancelPending(detailsNoteId?.milliSeconds?.toInt() ?: -1)
                }
                setDataToBundle(Table.type_archive, Table.type_archive)
                mDatabaseHelper?.deleteNoteByID(noteID, Table.type_note)
                mDatabaseHelper?.getAllNotes(Table.type_note)
                openActivity(MainActivity::class.java)
            }

            R.id.detail_note_set_time -> {
                val datePicker = DatePickerDialog()
                datePicker.setListener(this@DetailedNotesActivity)
                datePicker.show(
                    supportFragmentManager,
                    DatePickerDialog::class.java.simpleName.toString()
                )
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SuspiciousIndentation", "SetTextI18n")
    private fun getData(position: Int, position_search: Int) {
        if (position >= 0 && position_search < 0) {
            mDatabaseHelper?.getLiveData(Table.type_note)?.value?.let {
                mList = it
                val detailsNoteActivity = it.getOrNull(position)
                mBinding.TextTitleDetailNotes.setText(detailsNoteActivity?.title)
                mBinding.TextViewDateTimeDetailNotes.text = detailsNoteActivity?.timeNote
                mBinding.TextDetailNotes.setText(detailsNoteActivity?.notes)
                if (detailsNoteActivity?.image!!.isNotEmpty()) {
                    Glide.with(this).load(detailsNoteActivity.image).into(mBinding.ImageDetailNotes)
                    pathImage = detailsNoteActivity.image
                    mBinding.ImageDetailNotes.visibility = VISIBLE
                }
                noteID = detailsNoteActivity.takeNoteID
                dateMilli = detailsNoteActivity.milliSeconds.toLong()
                timeSet = detailsNoteActivity.timeSet
                timeOld = detailsNoteActivity.timeOld
            }
        } else if (position < 0 && position_search >= 0) {
            mDatabaseHelper?.getNotesByID(Table.type_note, position_search).let {
                if (it != null) {
                    for (mListSearch in it) {
                        if (position_search == mListSearch.takeNoteID) {
                            mBinding.TextTitleDetailNotes.setText(mListSearch?.title)
                            mBinding.TextViewDateTimeDetailNotes.text = mListSearch?.timeNote
                            mBinding.TextDetailNotes.setText(mListSearch?.notes)
                            if (mListSearch?.image!!.isNotEmpty()) {
                                Glide.with(this).load(mListSearch.image)
                                    .into(mBinding.ImageDetailNotes)
                                pathImage = mListSearch.image
                                mBinding.ImageDetailNotes.visibility = View.VISIBLE
                            }
                            noteID = mListSearch.takeNoteID
                            dateMilli = mListSearch.milliSeconds.toLong()
                            timeSet = mListSearch.timeSet
                            timeOld = mListSearch.timeOld
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDataToBundle(table: String, insert: String) {
        val notesModel = NotesModel()

        notesModel.takeNoteID = (noteID)
        if (mBinding.TextDetailNotes.text!!.trim()
                .isNotEmpty() && mBinding.TextTitleDetailNotes.text.trim().isNotEmpty()
        ) {
            notesModel.title = mBinding.TextTitleDetailNotes.text.toString().trim()
            notesModel.notes = mBinding.TextDetailNotes.text.toString().trim()
        } else if (mBinding.TextDetailNotes.text!!.trim()
                .isEmpty() && mBinding.TextTitleDetailNotes.text.isEmpty()
        ) {
            notesModel.title = getString(R.string.empty_note_content)
            notesModel.notes = ""
        } else if (mBinding.TextDetailNotes.text!!.isNotEmpty() && mBinding.TextTitleDetailNotes.text!!.isEmpty()) {
            notesModel.title = ""
            notesModel.notes = mBinding.TextDetailNotes.text.toString().trim()
        } else if (mBinding.TextTitleDetailNotes.text!!.isNotEmpty() && mBinding.TextDetailNotes.text!!.isEmpty()) {
            notesModel.notes = ""
            notesModel.title = mBinding.TextTitleDetailNotes.text.toString().trim()
        }
        notesModel.timeNote =
            SimpleDateFormat("EEE d MMM yyyy").format(Calendar.getInstance().time).toString().trim()
        notesModel.image = pathImage
        if (mUri != null) {
            notesModel.image = getPath(mUri)
        } else if (pathImage.isEmpty() && mUri == null) {
            notesModel.image = ""
        }
        Log.d("milli_", dateMilli.toString())
        if (dateMilli!! < 0) {
            notesModel.milliSeconds = -1
            notesModel.timeSet = ""
        } else {
            notesModel.milliSeconds = dateMilli?.toInt() ?: -1
            notesModel.timeSet = timeSet
        }
        notesModel.timeOld = timeOld
        when (insert) {
            Table.type_note -> {
                Log.d("milli_2", dateMilli.toString())
                mDatabaseHelper?.updateNote(notesModel, table)
                mDatabaseHelper?.getAllNotes(table)
                setTime_()
            }

            Table.type_recycle -> {
                mDatabaseHelper?.insertNote(notesModel, table)
                mDatabaseHelper?.getAllNotes(table)
            }

            else -> {
                mDatabaseHelper?.insertNote(notesModel, table)
                mDatabaseHelper?.getAllNotes(table)
            }
        }
        openActivity(MainActivity::class.java)
    }

    private fun setTime_() {
        if (isCheckSetTime) {
            if (timeSet == "") {
                setTime(
                    dateMilli!!.toLong(),
                    dateMilli!!.toInt(),
                    "${resources.getString(R.string.note_notification)} ${
                        mBinding.TextTitleDetailNotes.text.toString()
                    }",
                    noteID
                )
                isCheckSetTime = false
            } else {
                mList.let {
                    val detailsNoteId = it.getOrNull(position)
                    cancelPending(detailsNoteId?.milliSeconds?.toInt() ?: -1)
                }
                setTime(
                    dateMilli!!.toLong(),
                    dateMilli!!.toInt(),
                    "${resources.getString(R.string.note_notification)} ${
                        mBinding.TextTitleDetailNotes.text.toString()
                    }",
                    noteID
                )
                isCheckSetTime = false
            }
        } else {
            cancelPending((dateMilli ?: -1).toInt())
            setTime(
                dateMilli!!.toLong(),
                dateMilli!!.toInt(),
                "${resources.getString(R.string.note_notification)} ${
                    mBinding.TextTitleDetailNotes.text.toString()
                }",
                noteID
            )
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

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ButtonBackDetailNotes -> {
                setDataToBundle(Table.type_note, Table.type_note)
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        setDataToBundle(Table.type_note, Table.type_note)
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

    private fun cancelPending(requestCode: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val mIntent = Intent(this@DetailedNotesActivity, MyAlarmManager::class.java)
        val pendingCancel = PendingIntent.getBroadcast(
            this@DetailedNotesActivity,
            requestCode,
            mIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        alarmManager.cancel(pendingCancel)
        pendingCancel.cancel()
    }

    @SuppressLint("SimpleDateFormat")
    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val timePicker = TimePickerDialog()
        timePicker.setListener(this@DetailedNotesActivity)
        timePicker.show(supportFragmentManager, "TakePicker")
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm").parse("$p1/${p2 + 1}/$p3 $timeInstance")
        dateSet = "$p1/${p2 + 1}/$p3"
        timeSet = "$p1/${p2 + 1}/$p3 $timeInstance"
        dateMilli = date!!.time - Calendar.getInstance().timeInMillis
        isCheckSetTime = true
    }

    @SuppressLint("SimpleDateFormat")
    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm").parse("$dateSet $p1:$p2")
        dateMilli = date!!.time - Calendar.getInstance().timeInMillis
        timeSet = "$dateSet $p1:$p2"
        if (dateMilli!! < 0) {
            createCustomToast(
                R.drawable.warning,
                resources.getString(R.string.set_time_notification)
            )
        }
        isCheckSetTime = true
    }

    private fun setTime(timeData: Long, requestCode: Int, message: String, id: Int) {
        if (timeData > 0) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val mIntent = Intent(this@DetailedNotesActivity, MyAlarmManager::class.java)
            mIntent.putExtra("message", message)
            mIntent.putExtra("id", id)
            Log.d("date_milli", "$requestCode $id $message")
            val pendingIntent = PendingIntent.getBroadcast(
                this@DetailedNotesActivity,
                requestCode,
                mIntent,
                PendingIntent.FLAG_MUTABLE
            )
            val timeMilli = System.currentTimeMillis() + timeData
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeMilli, pendingIntent)
        }
    }
}