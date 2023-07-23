package com.example.notepad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notepad.custom.Table;
import com.example.notepad.model.NotesModel;

import java.util.ArrayList;
import java.util.List;

public class NotesDatabaseHelper extends SQLiteOpenHelper {

    private MutableLiveData<List<NotesModel>> liveData = new MutableLiveData<>();
    private MutableLiveData<List<NotesModel>> liveDataRecycle = new MutableLiveData<>();
    private MutableLiveData<List<NotesModel>> liveDataArchive = new MutableLiveData<>();
    private static final String DATABASE_NAME = "notepad5.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NOTE = "note";
    private static final String RECYCLE = "recycle";
    private static final String ARCHIVE = "archive";


    public NotesDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String queryCreateTableNote = "CREATE TABLE " + TABLE_NOTE + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title VARCHAR (500) NOT NULL, " +
                "image VARCHAR(1000) NOT NULL," +
                "time VARCHAR (50) NOT NULL," +
                "notes VARCHAR (5000) NOT NULL," +
                "milliSeconds INTERGER (1000)," +
                "timeSet VARCHAR (50) NOT NULL," +
                "timeOld VARCHAR (50) NOT NULL," +
                "passwordNote VARCHAR (100) NOT NULL" +
                ")";
        String queryCreateTableTrashCan = "CREATE TABLE " + RECYCLE + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title VARCHAR (500) NOT NULL, " +
                "image VARCHAR(1000) NOT NULL," +
                "time VARCHAR (50) NOT NULL," +
                "notes VARCHAR (5000) NOT NULL," +
                "milliSeconds INTERGER (1000)," +
                "timeSet VARCHAR (50) NOT NULL," +
                "timeOld VARCHAR (50) NOT NULL," +
                "passwordNote VARCHAR (100) NOT NULL" +
                ")";
        String queryCreateTableArchive = "CREATE TABLE " + ARCHIVE + " ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title VARCHAR (500) NOT NULL, " +
                "image VARCHAR(1000) NOT NULL," +
                "time VARCHAR (50) NOT NULL," +
                "notes VARCHAR (5000) NOT NULL," +
                "milliSeconds INTERGER (1000)," +
                "timeSet VARCHAR (50) NOT NULL," +
                "timeOld VARCHAR (50) NOT NULL," +
                "passwordNote VARCHAR (100) NOT NULL" +
                ")";
        sqLiteDatabase.execSQL(queryCreateTableNote);
        sqLiteDatabase.execSQL(queryCreateTableTrashCan);
        sqLiteDatabase.execSQL(queryCreateTableArchive);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + RECYCLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ARCHIVE);
        onCreate(sqLiteDatabase);
    }

    public void getAllNotes(String table) {

        List<NotesModel> mListData = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, title, image, time, notes, milliSeconds, timeSet, timeOld, passwordNote FROM '" + table + "' ORDER BY id DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int takeNoteID = cursor.getInt(0);
            String titleNote = cursor.getString(1);
            String imageNote = cursor.getString(2);
            String timeNote = cursor.getString(3);
            String noteContent = cursor.getString(4);
            int milliSeconds = cursor.getInt(5);
            String timeSet = cursor.getString(6);
            String timeOld = cursor.getString(7);
            String passwordNote = cursor.getString(8);

            mListData.add(new NotesModel(takeNoteID, titleNote, imageNote, timeNote, noteContent, milliSeconds, timeSet, timeOld, passwordNote));
            cursor.moveToNext();
        }
        cursor.close();
        if (table.equals(Table.type_note)) {
            liveData.setValue(mListData);
        } else if (table.equals(Table.type_recycle)) {
            liveDataRecycle.setValue(mListData);
        } else if (table.equals(Table.type_archive)) {
            liveDataArchive.setValue(mListData);
        }
    }

    public List<NotesModel> getNotesByID(String table, int id) {

        List<NotesModel> mListData = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, title, image, time, notes, milliSeconds, timeSet, timeOld, passwordNote FROM '" + table + "' WHERE '" + id + "' ORDER BY id DESC", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int takeNoteID = cursor.getInt(0);
            String titleNote = cursor.getString(1);
            String imageNote = cursor.getString(2);
            String timeNote = cursor.getString(3);
            String noteContent = cursor.getString(4);
            int milliSeconds = cursor.getInt(5);
            String timeSet = cursor.getString(6);
            String timeOld = cursor.getString(7);
            String passwordNote = cursor.getString(7);

            mListData.add(new NotesModel(takeNoteID, titleNote, imageNote, timeNote, noteContent, milliSeconds, timeSet, timeOld, passwordNote));
            cursor.moveToNext();
        }
        cursor.close();
        return mListData;
    }

    @SuppressLint("Range")
    public List<NotesModel> searchDataNotes(String keyword, String table) {

        List<NotesModel> mListData = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, title, image, time, notes, milliSeconds, timeSet, timeOld, passwordNote FROM '" + table + "' WHERE title LIKE ?",
                new String[]{"%" + keyword + "%"});

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int takeNoteID = cursor.getInt(cursor.getColumnIndex("id"));
            String titleNote = cursor.getString(cursor.getColumnIndex("title"));
            String imageNote = cursor.getString(cursor.getColumnIndex("image"));
            String timeNote = cursor.getString(cursor.getColumnIndex("time"));
            String noteContent = cursor.getString(cursor.getColumnIndex("notes"));
            int milliSeconds = cursor.getInt(cursor.getColumnIndex("milliSeconds"));
            String timeSet = cursor.getString(cursor.getColumnIndex("timeSet"));
            String timeOld = cursor.getString(cursor.getColumnIndex("timeOld"));
            String passwordNote = cursor.getString(cursor.getColumnIndex("passwordNote"));

            mListData.add(new NotesModel(takeNoteID, titleNote, imageNote, timeNote, noteContent, milliSeconds, timeSet, timeOld, passwordNote));
            cursor.moveToNext();
        }

        cursor.close();

        return mListData;
    }

    public LiveData<List<NotesModel>> getLiveData(String type) {
        if (type.equals(Table.type_note)) {
            return liveData;
        } else if (type.equals(Table.type_recycle)) {
            return liveDataRecycle;
        } else if (type.equals(Table.type_archive)) {
            return liveDataArchive;
        } else {
            return new MutableLiveData<>();
        }
    }

    public void insertNote(NotesModel notesModel, String table) {
        SQLiteDatabase mDatabase = getWritableDatabase();
        mDatabase.execSQL("INSERT INTO '" + table + "' (title, image, time, notes, milliseconds, timeSet, timeOld, passwordNote) VALUES (?,?,?,?,?,?,?,?)",
                new String[]{notesModel.getTitle(), notesModel.getImage(), notesModel.getTimeNote(),
                        notesModel.getNotes(), String.valueOf(notesModel.getMilliSeconds()), notesModel.getTimeSet(), notesModel.getTimeOld(), notesModel.getPasswordNote()});
    }

    public void updateNote(NotesModel notesModel, String table) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE '" + table + "' SET title = ?, image = ?, time = ?, notes = ?, milliseconds = ?, timeSet = ? where id = ?",
                new String[]{notesModel.getTitle(), notesModel.getImage(), notesModel.getTimeNote(),
                        notesModel.getNotes(), notesModel.getMilliSeconds() + "", notesModel.getTimeSet(), notesModel.getTakeNoteID() + ""});
    }

    public void deleteTimeSet(NotesModel notesModel, String table) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE '" + table + "' SET milliSeconds = ?, timeSet = ? where id = ?",
                new String[]{String.valueOf(notesModel.getMilliSeconds()), notesModel.getTimeSet(), notesModel.getTakeNoteID() + ""});
    }

    public void updatePassNote(NotesModel notesModel, String table) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("UPDATE '" + table + "' SET passwordNote = ? where id = ?",
                new String[]{String.valueOf(notesModel.getPasswordNote()), notesModel.getTakeNoteID() + ""});
    }

    public void deleteNoteByID(int NoteID, String table) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM '" + table + "' where id = ?", new String[]{String.valueOf(NoteID)});
    }

    public void deleteAllRecycle() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM recycle");
    }

}
