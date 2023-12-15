package com.raywenderlich.android.sleepguardian.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.raywenderlich.android.sleepguardian.AlarmService
import com.raywenderlich.android.sleepguardian.receiver.SleepReceiver
import java.util.concurrent.TimeUnit

class StopButtonReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // アラームを停止するための処理
        stopAlarm(context)

    }

    private fun stopAlarm(context: Context?) {
        // アラーム停止の処理を追加
        val stopIntent = Intent(context, AlarmService::class.java)
        context?.stopService(stopIntent)

        // boolean型の値をSleepReceiverに送信
        val broadcastIntent = Intent("ACTION_ALARM_STOPPED")
        broadcastIntent.putExtra("isAlarmStopped", true)
        context?.sendBroadcast(broadcastIntent)
    }
}