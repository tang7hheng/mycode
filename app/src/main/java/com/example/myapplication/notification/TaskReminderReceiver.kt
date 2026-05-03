package com.example.myapplication.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.myapplication.MainActivity

class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra("task_id", -1)
        val taskTitle = intent.getStringExtra("task_title") ?: "任务提醒"
        val taskDescription = intent.getStringExtra("task_description") ?: ""

        showNotification(context, taskId, taskTitle, taskDescription)
    }

    private fun showNotification(
        context: Context,
        taskId: Long,
        title: String,
        description: String
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 创建通知渠道（Android 8.0+）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "任务提醒",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                this.description = "任务截止时间提醒"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 点击通知打开应用
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("task_id", taskId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 构建通知
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(taskId.toInt(), notification)
    }

    companion object {
        const val CHANNEL_ID = "task_reminder_channel"
    }
}
