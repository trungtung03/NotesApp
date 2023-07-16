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
import android.widget.Toast
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
import com.example.notepad.databinding.ActivityDetailedNotesBinding
import com.example.notepad.model.NotesModel
import com.facebook.CallbackManager
import com.facebook.FacebookSdk
import com.facebook.messenger.ShareToMessengerParams
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import java.text.SimpleDateFormat
import java.util.Calendar


class DetailedNotesActivity : BaseActivity(), View.OnClickListener {

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
    lateinit var callback: CallbackManager

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

    override fun initView() {
        mBinding = ActivityDetailedNotesBinding.inflate(layoutInflater)
        setSupportActionBar(mBinding.ToolbarDetailNotes)

        FacebookSdk.setClientToken("4220eefecece56c04f7a9992e3d3de86")
        FacebookSdk.setApplicationId("3516244145330325")
        FacebookSdk.sdkInitialize(this@DetailedNotesActivity);
        callback = CallbackManager.Factory.create();

        position = intent.getIntExtra("position_detail", -1)
        position_search = intent.getIntExtra("search_detail", -1)

        mDatabaseHelper = MainApp.getInstant()?.mDatabaseHelper

        getData(position, position_search)

        mBinding.ButtonBackDetailNotes.setOnClickListener(this)
        mBinding.ButtonShareDetailNotes.setOnClickListener(this)

        notesModel.takeNoteID = noteID
        notesModel.title = mBinding.TextTitleDetailNotes.text.toString().trim()
        notesModel.image = pathImage
        notesModel.timeNote = mBinding.TextViewDateTimeDetailNotes.text.toString().trim()
        notesModel.notes = mBinding.TextDetailNotes.text.toString().trim()
        notesModel.milliSeconds = dateMilli?.toInt() ?: -1
        notesModel.timeSet = timeSet
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail_note, menu)
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
                mDatabaseHelper?.insertNote(notesModel, "recycle")
                mDatabaseHelper?.deleteNoteByID(noteID, "note")
                mDatabaseHelper?.getAllNotes(Table.type_note)
                mDatabaseHelper?.getAllNotes(Table.type_recycle)
                openActivity(MainActivity::class.java)
            }

            R.id.detail_note_archive -> {
                mList.let {
                    val detailsNoteId = it.getOrNull(position)
                    cancelPending(detailsNoteId?.milliSeconds?.toInt() ?: -1)
                }
                mDatabaseHelper?.insertNote(notesModel, "archive")
                mDatabaseHelper?.deleteNoteByID(noteID, "note")
                mDatabaseHelper?.getAllNotes(Table.type_note)
                mDatabaseHelper?.getAllNotes(Table.type_archive)
                openActivity(MainActivity::class.java)
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
                            Log.d("time_set", mListSearch.takeNoteID.toString())
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDataToBundle() {
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
        if (dateMilli!! < 0) {
            notesModel.milliSeconds = -1
            notesModel.timeSet = ""
        } else {
            notesModel.milliSeconds = dateMilli?.toInt() ?: -1
            notesModel.timeSet = timeSet
        }
        mDatabaseHelper?.updateNote(notesModel, "note")
        mDatabaseHelper?.getAllNotes(Table.type_note)
        openActivity(
            MainActivity::
            class.java
        )
        finish()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callback.onActivityResult(requestCode, resultCode, data)
    }
private val REQUEST_CODE_SHARE_TO_MESSENGER = -1
    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.ButtonBackDetailNotes -> {
                setDataToBundle()
            }

            R.id.ButtonShareDetailNotes -> {

            }
        }
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
}