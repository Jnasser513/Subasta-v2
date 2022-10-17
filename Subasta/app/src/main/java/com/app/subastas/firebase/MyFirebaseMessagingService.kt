package com.app.subastas.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {

    private val TAG = "FirebaseService"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "onMessageReceived: Message received from: "+ remoteMessage.from)

        Log.d(TAG, remoteMessage.notification!!.body.toString())
        Log.d(TAG, remoteMessage.data.toString())

        /*if(remoteMessage.notification != null) {
            val title = remoteMessage.notification!!.title
            val body = remoteMessage.notification!!.body

            if(title!!.contains("Prueba")) {
                Log.d("PRUEBAPUSH", "PUSHSEND")
                val intent = Intent(this, HomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.putExtra("push", "push")
                intent.putExtra("idLote", 21)
                startActivity(intent)
            }
        } else {
            Log.d("NO ENTRO", "NO ENTRO")
        }*/
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        Log.d(TAG, "onDeleteMessaged: called")
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.d(TAG, "onNewToken: called")
    }

    /*override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val from = remoteMessage.from
        Log.d(TAG, "Mensaje recibido de: $from")
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Notificación: " + remoteMessage.notification!!.body)
            remoteMessage.notification!!.title?.let {
                remoteMessage.notification!!
                    .body?.let { it1 ->
                        mostrarNotificacion(
                            it, it1
                        )
                    }
            }
        }
        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Data: " + remoteMessage.data)
        }
    }

    private fun mostrarNotificacion(title: String, body: String) {
        var intent: Intent? = null
        if (body.contains("Prueba")) {
            intent = Intent(this, HomeActivity::class.java)
        }
        /*if (body == "AppIntro") {
            intent = Intent(this, AppIntroGalery::class.java)
        }
        if (body == "Menu") {
            intent = Intent(this, TabsActivity::class.java)
        }*/
        intent!!.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val soundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setSound(soundUri)
            .setContentIntent(pendingIntent)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, notificationBuilder.build())
    }*/

}