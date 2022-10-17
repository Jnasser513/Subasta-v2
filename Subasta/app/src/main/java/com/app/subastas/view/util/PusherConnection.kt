package com.app.subastas.view.util

import com.pusher.client.Pusher
import com.pusher.client.PusherOptions

class PusherConnection {

    fun setConnection(): Pusher{
        val options = PusherOptions()
        options.setCluster("us2");
        return Pusher("ea72dec994bb192d9e9b", options)
    }

}