package com.raywenderlich.android.sleepguardian

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import android.widget.ToggleButton
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

        initialToggleButtons()
    }

    private fun initialToggleButtons() {
        binding.toggleSunday.isChecked = true
        updateToggleButtonStyle(binding.toggleSunday, true)

        val toggleButtonListener = View.OnClickListener{view ->

            val selectedToggleButton = view as ToggleButton
            val day = getDayKeyFromToggleButtonId(selectedToggleButton.id)
            if(selectedToggleButton.isChecked)
            {
                deselectOtherToggleButtons(selectedToggleButton.id)
                updateToggleButtonStyle(selectedToggleButton,true)
                updateDisplay(day)
            }
            else
            {
                updateToggleButtonStyle(selectedToggleButton,false)
            }
        }

        binding.toggleSunday.setOnClickListener(toggleButtonListener)
        binding.toggleMonday.setOnClickListener(toggleButtonListener)
        binding.toggleTuesday.setOnClickListener(toggleButtonListener)
        binding.toggleWednesday.setOnClickListener(toggleButtonListener)
        binding.toggleThursday.setOnClickListener(toggleButtonListener)
        binding.toggleFriday.setOnClickListener(toggleButtonListener)
        binding.toggleSaturday.setOnClickListener(toggleButtonListener)

    }

    private fun getDayKeyFromToggleButtonId(toggleButtonId: Int): String {
        return when (toggleButtonId) {
            R.id.toggleSunday -> "toggleSunday"
            R.id.toggleMonday -> "toggleMonday"
            R.id.toggleTuesday -> "toggleTuesday"
            R.id.toggleWednesday -> "toggleWednesday"
            R.id.toggleThursday -> "toggleThursday"
            R.id.toggleFriday -> "toggleFriday"
            R.id.toggleSaturday -> "toggleSaturday"
            else -> ""
        }
    }

    private fun updateDisplay(selectedToggleButton: String?) {
        val prefs2 = requireContext().getSharedPreferences("AlarmPreferences", MODE_PRIVATE)
        val alarmTime = prefs2.getLong(selectedToggleButton, -1L)

        if (alarmTime != -1L) {
            val alarmCalendar = Calendar.getInstance().apply {
                timeInMillis = alarmTime
            }
            val hour = alarmCalendar.get(Calendar.HOUR_OF_DAY)
            val minute = alarmCalendar.get(Calendar.MINUTE)
            val amPm = if (hour < 12) "AM" else "PM"
            val formattedHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour

            val formattedTime = String.format("%02d:%02d %s", formattedHour, minute, amPm)
            binding.selectedTime.text = formattedTime
            Log.d("updateDisplay","if")
        } else {
            binding.selectedTime.text = "XX:XX"
            Log.d("updateDisplay","else")
        }
    }



    private fun deselectOtherToggleButtons(selectedId: Int) {

        val toggleisChecked = requireContext().getSharedPreferences("toggleisChecked",MODE_PRIVATE)
        val editor = toggleisChecked.edit()

        val allToggleButton = listOf(
            "toggleSunday" to binding.toggleSunday,
            "toggleMonday" to binding.toggleMonday,
            "toggleTuesday" to binding.toggleTuesday,
            "toggleWednesday" to binding.toggleWednesday,
            "toggleThursday" to binding.toggleThursday,
            "toggleFriday" to binding.toggleFriday,
            "toggleSaturday" to binding.toggleSaturday
        )

        for ((key, toggleButton) in allToggleButton) {
            if (toggleButton.id != selectedId) {
                toggleButton.isChecked = false
                updateToggleButtonStyle(toggleButton, false)
                editor.putBoolean(key, false)
            } else {
                updateToggleButtonStyle(toggleButton, true)
                editor.putBoolean(key, true)
            }
        }

        editor.apply()
        getCheckedDates()
    }

    fun getCheckedDates(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("toggleisChecked", Context.MODE_PRIVATE)
        val allEntries = sharedPreferences.all

        for ((key, value) in allEntries) {
            if (value is Boolean && value) {
                return key
            }

        }

        return null
    }



    private fun updateToggleButtonStyle(toggleButton: ToggleButton, isSelected: Boolean) {

        if(isSelected)
        {
            toggleButton.setBackgroundResource(R.color.selectedColor)
            toggleButton.setTextColor(resources.getColor(R.color.selectedTextColor))
        }
        else
        {
            toggleButton.setBackgroundResource(R.color.unselectedColor)
            toggleButton.setTextColor(resources.getColor(R.color.unselectedTextColor))
        }

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

        binding.toggleSunday.isChecked = true
        updateDisplay(getCheckedDates())

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

        val day = getCheckedDates()
        val prefs2 = requireContext().getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)

        val alarmTime = prefs2.getLong(day,-1L)

        // AlarmManagerを取得
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // アラームをセットするためのIntent

        if(day != null)
        {
            val alarmCalendar = Calendar.getInstance().apply {
                timeInMillis = alarmTime
                setToNextWeekday(day)
            }

            when(day)
            {
                "toggleSunday" -> {
                    val intentSunday = Intent(requireContext(), AlarmReceiver::class.java)
                    val pendingSunday = PendingIntent.getBroadcast(
                        requireContext(),
                        0,
                        intentSunday,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        alarmCalendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY * 7,
                        pendingSunday
                    )
                }
                "toggleMonday" -> {
                    val intentMonday = Intent(requireContext(), AlarmReceiver::class.java)
                    val pendingMonday = PendingIntent.getBroadcast(
                        requireContext(),
                        1,
                        intentMonday,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        alarmCalendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY * 7,
                        pendingMonday
                    )
                }
                "toggleTuesday" -> {
                    val intentTuesday = Intent(requireContext(), AlarmReceiver::class.java)
                    val pendingTuesday = PendingIntent.getBroadcast(
                        requireContext(),
                        2,
                        intentTuesday,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        alarmCalendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY * 7,
                        pendingTuesday
                    )
                }
                "toggleWednesday" -> {
                    val intentWednesday = Intent(requireContext(), AlarmReceiver::class.java)
                    val pendingWednesday = PendingIntent.getBroadcast(
                        requireContext(),
                        3,
                        intentWednesday,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        alarmCalendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY * 7,
                        pendingWednesday
                    )
                }
                "toggleThursday" -> {
                    val intentThursday = Intent(requireContext(), AlarmReceiver::class.java)
                    val pendingThursday = PendingIntent.getBroadcast(
                        requireContext(),
                        4,
                        intentThursday,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        alarmCalendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY * 7,
                        pendingThursday
                    )
                }
                "toggleFriday" -> {
                    val intentFriday = Intent(requireContext(), AlarmReceiver::class.java)
                    val pendingFriday = PendingIntent.getBroadcast(
                        requireContext(),
                        5,
                        intentFriday,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        alarmCalendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY * 7,
                        pendingFriday
                    )
                }
                "toggleSaturday" -> {
                    val intentSaturday = Intent(requireContext(), AlarmReceiver::class.java)
                    val pendingSaturday = PendingIntent.getBroadcast(
                        requireContext(),
                        6,
                        intentSaturday,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        alarmCalendar.timeInMillis,
                        AlarmManager.INTERVAL_DAY * 7,
                        pendingSaturday
                    )
                }
                "else" -> {
                    Log.d("setAlarm","no toggle setAlarm")
                }
            }
            Toast.makeText(requireContext(), "アラームをセットしました", Toast.LENGTH_SHORT).show()
        }
        else
        {
            Toast.makeText(requireContext(),"アラーム時刻が設定されていません",Toast.LENGTH_SHORT).show()
        }

        // ユーザーが選択した時刻をセット
//        calendar = Calendar.getInstance(alarmTime)
//        calendar[Calendar.HOUR_OF_DAY] = alarmTime.hour
//        calendar[Calendar.MINUTE] = alarmTime.minute
//        calendar[Calendar.SECOND] = 0
//        calendar[Calendar.MILLISECOND] = 0

//        calendar = Calendar.getInstance()
//        calendar[Calender.HOUR_OF_DAY] =

        // SharedPreferencesにユーザーが選択した時刻を保存
//        saveAlarmTime(calendar.timeInMillis)

        // アラームを設定
//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            pendingIntent
//        )

//        Toast.makeText(requireContext(), "アラームをセットしました", Toast.LENGTH_SHORT).show()
    }

    private fun Calendar.setToNextWeekday(day: String) {
        val dayOfWeek = when(day){
            "toggleSunday" -> Calendar.SUNDAY
            "toggleMonday" -> Calendar.MONDAY
            "toggleTuesday" -> Calendar.TUESDAY
            "toggleWednesday" -> Calendar.WEDNESDAY
            "toggleThursday" -> Calendar.THURSDAY
            "toggleFriday" -> Calendar.FRIDAY
            "toggleSaturday" -> Calendar.SATURDAY
            else -> return
        }

        while(get(Calendar.DAY_OF_WEEK) != dayOfWeek)
        {
            add(Calendar.DAY_OF_YEAR,1)
        }

    }

    private fun saveAlarmTime(timeInMillis: Long) {
        val sharedPreferences = requireContext().getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val checkedDates = getCheckedDates()
//        for (day in checkedDates) {
//            editor.putLong(day, timeInMillis)
//        }
        editor.putLong(checkedDates,timeInMillis)
        editor.apply()

        Log.d("AlarmSaved", "Alarm time saved for days: ${checkedDates}")
    }

    private fun showTimePicker() {
        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("時間を設定してください")
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

            saveAlarmTime(calendar.timeInMillis)
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
