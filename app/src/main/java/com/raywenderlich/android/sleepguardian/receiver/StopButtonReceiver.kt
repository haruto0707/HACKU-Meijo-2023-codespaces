package com.raywenderlich.android.sleepguardian.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.raywenderlich.android.sleepguardian.AlarmService

class StopButtonReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // アラームを停止するための処理
        stopAlarm(context)

        // 5分後に SleepReceiver を起動
        scheduleSleepReceiver(context, intent)
    }

    private fun stopAlarm(context: Context?) {
        // アラーム停止の処理を追加
        val stopIntent = Intent(context, AlarmService::class.java)
        context?.stopService(stopIntent)
    }

    private fun scheduleSleepReceiver(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("key")
        // motion の値をログに出力
        Log.d("StopButtonReceiver", "スリープレシーバーを1分後にスケジュールしました。 $message")
    }
}
