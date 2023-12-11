package com.raywenderlich.android.sleepguardian

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class firstAlarm : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_alarm)

        imageView = findViewById(R.id.myImageView)

        // 画面の幅と高さを取得
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        // ImageViewのサイズを画面いっぱいに設定
        imageView.layoutParams.width = screenWidth
        imageView.layoutParams.height = screenHeight
        imageView.scaleType = ImageView.ScaleType.FIT_XY // 画像をImageViewに合わせて拡大

        // ImageViewの更新
        imageView.requestLayout()

        // toHome ボタンにクリックリスナーを設定
        findViewById<View>(R.id.toHome).setOnClickListener {
            // MainActivity に遷移するIntentを作成
            val intent = Intent(this, MainActivity::class.java)
            // Intentを使用して画面遷移
            startActivity(intent)
            // AlarmServiceを停止
            stopAlarmService()
            // アクティビティを終了
            finish()
        }
    }

    private fun stopAlarmService() {
        // AlarmServiceを停止するIntentを作成
        val intent = Intent(this, AlarmService::class.java)
        stopService(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        // MediaPlayerを解放
        Toast.makeText(this, "Alarm Stopped", Toast.LENGTH_SHORT).show()
    }
}
