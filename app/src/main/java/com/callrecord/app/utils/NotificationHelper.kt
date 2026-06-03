package com.callrecord.app.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.callrecord.app.R
import com.callrecord.app.service.RecorderService

object NotificationHelper {
    private const val CHANNEL_ID = "call_recording"
    private const val CHANNEL_NAME = "Call Recording"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Shows call recording status"
            manager.createNotificationChannel(channel)
        }
    }

    fun buildRecordingNotification(context: Context): Notification {
        val stopIntent = Intent(context, RecorderService::class.java).apply {
            action = RecorderService.ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            context,
            2,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or pendingIntentImmutableFlag()
        )

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.recording_notification_title))
            .setContentText(context.getString(R.string.recording_notification_text))
            .setOngoing(true)
            .addAction(0, context.getString(R.string.stop_recording), stopPendingIntent)
            .build()
    }

    private fun pendingIntentImmutableFlag(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            0
        }
    }
}
