package com.raywenderlich.android.sleepguardian

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.raywenderlich.android.sleepguardian.databinding.FragmentHomeBinding
import com.raywenderlich.android.sleepguardian.receiver.AlarmReceiver
import java.util.Calendar

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class Home : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding: FragmentHomeBinding
    private lateinit var picker: MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var alarmManager : AlarmManager
    private lateinit var  pendingIntent: PendingIntent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        binding = FragmentHomeBinding.inflate(layoutInflater)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val name: CharSequence = "Sleep GuardianReminderChannel"
            val description = "Channnel For Alarm Manager"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("Sleep Guardian", name, importance)
            channel.description = description

            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = binding.root

        binding.selectTimeBtn.setOnClickListener {
            showTimePicker()
        }
        binding.setAlarmBtn.setOnClickListener {
            // ここにアラームを設定する処理を追加
            setAlarm()
        }
        binding.cancelAlarmBtn.setOnClickListener {
            // ここにアラームをキャンセルする処理を追加
            cancelAlarm()
        }

        return view
    }

    private fun cancelAlarm() {

        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        alarmManager.cancel(pendingIntent)

        Toast.makeText(requireContext(),"アラームをキャンセルしました",Toast.LENGTH_LONG).show()

    }

    private fun setAlarm() {
        // AlarmManagerを取得
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // アラームをセットするためのIntent
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // アラームを設定
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

        Toast.makeText(requireContext(), "アラームをセットしました", Toast.LENGTH_SHORT).show()
    }




    private fun showTimePicker() {
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("時間をセットしてください")  // タイポ修正
            .build()

        picker.show(parentFragmentManager, "Sleep Guardian")

        picker.addOnPositiveButtonClickListener {
            val formattedTime = if (picker.hour > 12) {
                String.format("%02d", picker.hour - 12) + ":" + String.format(
                    "%02d",
                    picker.minute
                ) + "PM"
            } else {
                String.format("%02d", picker.hour) + ":" + String.format(
                    "%02d",
                    picker.minute
                ) + "AM"
            }

            binding.selectedTime.text = formattedTime

            calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = picker.hour
            calendar[Calendar.MINUTE] = picker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Home().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
