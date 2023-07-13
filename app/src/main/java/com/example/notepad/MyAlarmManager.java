package com.example.notepad;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

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
        Uri soundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.tin_ting);
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

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            Notification.Builder builderApi31 = new Notification.Builder(context, "notepad")
//                    .setSmallIcon(R.drawable.archive)
//                    .setContentTitle("Notepad")
//                    .setContentText("Bạn có một thông báo mới " + mm);
//
//            if (notificationManager.getCurrentInterruptionFilter() == NotificationManager.INTERRUPTION_FILTER_PRIORITY) {
//                builder.setPriority(NotificationCompat.PRIORITY_HIGH);
//            }
//
//            notificationManager.notify(Random.Default.nextInt(1, 500), builderApi31.build());
//        }
    }
}
