package com.app.subastas.view.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo


class Connectivity() {
    fun verifyConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        return activeNetwork?.isConnectedOrConnecting == true
    }

    fun isOnlineNet(): Boolean {
        try {
            val p: Process = Runtime.getRuntime().exec("ping -c 1 www.google.es")
            val i: Int = p.waitFor()
            return (i == 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}