package com.raywenderlich.android.sleepguardian.receiver

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.raywenderlich.android.sleepguardian.AlarmService
import com.raywenderlich.android.sleepguardian.DestinationActivity
import com.raywenderlich.android.sleepguardian.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val serviceIntent = Intent(context, AlarmService::class.java)
        context?.startService(serviceIntent)

        val i = Intent(context, DestinationActivity::class.java)
        intent!!.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            i,
            PendingIntent.FLAG_UPDATE_CURRENT
        )


//        val builder = NotificationCompat.Builder(context!!, "foxandroid")
//            .setSmallIcon(R.drawable.ic_launcher_background)
//            .setContentTitle("Foxandroid Alarm Manager")
//            .setContentText("Subscribe for more android related content")
//            .setAutoCancel(true)
//            .setDefaults(NotificationCompat.DEFAULT_ALL)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setContentIntent(pendingIntent)
//
//        val notificationManager = NotificationManagerCompat.from(context)
//        notificationManager.notify(123, builder.build())
    }
}
