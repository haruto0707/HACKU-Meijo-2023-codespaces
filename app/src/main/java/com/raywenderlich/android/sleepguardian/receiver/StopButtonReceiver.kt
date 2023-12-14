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

        // 5分後にSleepReceiverを起動
        scheduleSleepReceiver(context)
    }

    private fun stopAlarm(context: Context?) {
        // アラーム停止の処理を追加
        val stopIntent = Intent(context, AlarmService::class.java)
        context?.stopService(stopIntent)
    }

    private fun scheduleSleepReceiver(context: Context?) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val sleepReceiverIntent = Intent(context, SleepReceiver::class.java)

            // SleepReceiverを起動
            context?.sendBroadcast(sleepReceiverIntent)

            Log.d("SleepReceiver", "Scheduled SleepReceiver after 5 minutes.")
        }, TimeUnit.MINUTES.toMillis(1))
    }


}
