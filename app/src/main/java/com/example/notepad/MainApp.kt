package com.example.notepad

import android.app.Application
import android.os.Build.VERSION_CODES.N
import com.example.notepad.custom.Table
import com.example.quanlychitieu.local.Preference

class MainApp : Application() {
    lateinit var mDatabaseHelper : NotesDatabaseHelper
    var preference: Preference? = null
    override fun onCreate() {
        super.onCreate()
        instant = this
        preference = Preference.buildInstance(this)
        if (preference?.firstInstall == false) {
            preference?.firstInstall = true
            preference?.setValueCoin(10)
        }
        mDatabaseHelper = NotesDatabaseHelper(applicationContext)
        mDatabaseHelper.getAllNotes(Table.type_note)
        mDatabaseHelper.getAllNotes(Table.type_recycle)
        mDatabaseHelper.getAllNotes(Table.type_archive)
    }

    companion object {
        private var  instant: MainApp? = null
        fun getInstant(): MainApp? {
            if (instant == null) {
                instant = MainApp()
            }
            return instant
        }
    }


}