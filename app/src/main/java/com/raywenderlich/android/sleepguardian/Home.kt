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
        // トグルボタンの初期設定とリスナーの設定
        initializeToggleButtons()

    }

    // 曜日の選択状態を保存する
    private fun saveDaySelection(day: String, isSelected: Boolean) {
        val prefs = requireContext().getSharedPreferences("DaySelections", Context.MODE_PRIVATE)
        prefs.edit().putBoolean(day, isSelected).apply()
    }

    private fun initializeToggleButtons() {
        binding.toggleSunday.isChecked = true
        updateToggleButtonStyle(binding.toggleSunday, true)
        displayAlarmTimeForDay("Sunday")

        val toggleButtonListener = View.OnClickListener { view ->
            val selectedToggleButton = view as ToggleButton
            val day = getDayFromToggleButton(selectedToggleButton.id)
            if (selectedToggleButton.isChecked) {
                deselectOtherToggleButtons(selectedToggleButton.id)
                updateToggleButtonStyle(selectedToggleButton, true)
                saveDaySelection(day, true)
            } else {
                updateToggleButtonStyle(selectedToggleButton, false)
                saveDaySelection(day, false)
            }
            displayAlarmTimeForDay(day)
        }


        binding.toggleSunday.setOnClickListener(toggleButtonListener)
        binding.toggleMonday.setOnClickListener(toggleButtonListener)
        binding.toggleTuesday.setOnClickListener(toggleButtonListener)
        binding.toggleWednesday.setOnClickListener(toggleButtonListener)
        binding.toggleThursday.setOnClickListener(toggleButtonListener)
        binding.toggleFriday.setOnClickListener(toggleButtonListener)
        binding.toggleSaturday.setOnClickListener(toggleButtonListener)
    }

    private fun saveAlarmTimeForDay(day: String, timeInMillis: Long) {
        val sharedPreferences = requireContext().getSharedPreferences("AlarmTimePerDay", Context.MODE_PRIVATE)
        sharedPreferences.edit().putLong(day, timeInMillis).apply()
    }

    private fun displayAlarmTimeForDay(day: String) {
        val sharedPreferences = requireContext().getSharedPreferences("AlarmTimePerDay", Context.MODE_PRIVATE)
        val savedTimeInMillis = sharedPreferences.getLong(day, -1L)

        if (savedTimeInMillis != -1L) {
            val calendar = Calendar.getInstance().apply { timeInMillis = savedTimeInMillis }
            val formattedTime = formatTime(calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE])
            binding.selectedTime.text = formattedTime
        } else {
            binding.selectedTime.text = "XX:XX"
        }
    }


    // IDから曜日を取得する
    private fun getDayFromToggleButton(id: Int): String {
        return when (id) {
            R.id.toggleSunday -> "Sunday"
            R.id.toggleMonday -> "Monday"
            R.id.toggleTuesday -> "Tuesday"
            R.id.toggleWednesday -> "Wednesday"
            R.id.toggleThursday -> "Thursday"
            R.id.toggleFriday -> "Friday"
            R.id.toggleSaturday -> "Saturday"
            else -> ""
        }
    }

    private fun deselectOtherToggleButtons(selectedId: Int) {
        val allToggleButtons = listOf(
            binding.toggleSunday,
            binding.toggleMonday,
            binding.toggleTuesday,
            binding.toggleWednesday,
            binding.toggleThursday,
            binding.toggleFriday,
            binding.toggleSaturday
        )

        for (toggleButton in allToggleButtons) {
            if (toggleButton.id != selectedId) {
                toggleButton.isChecked = false
                updateToggleButtonStyle(toggleButton, false)
            }
        }
    }


    private fun updateToggleButtonStyle(toggleButton: ToggleButton, isSelected: Boolean) {

        if (isSelected) {
            toggleButton.setBackgroundResource(R.color.selectedColor)
            toggleButton.setTextColor(resources.getColor(R.color.selectedTextColor))
        } else {
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
            resetAlarmScheduledFlag()
        }
        binding.cancelAlarmBtn.setOnClickListener {
            // ここにアラームをキャンセルする処理を追加
            cancelAlarm()
        }

        // アプリ起動時に保存された時刻を読み込んで表示
        val savedAlarmTime = getSavedAlarmTime()
        if (savedAlarmTime != -1L) {
            calendar = Calendar.getInstance()
            calendar.timeInMillis = savedAlarmTime
            updateSelectedTimeText()
        }

        return view
    }

    private fun resetAlarmScheduledFlag() {
        val prefs = requireContext().getSharedPreferences("SleepReceiverPrefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("isAlarmScheduled", false).apply()
    }

    private fun getSavedAlarmTime(): Long {
        val sharedPreferences = requireContext().getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getLong("alarmTime", -1L)
    }

    private fun updateSelectedTimeText() {
        val formattedTime = if (calendar[Calendar.HOUR_OF_DAY] > 12) {
            String.format("%02d", calendar[Calendar.HOUR_OF_DAY] - 12) + ":" + String.format(
                "%02d",
                calendar[Calendar.MINUTE]
            ) + "PM"
        } else {
            String.format("%02d", calendar[Calendar.HOUR_OF_DAY]) + ":" + String.format(
                "%02d",
                calendar[Calendar.MINUTE]
            ) + "AM"
        }

        binding.selectedTime.text = formattedTime
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

        val prefs = requireContext().getSharedPreferences("DaySelections", Context.MODE_PRIVATE)
//        val isSundaySelected = prefs.getBoolean("Sunday", false)
//        val isMondaySelected = prefs.getBoolean("Monday", false)
//        val isTuesdaySelected = prefs.getBoolean("Tuesday",false)
//        val isWednesdaySelected = prefs.getBoolean("Wednesday",false)
//        val isThursdaySelected = prefs.getBoolean("Thursday",false)
//        val isFridaySelected = prefs.getBoolean("Friday",false)
//        val isSaturdaySelected = prefs.getBoolean("Saturday",false)



        // ユーザーが選択した時刻をセット
        calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = picker.hour
        calendar[Calendar.MINUTE] = picker.minute
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0

        // SharedPreferencesにユーザーが選択した時刻を保存
        if (prefs.getBoolean("Sunday", false)) {
            setSingleAlarmForDay("Sunday", Calendar.SUNDAY)
        }
        if (prefs.getBoolean("Monday", false)) {
            setSingleAlarmForDay("Monday", Calendar.MONDAY)
        }
        if (prefs.getBoolean("Tuesday", false)) {
            setSingleAlarmForDay("Tuesday", Calendar.TUESDAY)
        }
        if (prefs.getBoolean("Wednesday", false)) {
            setSingleAlarmForDay("Wednesday", Calendar.WEDNESDAY)
        }
        if (prefs.getBoolean("Thursday", false)) {
            setSingleAlarmForDay("Thursday", Calendar.THURSDAY)
        }
        if (prefs.getBoolean("Friday", false)) {
            setSingleAlarmForDay("Friday", Calendar.FRIDAY)
        }
        if (prefs.getBoolean("Saturday", false)) {
            setSingleAlarmForDay("Saturday", Calendar.SATURDAY)
        }

        Toast.makeText(requireContext(), "アラームをセットしました", Toast.LENGTH_SHORT).show()
    }

    private fun setSingleAlarmForDay(day: String, dayOfWeek: Int) {
        val sharedPreferences = requireContext().getSharedPreferences("AlarmTimePerDay", Context.MODE_PRIVATE)
        val timeInMillis = sharedPreferences.getLong(day, -1L)

        if (timeInMillis != -1L) {
            val alarmCalendar = getNextAlarmCalendar(timeInMillis, dayOfWeek)
            val intent = Intent(requireContext(), AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                requireContext(),
                dayOfWeek,  // 使用している曜日を識別するためのユニークなIDとして
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmCalendar.timeInMillis, pendingIntent)
        }
    }

    private fun getNextAlarmCalendar(timeInMillis: Long, dayOfWeek: Int): Calendar {
        val alarmCalendar = Calendar.getInstance().apply {
            this.timeInMillis = timeInMillis
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            if (this.before(Calendar.getInstance())) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }
        }
        return alarmCalendar
    }


    private fun setSingleAlarm(alarmCalendar: Calendar) {
        val intent = Intent(requireContext(), AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            alarmCalendar.timeInMillis,
            pendingIntent
        )
    }



    private fun saveAlarmTime(timeInMillis: Long) {
        val sharedPreferences = requireContext().getSharedPreferences("AlarmPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("alarmTime", timeInMillis)
        editor.apply()
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
            val selectedDay = getSelectedDay()
            if (selectedDay.isNotEmpty()) {
                val formattedTime = formatTime(picker.hour, picker.minute)
                binding.selectedTime.text = formattedTime
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, picker.hour)
                    set(Calendar.MINUTE, picker.minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                saveAlarmTimeForDay(selectedDay, selectedCalendar.timeInMillis)
            }
        }
    }

    private fun formatTime(hour: Int, minute: Int): String {
        return String.format("%02d:%02d", hour, minute)
    }


    private fun getSelectedDay(): String {
        return when{
            binding.toggleSunday.isChecked -> "Sunday"
            binding.toggleMonday.isChecked -> "Monday"
            binding.toggleTuesday.isChecked -> "Tuesday"
            binding.toggleWednesday.isChecked -> "Wednesday"
            binding.toggleThursday.isChecked -> "Thursday"
            binding.toggleFriday.isChecked -> "Friday"
            binding.toggleSaturday.isChecked -> "Saturday"
            else -> ""
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
