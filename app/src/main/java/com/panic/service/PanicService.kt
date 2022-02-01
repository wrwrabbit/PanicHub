package com.panic.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import com.panic.BuildConfig
import com.panic.R
import com.panic.ext.printData
import com.panic.ext.toastLong
import info.guardianproject.panic.Panic
import info.guardianproject.panic.PanicTrigger
import info.guardianproject.panic.PanicUtils

class PanicService : LifecycleService() {

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
        if (!Panic.isTriggerIntent(intent)) {
            stopForeground(STOP_FOREGROUND_REMOVE)
            return super.onStartCommand(intent, flags, startId)
        }

        Log.d(TAG, "onStartCommand:")
        intent?.extras?.printData(TAG)
        PanicTrigger.sendTrigger(this)
        toastLong("${getString(R.string.app_name)} onStartCommand!")

        stopForeground(STOP_FOREGROUND_REMOVE)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent): IBinder? {
        super.onBind(p0)
        return null
    }

    companion object {
        const val TAG: String = "PanicService"
        private const val NOTIFICATION_ID = 412
        private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".MUSIC_NOTIFICATION_CHANNEL_ID"
    }


}
