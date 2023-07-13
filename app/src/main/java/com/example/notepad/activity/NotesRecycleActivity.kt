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
import com.example.notepad.databinding.ActivityNotesRecycleBinding
import com.example.notepad.model.NotesModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class NotesRecycleActivity : BaseActivity() {

    private lateinit var mBinding: ActivityNotesRecycleBinding
    private var mUri: Uri? = null
    private var pathImage: String = ""
    private var mDatabaseHelper: NotesDatabaseHelper? = null
    private val MY_REQUEST_CODE = 10
    private var noteID: Int = -1
    private val TITLE_INTENT_RESULT_LAUNCHER = "Select picture"
    private val PERMISSION_FAIL = "Please allow the app to access the photo storage"
    var position = -1
    var dateMilli: Long? = -1
    lateinit var mList: List<NotesModel>
    val notesModel = NotesModel()
    var timeSet = ""

    private val mActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val intent = result.data
            if (intent == null) {
                return@registerForActivityResult
            } else {
                mUri = intent.data
                Glide.with(this).load(getPath(uri = mUri)).into(mBinding.ImageRecycleNotes)
                mBinding.ImageRecycleNotes.visibility = View.VISIBLE
            }
        }
    }

    override fun setLayout(): View = mBinding.root

    override fun initView() {
        mBinding = ActivityNotesRecycleBinding.inflate(layoutInflater)
        setSupportActionBar(mBinding.ToolbarRecycleNotes)
        actionView()
    }

    private fun actionView() {
        position = intent.getIntExtra("position_recycle", -1)

        mDatabaseHelper = MainApp.getInstant()?.mDatabaseHelper
        getData(position)

        mBinding.ButtonBackRecycleNotes.setOnClickListener { setDataToBundle() }

        notesModel.takeNoteID = noteID
        notesModel.title = mBinding.TextTitleRecycleNotes.text.toString().trim()
        notesModel.image = pathImage
        notesModel.timeNote = mBinding.TextViewDateTimeRecycleNotes.text.toString().trim()
        notesModel.notes = mBinding.TextRecycleNotes.text.toString().trim()
        notesModel.milliSeconds = dateMilli?.toInt() ?: -1
        notesModel.timeSet = timeSet
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_recycle_note, menu)
        return true
    }

    @SuppressLint("SimpleDateFormat")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.recycle_note_add_image -> {
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

            R.id.recycle_note_restore -> {
                if (timeSet != "") {
                    val timeSet =
                        SimpleDateFormat("dd/MM/yyyy HH:mm").parse(timeSet)
                    val totalMilli = (timeSet?.time ?: 0) - Calendar.getInstance().timeInMillis
                    Log.d("minus_time", totalMilli.toString())
                    if (totalMilli <= 0) {
                        500.toLong().setTime(
                            Random.nextInt(1,500),
                            "Bạn có một ghi chú đã cũ: ${
                                mBinding.TextTitleRecycleNotes.text.toString().trim()
                            }"
                        )
                        notesModel.milliSeconds = -1
                        notesModel.timeSet = ""
                        mDatabaseHelper?.insertNote(notesModel, "note")
                        mDatabaseHelper?.getAllNotes(Table.type_note)
                    } else {
                        mDatabaseHelper?.insertNote(notesModel, "note")
                        mDatabaseHelper?.getAllNotes(Table.type_note)
                        dateMilli?.setTimeInId(
                            dateMilli!!.toInt(),
                            "Đã đến giờ thực hiện công việc: ${
                                mBinding.TextTitleRecycleNotes.text.toString().trim()
                            }",
                            mDatabaseHelper?.getLiveData(Table.type_note)?.value?.first()?.takeNoteID
                                ?: 0
                        )
                    }
                } else {
                    mDatabaseHelper?.insertNote(notesModel, "note")
                    mDatabaseHelper?.getAllNotes(Table.type_note)
                }
                mDatabaseHelper?.deleteNoteByID(noteID, "recycle")
                mDatabaseHelper?.getAllNotes(Table.type_recycle)
                backToMain()
            }

            R.id.recycle_note_forever -> {
                mDatabaseHelper?.deleteNoteByID(noteID, "recycle")
                mDatabaseHelper?.getAllNotes(Table.type_recycle)
                backToMain()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getData(position: Int) {
        mDatabaseHelper?.getLiveData(Table.type_recycle)?.value?.let {
            mList = it
            val recycleNoteActivity = it.getOrNull(position)
            mBinding.TextTitleRecycleNotes.setText(recycleNoteActivity?.title)
            mBinding.TextViewDateTimeRecycleNotes.text = recycleNoteActivity?.timeNote
            mBinding.TextRecycleNotes.setText(recycleNoteActivity?.notes)
            if (recycleNoteActivity?.image!!.isNotEmpty()) {
                Glide.with(this).load(recycleNoteActivity.image).into(mBinding.ImageRecycleNotes)
                pathImage = recycleNoteActivity.image
                mBinding.ImageRecycleNotes.visibility = View.VISIBLE
            }
            noteID = recycleNoteActivity.takeNoteID
            dateMilli = recycleNoteActivity.milliSeconds.toLong()
            timeSet = recycleNoteActivity.timeSet
            Log.d("time_set", timeSet)
        }
    }

    private fun Long.setTime(requestCode: Int, message: String) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val mIntent = Intent(this@NotesRecycleActivity, MyAlarmManager::class.java)
        mIntent.putExtra("message", message)
        val pendingIntent = PendingIntent.getBroadcast(
            this@NotesRecycleActivity,
            requestCode,
            mIntent,
            PendingIntent.FLAG_MUTABLE
        )
        val timeMilli = System.currentTimeMillis() + this
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeMilli, pendingIntent)
    }

    private fun Long.setTimeInId(requestCode: Int, message: String, id: Int) {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val mIntent = Intent(this@NotesRecycleActivity, MyAlarmManager::class.java)
        mIntent.putExtra("message", message)
        mIntent.putExtra("id", id)
        val pendingIntent = PendingIntent.getBroadcast(
            this@NotesRecycleActivity,
            requestCode,
            mIntent,
            PendingIntent.FLAG_MUTABLE
        )
        val timeMilli = System.currentTimeMillis() + this
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeMilli, pendingIntent)
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
            null,
            MediaStore.Images.Media._ID + " = ? ",
            arrayOf(document_id),
            null
        )
        cursor?.moveToFirst()
        val path: String? = cursor?.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
        cursor?.close()
        return path
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                createCustomToast(
                    R.drawable.warning, PERMISSION_FAIL
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
                    intent, TITLE_INTENT_RESULT_LAUNCHER
                )
            )
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDataToBundle() {
        val notesModel = NotesModel()

        notesModel.takeNoteID = (noteID)
        if (mBinding.TextRecycleNotes.text!!.trim()
                .isNotEmpty() && mBinding.TextTitleRecycleNotes.text.trim().isNotEmpty()
        ) {
            notesModel.title = mBinding.TextTitleRecycleNotes.text.toString().trim()
            notesModel.notes = mBinding.TextRecycleNotes.text.toString().trim()
        } else if (mBinding.TextRecycleNotes.text!!.trim()
                .isEmpty() && mBinding.TextTitleRecycleNotes.text.isEmpty()
        ) {
            notesModel.title = getString(R.string.empty_note_content)
            notesModel.notes = ""
        } else if (mBinding.TextRecycleNotes.text!!.isNotEmpty() && mBinding.TextTitleRecycleNotes.text!!.isEmpty()) {
            notesModel.title = ""
            notesModel.notes = mBinding.TextRecycleNotes.text.toString().trim()
        } else if (mBinding.TextTitleRecycleNotes.text!!.isNotEmpty() && mBinding.TextRecycleNotes.text!!.isEmpty()) {
            notesModel.notes = ""
            notesModel.title = mBinding.TextTitleRecycleNotes.text.toString().trim()
        }
        notesModel.timeNote =
            SimpleDateFormat("EEE d MMM yyyy").format(Calendar.getInstance().time).toString().trim()
        notesModel.image = pathImage
        if (mUri != null) {
            notesModel.image = getPath(mUri)
        } else if (pathImage.isEmpty() && mUri == null) {
            notesModel.image = ""
        }
        if (dateMilli!! < 0) {
            notesModel.milliSeconds = -1
            notesModel.timeSet = ""
        } else {
            notesModel.milliSeconds = dateMilli?.toInt() ?: -1
            notesModel.timeSet = timeSet
        }
        mDatabaseHelper?.updateNote(notesModel, "recycle")
        mDatabaseHelper?.getAllNotes(Table.type_recycle)
        backToMain()
    }

    private fun backToMain() {
        val mIntent = Intent(this@NotesRecycleActivity, MainActivity::class.java)
        mIntent.putExtra("recycle", "recycle")
        startActivity(mIntent)
        overridePendingTransition(R.anim.fade_in, R.anim.slide_out)
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        setDataToBundle()
    }
}