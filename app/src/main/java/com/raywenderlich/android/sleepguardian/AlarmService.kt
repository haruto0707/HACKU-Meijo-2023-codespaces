// AlarmService.kt

package com.raywenderlich.android.sleepguardian

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.raywenderlich.android.sleepguardian.receiver.StopButtonReceiver

class AlarmService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var notificationManager: NotificationManagerCompat

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            // アラーム音を再生
            mediaPlayer = MediaPlayer.create(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            mediaPlayer.isLooping = true
            mediaPlayer.start()

            // 通知を表示
            showNotification()

            // サービスをフォアグラウンドで実行
            startForeground(NOTIFICATION_ID, createNotification())
        } catch (e: Exception) {
            Log.e(TAG, "Error in onStartCommand: ${e.message}", e)
        }

        return START_STICKY
    }

    private fun showNotification() {
        try {
            notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(NOTIFICATION_ID, createNotification())
        } catch (e: Exception) {
            Log.e(TAG, "Error in showNotification: ${e.message}", e)
        }
    }

    private fun createNotification(): Notification {
        try {
            createNotificationChannel()

            // ボタンがクリックされたときにブロードキャストされるインテント
            val stopButtonIntent = Intent(this, StopButtonReceiver::class.java).apply {
                putExtra("MESSAGE", "アラーム停止")
            }
            val stopPendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                stopButtonIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            return NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Sleep Guardian")
                .setContentText("アラームが鳴りました")
                .setAutoCancel(false)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(stopPendingIntent)  // 通知をクリックした際のインテントを変更
                .addAction(0, "アラーム停止", stopPendingIntent) // ボタンを追加
                .build()
        } catch (e: Exception) {
            Log.e(TAG, "Error in createNotification: ${e.message}", e)
            throw e
        }
    }

    private fun createNotificationChannel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = "Sleep GuardianReminderChannel"
                val descriptionText = "Channel For Alarm Manager"
                val importance = NotificationManager.IMPORTANCE_HIGH
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    name,
                    importance
                ).apply {
                    description = descriptionText
                }
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in createNotificationChannel: ${e.message}", e)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        try {
            super.onDestroy()
            mediaPlayer.stop()
            // 通知をキャンセル
            notificationManager.cancel(NOTIFICATION_ID)
        } catch (e: Exception) {
            Log.e(TAG, "Error in onDestroy: ${e.message}", e)
        }
    }

    companion object {
        private const val TAG = "AlarmService"
        private const val CHANNEL_ID = "com.raywenderlich.android.sleepguardian.SleepGuardianChannel"
        private const val NOTIFICATION_ID = 1
    }
}
