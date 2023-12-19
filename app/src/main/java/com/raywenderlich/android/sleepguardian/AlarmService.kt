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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.raywenderlich.android.sleepguardian.receiver.StopButtonReceiver
import java.util.Random

class AlarmService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var notificationManager: NotificationManagerCompat
    private var uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // アラーム音を再生
        mediaPlayer = MediaPlayer.create(this, uri)
        mediaPlayer.isLooping = true
        mediaPlayer.start()

        // 通知を表示
        showNotification()

        // サービスをフォアグラウンドで実行
        startForeground(NOTIFICATION_ID, createNotification())

        return START_STICKY
    }

    private fun showNotification() {
        notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
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
    }

    private fun createNotificationChannel() {
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
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.stop()
        // 通知をキャンセル
        notificationManager.cancel(NOTIFICATION_ID)

        // SleepReceiverをトリガーするインテントを送信
        val triggerIntent = Intent("com.raywenderlich.android.ACTION_TRIGGER_SLEEP_RECEIVER")
        sendBroadcast(triggerIntent)  // context?.sendBroadcast(triggerIntent) から変更
    }

    companion object {
        private const val CHANNEL_ID = "Sleep Guardian"
        private val NOTIFICATION_ID = Random().nextInt()
    }
}
