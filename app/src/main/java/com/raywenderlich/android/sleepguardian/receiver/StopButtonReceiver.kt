package com.raywenderlich.android.sleepguardian.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.raywenderlich.android.sleepguardian.AlarmService

class StopButtonReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // アラームを停止するための処理
        val stopIntent = Intent(context, AlarmService::class.java)
        context?.stopService(stopIntent)
    }
}