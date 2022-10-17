package com.app.subastas.view.util

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import com.app.subastas.R
import java.util.*

class TimePickerFragment(val listener: (String) -> Unit):
    DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        listener("${hourOfDay}:${minute}")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR)
        val minute = c.get(Calendar.MINUTE)
        return TimePickerDialog(activity as Context, R.style.PickerTheme, this, hour, minute, false)
    }
}