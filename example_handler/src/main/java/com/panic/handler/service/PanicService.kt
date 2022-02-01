package com.panic.handler.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.panic.handler.BuildConfig
import com.panic.handler.R
import com.panic.handler.ext.toastLong
import info.guardianproject.panic.PanicTrigger

class PanicService : Service() {

    private val context: Context by lazy {
        this
    }

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
    }

    private fun buildNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        return NotificationCompat.Builder(application, NOTIFICATION_CHANNEL_ID)
            .setColor(ContextCompat.getColor(context, R.color.black))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            // https://stackoverflow.com/a/45920861/2425851
            .setChannelId(NOTIFICATION_CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentTitle("title")
            .setContentText("subtitle")
            .build()
    }

    /**
     * Creates Notification Channel. This is required in Android O+ to display notifications.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
            val name = application.getString(R.string.service_name)
            val description = application.getString(R.string.service_description)

            // https://stackoverflow.com/a/45920861/2425851
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT)
                .apply {
                    setDescription(description)
                    setShowBadge(false)
                    setSound(null, null)
                }
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification())
        Log.d(TAG, "onStartCommand:")
        PanicTrigger.sendTrigger(this)
        toastLong("${getString(R.string.app_name)} onStartCommand!")

        stopForeground(STOP_FOREGROUND_REMOVE)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    companion object {
        const val TAG: String = "PanicService"
        private const val NOTIFICATION_ID = 412
        private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".MUSIC_NOTIFICATION_CHANNEL_ID"
    }


}
