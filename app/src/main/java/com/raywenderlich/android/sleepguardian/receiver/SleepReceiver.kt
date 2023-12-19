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
  var cnt = 1;


  override fun onReceive(context: Context, intent: Intent) {
    val prefs = context.getSharedPreferences("SleepReceiverPrefs", Context.MODE_PRIVATE)
    val isAlarmScheduled = prefs.getBoolean("isAlarmScheduled", false)

    if (!isAlarmScheduled) {
      scheduleAlarm(context)
      prefs.edit().putBoolean("isAlarmScheduled", true).apply()
    }
  }

  private fun handleSleepEvents(events: List<SleepClassifyEvent>, context: Context) {
    for (event in events) {
      Log.d(TAG, "Handling SleepClassifyEvent: $event")
      if (event.motion == 0) {
        scheduleAlarm(context)
      }
    }
  }

  private fun scheduleAlarm(context: Context) {
    Log.d(TAG, "Scheduling alarm")
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({
      val alarmIntent = Intent(context, AlarmService::class.java)
      context.startService(alarmIntent)
      Log.d(TAG, "Alarm scheduled")
    }, TimeUnit.MINUTES.toMillis(1)) // 1分後にスケジュール
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
