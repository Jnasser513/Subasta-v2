package com.app.subastas.view.util

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast


class DownLoadCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val action = intent.action
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
            Toast.makeText(context, "Descarga completada", Toast.LENGTH_LONG).show()
        }
    }
}