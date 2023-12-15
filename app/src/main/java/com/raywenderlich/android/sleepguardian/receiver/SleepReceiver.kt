package com.raywenderlich.android.sleepguardian.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.SleepClassifyEvent
import com.google.android.gms.location.SleepSegmentEvent
import com.raywenderlich.android.sleepguardian.AlarmService
import java.util.concurrent.TimeUnit

class SleepReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    if ("ACTION_ALARM_STOPPED" == intent.action) {
      // ブロードキャストからboolean型の値を取得
      val isAlarmStopped = intent.getBooleanExtra("isAlarmStopped", false)

      if (isAlarmStopped) {
        if (SleepSegmentEvent.hasEvents(intent)) {
          val events = SleepSegmentEvent.extractEvents(intent)

          Log.d(TAG, "Logging SleepSegmentEvents")
          for (event in events) {
            Log.d(TAG,
              "${event.startTimeMillis} to ${event.endTimeMillis} with status ${event.status}")
          }
        } else if (SleepClassifyEvent.hasEvents(intent)) {
          val events = SleepClassifyEvent.extractEvents(intent)

          Log.d(TAG, "Logging SleepClassifyEvents")
          for (event in events) {
            Log.d(TAG,
              "Confidence: ${event.confidence} - Light: ${event.light} - Motion: ${event.motion}")

            if (event.motion == 1) {
              scheduleAlarm(context)
            }
          }
        }
      }
    }
  }

  private fun scheduleAlarm(context: Context) {
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({
      val alarmIntent = Intent(context, AlarmService::class.java)
      context.startService(alarmIntent)
    }, TimeUnit.MINUTES.toMillis(5)) // 5分後にスケジュール、必要に応じて調整してください
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
