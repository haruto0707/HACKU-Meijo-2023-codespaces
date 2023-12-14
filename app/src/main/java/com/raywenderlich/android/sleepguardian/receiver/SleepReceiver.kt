package com.raywenderlich.android.sleepguardian.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.google.android.gms.location.SleepClassifyEvent
import com.google.android.gms.location.SleepSegmentEvent
import com.raywenderlich.android.sleepguardian.AlarmService
import java.util.concurrent.TimeUnit

class SleepReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    if (SleepSegmentEvent.hasEvents(intent)) {
      // スリープセグメントイベントの処理
      val events = SleepSegmentEvent.extractEvents(intent)
      // スリープセグメントイベントの処理を記述
    } else if (SleepClassifyEvent.hasEvents(intent)) {
      // スリープクラス分類イベントの処理
      val events = SleepClassifyEvent.extractEvents(intent)
      // スリープクラス分類イベントの処理を記述
    } else {
      // 何もスリープイベントが受信されなかった場合、一定の遅延後にアラームサービスを再びスケジュール
      scheduleAlarmAgain(context)
    }
  }

  private fun scheduleAlarmAgain(context: Context) {
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({
      val alarmIntent = Intent(context, AlarmService::class.java)
      context.startService(alarmIntent)
    }, TimeUnit.MINUTES.toMillis(1)) // 5分後にスケジュール、必要に応じて調整してください
  }

  companion object {
    private const val TAG = "SLEEP_RECEIVER"

    fun createPendingIntent(context: Context): PendingIntent {
      val intent = Intent(context, SleepReceiver::class.java)
      return PendingIntent.getBroadcast(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
      )
    }
  }
}
