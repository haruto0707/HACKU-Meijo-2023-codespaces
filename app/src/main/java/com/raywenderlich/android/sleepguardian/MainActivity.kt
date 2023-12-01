/*
 * Copyright (c) 2021 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.sleepguardian

import android.Manifest.permission.ACTIVITY_RECOGNITION
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.raywenderlich.android.sleepguardian.databinding.ActivityMainBinding





/**
 * Main Screen
 */
class MainActivity : AppCompatActivity() {


  private val binding by lazy {
    ActivityMainBinding.inflate(layoutInflater)
  }

  private val sleepRequestManager by lazy{
    SleepRequestsManager(this)
  }

  private val permissionRequester: ActivityResultLauncher<String> =
      registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {
          requestActivityRecognitionPermission()
        } else {
          sleepRequestManager.subscribeToSleepUpdates()
        }
      }


  override fun onCreate(savedInstanceState: Bundle?) {
    // Switch to AppTheme for displaying the activity
    setTheme(R.style.AppTheme)

    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    // Your code
    sleepRequestManager.requestSleepUpdates(requestPermission = {
      permissionRequester.launch(ACTIVITY_RECOGNITION)
    })
//    if (hasNavigationBar()) {
//      val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
//      val layoutParams = bottomNavigationView.layoutParams as ViewGroup.MarginLayoutParams
//      layoutParams.bottomMargin += getNavigationBarHeight()
//      bottomNavigationView.layoutParams = layoutParams
//      bottomNavigationView.requestLayout() // レイアウトの更新を強制
//    }

    //BottomNavigationViewによる画面遷移
    replaceFragment(Home())
    supportActionBar?.title = "アラーム"
    binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
      when (item.itemId) {
        R.id.navigation_alarm -> {
          replaceFragment(Home())
          supportActionBar?.title = "アラーム"  // Use supportActionBar to set the title
        }
        R.id.navigation_calendar -> {
          replaceFragment(Calendar())
          supportActionBar?.title = "カレンダー"  // Set the title for calendar
        }
        R.id.navigation_settings -> {
          replaceFragment(Setting())
          supportActionBar?.title = "設定"  // Set the title for settings
        }
      }
      true
    }



  }

  override fun onDestroy() {
    super.onDestroy()
    sleepRequestManager.unsubscribeFromSleepUpdates()
  }

  private fun requestActivityRecognitionPermission() {
    val intent = Intent().apply {
      action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
      data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    startActivity(intent)
  }

  //下部ナビゲーションバーの有無の判別
  fun hasNavigationBar(): Boolean {
    val id: Int = resources.getIdentifier("config_showNavigationBar", "bool", "android")
    return id > 0 && resources.getBoolean(id)
  }

  //下部ナビゲーションバーの高さの取得
  fun getNavigationBarHeight(): Int {
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
  }

  //アクションバーのメニュー表示
  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.actionbar, menu)
    return true
  }

  //アクションバーのボタン押下
  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.menu1 -> return true
      }
    return super.onOptionsItemSelected(item)
  }

  //BottomNavigationViewの画面フラグ用関数
  private fun replaceFragment(fragment : Fragment)
  {
    val fragmentManager = supportFragmentManager
    val fragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.replace(R.id.frame_layout,fragment)
    fragmentTransaction.commit()
  }





}
