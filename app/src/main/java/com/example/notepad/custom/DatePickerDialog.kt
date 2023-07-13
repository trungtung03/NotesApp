package com.example.notepad.custom

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.DialogInterface
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.example.notepad.R
import java.util.Calendar

class DatePickerDialog : DialogFragment() {
    private lateinit var listener: OnDateSetListener

    @SuppressLint("UseGetLayoutInflater", "InflateParams", "MissingInflatedId")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layoutInflater = layoutInflater
        val view = layoutInflater.inflate(R.layout.date_picker, null)
        val datePicker = view.findViewById<DatePicker>(R.id.DatePicker)
        val builder = AlertDialog.Builder(activity)
        builder.setView(view)
            .setPositiveButton("OK") { _: DialogInterface?, _: Int ->
                val selectDay = datePicker.dayOfMonth
                val selectMonth = datePicker.month
                val selectYear = datePicker.year
                listener.onDateSet(datePicker, selectDay, selectMonth, selectYear)
            }
            .setNegativeButton(
                "Cancel"
            ) { _: DialogInterface?, _: Int ->
                this@DatePickerDialog.dialog?.cancel()
            }

        return builder.create()
    }

    fun setListener(listener: OnDateSetListener) {
        this.listener = listener
    }
}