package com.example.notepad;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.notepad.custom.Table;
import com.example.notepad.model.NotesModel;
import java.util.Objects;
import kotlin.random.Random;

public class MyAlarmManager extends BroadcastReceiver {

    @SuppressLint({"MissingPermission", "NotificationPermission", "WrongConstant"})
    @Override
    public void onReceive(Context context, Intent intent) {
        NotesDatabaseHelper mData = Objects.requireNonNull(MainApp.Companion.getInstant()).mDatabaseHelper;
        NotesModel note = new NotesModel();
        note.setTakeNoteID(intent.getIntExtra("id", -1));
        note.setMilliSeconds(-1);
        note.setTimeSet("");
        mData.deleteTimeSet(note, Table.type_note);
        mData.getAllNotes(Table.type_note);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context.getApplicationContext(), "notepad")
                .setSmallIcon(R.drawable.archive)
                .setContentTitle("Notepad")
                .setContentText(intent.getStringExtra("message"))
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context.getApplicationContext());
        notificationManager.notify(Random.Default.nextInt(1, 500), builder.build());
    }
}
