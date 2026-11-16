package com.example.mynotes.auth.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.core.app.NotificationCompat

object NotificationHelper {
    private const val CHANNEL_ID = "auth_2fa_channel"

    fun sendTwoFactorCodeNotification(context: Context, code: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "2FA Authentication",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Коды подтверждения для входа в приложение"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_lock)
            .setContentTitle("Код подтверждения входа")
            .setContentText("Ваш код для входа: $code")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        try {
            notificationManager.notify(1001, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Toast.makeText(context, "Ваш код для входа: $code", Toast.LENGTH_LONG).show()
    }
}
