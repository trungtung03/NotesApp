package com.example.notepad.activity

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.notepad.MainApp
import com.example.notepad.MyAlarmManager
import com.example.notepad.NotesDatabaseHelper
import com.example.notepad.R
import com.example.notepad.base.BaseActivity
import com.example.notepad.custom.Table
import com.example.notepad.databinding.ActivityNotesArchiveBinding
import com.example.notepad.model.NotesModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class NotesArchiveActivity : BaseActivity() {

    private lateinit var mBinding: ActivityNotesArchiveBinding
    private var mUri: Uri? = null
    private var pathImage: String = ""
    private var mDatabaseHelper: NotesDatabaseHelper? = null
    private val MY_REQUEST_CODE = 10
    private var noteID: Int = -1
    private val TITLE_INTENT_RESULT_LAUNCHER = "Select picture"
    private val PERMISSION_FAIL = "Please allow the app to access the photo storage"
    val notesModel = NotesModel()
    var dateMilli: Long = -1
    var timeSet = ""
    var timeOld = ""
    var position = -1
    var position_search = -1

    private val mActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            if (intent == null) {
                return@registerForActivityResult
            } else {
                mUri = intent.data
                Glide.with(this).load(getPath(uri = mUri)).into(mBinding.ImageArchiveNotes)
                mBinding.ImageArchiveNotes.visibility = View.VISIBLE
            }
        }
    }

    override fun setLayout(): View = mBinding.root

    override fun initView() {
        mBinding = ActivityNotesArchiveBinding.inflate(layoutInflater)
        setSupportActionBar(mBinding.ToolbarArchiveNotes)
        actionView()
    }

    private fun actionView() {
        position = intent.getIntExtra("position_archive", -1)
        position_search = intent.getIntExtra("search_archive", -1)

        mDatabaseHelper = MainApp.getInstant()?.mDatabaseHelper
        getData(position, position_search)

        mBinding.ButtonBackArchiveNotes.setOnClickListener { setDataToBundle(Table.type_archive, Table.type_archive) }

        notesModel.takeNoteID = noteID
        notesModel.title = mBinding.TextTitleArchiveNotes.text.toString().trim()
        notesModel.image = pathImage
        notesModel.timeNote = mBinding.TextViewDateTimeArchiveNotes.text.toString().trim()
        notesModel.notes = mBinding.TextArchiveNotes.text.toString().trim()
        notesModel.milliSeconds = dateMilli.toInt()
        notesModel.timeSet = timeSet
        notesModel.timeOld = timeOld
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_archive_note, menu)
        return true
    }

    @SuppressLint("SimpleDateFormat")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.archive_note_add_image -> {
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

            R.id.archive_note_unarchive -> {
                if (timeSet != "") {
                    val timeSet =
                        SimpleDateFormat("dd/MM/yyyy HH:mm").parse(timeSet)
                    val totalMilli = (timeSet?.time ?: 0) - Calendar.getInstance().timeInMillis
                    Log.d("minus_time", totalMilli.toString())
                    if (totalMilli <= 0) {
                        500.toLong().setTime(
                            Random.nextInt(1, 500),
                            "${resources.getString(R.string.old_note_notification_from_archive)} ${
                                mBinding.TextTitleArchiveNotes.text.toString().trim()
                            }"
                        )
                        notesModel.milliSeconds = -1
                        notesModel.timeSet = ""
                        mDatabaseHelper?.insertNote(notesModel, "note")
                        mDatabaseHelper?.getAllNotes(Table.type_note)
                    } else {
                        mDatabaseHelper?.insertNote(notesModel, "note")
                        mDatabaseHelper?.getAllNotes(Table.type_note)
                        dateMilli.setTimeInId(
                            dateMilli.toInt(),
                            "${resources.getString(R.string.note_notification)} ${
                                mBinding.TextTitleArchiveNotes.text.toString().trim()
                            }",
                            mDatabaseHelper?.getLiveData(Table.type_note)?.value?.first()?.takeNoteID
                                ?: 0
                        )
                    }
                } else {
                    setDataToBundle(Table.type_note, Table.type_note)
//                    mDatabaseHelper?.insertNote(notesModel, "note")
//                    mDatabaseHelper?.getAllNotes(Table.type_note)
                }
                mDatabaseHelper?.deleteNoteByID(noteID, "archive")
                mDatabaseHelper?.getAllNotes(Table.type_archive)
                backToMain()
            }

            R.id.archive_note_delete -> {
                setDataToBundle(Table.type_recycle, Table.type_recycle)
                mDatabaseHelper?.deleteNoteByID(noteID, Table.type_archive)
                mDatabaseHelper?.getAllNotes(Table.type_archive)
                backToMain()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun Long.setTime(requestCode: Int, message: String) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val mIntent = Intent(this@NotesArchiveActivity, MyAlarmManager::class.java)
        mIntent.putExtra("message", message)
        val pendingIntent = PendingIntent.getBroadcast(
            this@NotesArchiveActivity,
            requestCode,
            mIntent,
            PendingIntent.FLAG_MUTABLE
        )
        val timeMilli = System.currentTimeMillis() + this
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeMilli, pendingIntent)
    }

    private fun Long.setTimeInId(requestCode: Int, message: String, id: Int) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val mIntent = Intent(this@NotesArchiveActivity, MyAlarmManager::class.java)
        mIntent.putExtra("message", message)
        mIntent.putExtra("id", id)
        val pendingIntent = PendingIntent.getBroadcast(
            this@NotesArchiveActivity,
            requestCode,
            mIntent,
            PendingIntent.FLAG_MUTABLE
        )
        val timeMilli = System.currentTimeMillis() + this
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeMilli, pendingIntent)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getData(position: Int, position_search: Int) {
        if (position >= 0 && position_search < 0) {
            mDatabaseHelper?.getLiveData(Table.type_archive)?.value?.let {
                val archiveNoteActivity = it.getOrNull(position)
                mBinding.TextTitleArchiveNotes.setText(archiveNoteActivity?.title)
                mBinding.TextViewDateTimeArchiveNotes.text = archiveNoteActivity?.timeNote
                mBinding.TextArchiveNotes.setText(archiveNoteActivity?.notes)
                if (archiveNoteActivity?.image!!.isNotEmpty()) {
                    Glide.with(this).load(archiveNoteActivity.image)
                        .into(mBinding.ImageArchiveNotes)
                    pathImage = archiveNoteActivity.image
                    mBinding.ImageArchiveNotes.visibility = View.VISIBLE
                }
                noteID = archiveNoteActivity.takeNoteID
                dateMilli = archiveNoteActivity.milliSeconds.toLong()
                timeSet = archiveNoteActivity.timeSet
                timeOld = archiveNoteActivity.timeOld
            }
        } else if (position < 0 && position_search >= 0) {
            mDatabaseHelper?.getNotesByID(Table.type_archive, position_search).let {
                if (it != null) {
                    for (mListSearch in it) {
                        if (position_search == mListSearch.takeNoteID) {
                            mBinding.TextTitleArchiveNotes.setText(mListSearch?.title)
                            mBinding.TextViewDateTimeArchiveNotes.text = mListSearch?.timeNote
                            mBinding.TextArchiveNotes.setText(mListSearch?.notes)
                            if (mListSearch?.image!!.isNotEmpty()) {
                                Glide.with(this).load(mListSearch.image)
                                    .into(mBinding.ImageArchiveNotes)
                                pathImage = mListSearch.image
                                mBinding.ImageArchiveNotes.visibility = View.VISIBLE
                            }
                            noteID = mListSearch.takeNoteID
                            dateMilli = mListSearch.milliSeconds.toLong()
                            timeSet = mListSearch.timeSet
                            timeOld = mListSearch.timeOld
                            Log.d("time_set", mListSearch.takeNoteID.toString())
                        }
                    }
                }
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
    private fun setDataToBundle(table:String, insert: String) {
        val notesModel = NotesModel()

        notesModel.takeNoteID = (noteID)
        if (mBinding.TextArchiveNotes.text!!.trim()
                .isNotEmpty() && mBinding.TextTitleArchiveNotes.text.trim().isNotEmpty()
        ) {
            notesModel.title = mBinding.TextTitleArchiveNotes.text.toString().trim()
            notesModel.notes = mBinding.TextArchiveNotes.text.toString().trim()
        } else if (mBinding.TextArchiveNotes.text!!.trim()
                .isEmpty() && mBinding.TextTitleArchiveNotes.text.trim().isEmpty()
        ) {
            notesModel.title = getString(R.string.empty_note_content)
            notesModel.notes = ""
        } else if (mBinding.TextArchiveNotes.text!!.isNotEmpty() && mBinding.TextTitleArchiveNotes.text!!.isEmpty()) {
            notesModel.title = ""
            notesModel.notes = mBinding.TextArchiveNotes.text.toString().trim()
        } else if (mBinding.TextTitleArchiveNotes.text!!.isNotEmpty() && mBinding.TextArchiveNotes.text!!.isEmpty()) {
            notesModel.notes = ""
            notesModel.title = mBinding.TextTitleArchiveNotes.text.toString().trim()
        }
        notesModel.timeNote =
            SimpleDateFormat("EEE d MMM yyyy").format(Calendar.getInstance().time).toString().trim()
        notesModel.image = pathImage
        if (mUri != null) {
            notesModel.image = getPath(mUri)
        } else if (pathImage.isEmpty() && mUri == null) {
            notesModel.image = ""
        }
        if (dateMilli < 0) {
            notesModel.milliSeconds = -1
            notesModel.timeSet = ""
        } else {
            notesModel.milliSeconds = dateMilli.toInt()
            notesModel.timeSet = timeSet
        }
        notesModel.timeOld = timeOld
        when (insert) {
            Table.type_archive -> {
                mDatabaseHelper?.updateNote(notesModel, table)
                mDatabaseHelper?.getAllNotes(table)
            }
            Table.type_note -> {
                mDatabaseHelper?.insertNote(notesModel, table)
                mDatabaseHelper?.getAllNotes(table)
            }
            else -> {
                mDatabaseHelper?.insertNote(notesModel, table)
                mDatabaseHelper?.getAllNotes(table)
            }
        }
        backToArchive()
    }

    private fun backToArchive() {
        val mIntent = Intent(this@NotesArchiveActivity, MainActivity::class.java)
        mIntent.putExtra("archive", "archive")
        startActivityForResult(mIntent, 1)
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
        finish()
    }

    private fun backToMain() {
        if (position >= 0 && position_search < 0) {
            backToArchive()
        } else if (position < 0 && position_search >= 0) {
            openActivity(MainActivity::class.java)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        setDataToBundle(Table.type_archive, Table.type_archive)
    }
}