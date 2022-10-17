package com.app.subastas.view.util

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.app.subastas.R

class ShowToast {

    fun showToast(context: Context, layout: View, textView: TextView, text: String) {
        setMessageToast(textView, text, context)
        Toast(context).apply {
            duration = Toast.LENGTH_SHORT
            setGravity(Gravity.BOTTOM, 0, 0)
            view = layout
        }.show()
    }

    private fun setMessageToast(textView: TextView, text: String, context: Context) {
        textView.text = context.getString(R.string.toast_text,
            text
        )
    }

}