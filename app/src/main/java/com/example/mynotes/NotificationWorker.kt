package com.example.mynotes

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val taskTitle = inputData.getString("taskTitle") ?: "Задача"
        val taskId = inputData.getLong("taskId", -1)

        sendNotification(taskTitle, taskId)

        return Result.success()
    }

    private fun sendNotification(title: String, id: Long) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "task_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Напоминания о задачах",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Use a proper icon if available
            .setContentTitle("Срок выполнения задачи")
            .setContentText("Срок по задаче '$title' подходит к концу!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(id.toInt(), notification)
    }

    companion object {
        fun schedule(context: Context, task: Task) {
            val reminderTime = if (task.reminderTime > 0) task.reminderTime else task.dueDate
            val currentTime = System.currentTimeMillis()
            val delay = reminderTime - currentTime

            if (delay > 0) {
                val data = Data.Builder()
                    .putString("taskTitle", task.title)
                    .putLong("taskId", task.id)
                    .build()

                val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .addTag("task_${task.id}")
                    .build()

                WorkManager.getInstance(context).enqueueUniqueWork(
                    "task_${task.id}",
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
            } else {
                cancel(context, task.id)
            }
        }

        fun cancel(context: Context, taskId: Long) {
            WorkManager.getInstance(context).cancelUniqueWork("task_${taskId}")
        }
    }
}
