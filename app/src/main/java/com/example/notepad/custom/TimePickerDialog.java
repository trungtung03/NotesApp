package com.example.notepad.custom;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import androidx.fragment.app.DialogFragment;

import com.example.notepad.R;

import java.util.Calendar;

public class TimePickerDialog extends DialogFragment {

    private android.app.TimePickerDialog.OnTimeSetListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.time_picker, null);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TimePicker timePicker = view.findViewById(R.id.TimePicker);
        timePicker.setIs24HourView(false);
        timePicker.setCurrentHour(hour);
        timePicker.setCurrentMinute(minute);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton("OK", (dialog, id) -> {
                    int selectedHour = timePicker.getCurrentHour();
                    int selectedMinute = timePicker.getCurrentMinute();
                    listener.onTimeSet(timePicker, selectedHour, selectedMinute);
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                    TimePickerDialog.this.getDialog().cancel();
                });

        return builder.create();
    }

    public void setListener(android.app.TimePickerDialog.OnTimeSetListener listener) {
        this.listener = listener;
    }
}
