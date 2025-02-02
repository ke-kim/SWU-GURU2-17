//package com.example.swu_guru2_17
//
//import android.animation.ValueAnimator
//import android.app.AlertDialog
//import android.content.Intent
//import java.util.concurrent.TimeUnit
//import android.os.Bundle
//import android.os.Handler
//import android.os.Looper
//import android.view.Gravity
//import android.view.MenuItem
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.GravityCompat
//import androidx.drawerlayout.widget.DrawerLayout
//import com.google.android.material.navigation.NavigationView
//import com.example.swu_guru2_17.databinding.ActivityTimerBinding
//
//class TimerActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityTimerBinding
//    private var isRunning = false
//    private var secondsElapsed = 0
//    private var animator: ValueAnimator? = null
//
//    private val handler = Handler(Looper.getMainLooper())
//    private lateinit var timerRunnable: Runnable
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityTimerBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setupDrawer() // 사이드바 기능 추가
//        setupTimer()
//    }
//
//    private fun setupDrawer() {
//        binding.menuButton.setOnClickListener {
//            binding.drawerLayout.openDrawer(GravityCompat.END) // 🔹 오른쪽에서 사이드바 열기
//        }
//
//        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.nav_book_memo -> startActivity(Intent(this, MemoListActivity::class.java))
//                R.id.nav_story_book -> startActivity(Intent(this, TimerActivity::class.java))
//                R.id.nav_alarm -> startActivity(Intent(this, AlarmActivity::class.java))
//                R.id.nav_goal -> startActivity(Intent(this, GoalActivity::class.java))
//                R.id.nav_statistics -> startActivity(Intent(this, StatisticsActivity::class.java))
//                R.id.nav_sticker -> startActivity(Intent(this, StickerBoardActivity::class.java))
//            }
//            binding.drawerLayout.closeDrawers() // 메뉴 클릭 후 닫기
//            true
//        }
//    }
//
//    private fun setupTimer() {
//        binding.startStopButton.setOnClickListener {
//            if (isRunning) {
//                stopTimer()
//            } else {
//                startTimer()
//            }
//        }
//
//        binding.resetButton.setOnClickListener {
//            resetTimer()
//        }
//
//        binding.musicButton.setOnClickListener {
//            showMusicDialog()
//        }
//    }
//
//    private fun startTimer() {
//        isRunning = true
//        binding.startStopButton.text = "Stop"
//
//        timerRunnable = object : Runnable {
//            override fun run() {
//                secondsElapsed++
//                updateTimerTextView()
//                handler.postDelayed(this, 1000)
//            }
//        }
//        handler.post(timerRunnable)
//
//        animator = ValueAnimator.ofFloat(0f, 1f).apply {
//            duration = 60000L // 60초
//            repeatCount = ValueAnimator.INFINITE
//            repeatMode = ValueAnimator.RESTART
//            addUpdateListener { animation ->
//                val progress = animation.animatedValue as Float
//                binding.circleTimerView.setProgress(progress)
//            }
//            start()
//        }
//    }
//
//    private fun stopTimer() {
//        isRunning = false
//        binding.startStopButton.text = "Start"
//        handler.removeCallbacks(timerRunnable)
//        animator?.cancel()
//    }
//
//    private fun resetTimer() {
//        stopTimer()
//        secondsElapsed = 0
//        binding.timerTextView.text = "00:00"
//        binding.circleTimerView.setProgress(0f)
//    }
//
//    private fun updateTimerTextView() {
//        val minutes = TimeUnit.SECONDS.toMinutes(secondsElapsed.toLong())
//        val seconds = secondsElapsed % 60
//        val formattedTime = String.format("%02d:%02d", minutes, seconds)
//        binding.timerTextView.text = formattedTime
//    }
//
//    private fun showMusicDialog() {
//        val musicOptions = arrayOf("NONE", "Library", "Night", "Rain", "Storm", "City", "Cooking")
//        AlertDialog.Builder(this).apply {
//            setTitle("Select Background Music")
//            setItems(musicOptions) { _, which ->
//                val selectedMusic = musicOptions[which]
//                startActivity(Intent(this@TimerActivity, TimerActivity::class.java))
//            }
//            create()
//            show()
//        }
//    }
//}

package com.example.swu_guru2_17

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Intent
import java.util.concurrent.TimeUnit
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.example.swu_guru2_17.databinding.ActivityTimerBinding

class TimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimerBinding
    private var isRunning = false
    private var secondsElapsed = 0
    private var progressElapsed = 0f
    private var animator: ValueAnimator? = null

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var timerRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDrawer()
        setupTimer()
    }

    private fun setupDrawer() {
        binding.menuButton.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_book_memo -> startActivity(Intent(this, MemoListActivity::class.java))
                R.id.nav_story_book -> startActivity(Intent(this, TimerActivity::class.java))
                R.id.nav_alarm -> startActivity(Intent(this, AlarmActivity::class.java))
                R.id.nav_goal -> startActivity(Intent(this, GoalActivity::class.java))
                R.id.nav_statistics -> startActivity(Intent(this, StatisticsActivity::class.java))
                R.id.nav_sticker -> startActivity(Intent(this, StickerBoardActivity::class.java))
            }
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun setupTimer() {
        binding.startStopButton.setOnClickListener {
            if (isRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }

        binding.resetButton.setOnClickListener {
            resetTimer()
        }

        binding.musicButton.setOnClickListener {
            showMusicDialog()
        }
    }

    private fun startTimer() {
        isRunning = true
        binding.startStopButton.text = "Stop"

        timerRunnable = object : Runnable {
            override fun run() {
                secondsElapsed++
                updateTimerTextView()
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(timerRunnable)

        animator = ValueAnimator.ofFloat(progressElapsed, 1f).apply {
            duration = (60000L * (1 - progressElapsed)).toLong()
            addUpdateListener { animation ->
                progressElapsed = animation.animatedValue as Float
                binding.circleTimerView.setProgress(progressElapsed)
            }
            start()
        }
    }

    private fun stopTimer() {
        isRunning = false
        binding.startStopButton.text = "Start"
        handler.removeCallbacks(timerRunnable)
        animator?.cancel()
    }

    private fun resetTimer() {
        stopTimer()
        secondsElapsed = 0
        progressElapsed = 0f
        binding.timerTextView.text = "00:00"
        binding.circleTimerView.setProgress(0f)
    }

    private fun updateTimerTextView() {
        val minutes = TimeUnit.SECONDS.toMinutes(secondsElapsed.toLong())
        val seconds = secondsElapsed % 60
        val formattedTime = String.format("%02d:%02d", minutes, seconds)
        binding.timerTextView.text = formattedTime
    }

    private fun showMusicDialog() {
        val musicOptions = arrayOf("NONE", "Library", "Night", "Rain", "Storm", "City", "Cooking")
        AlertDialog.Builder(this).apply {
            setTitle("Select Background Music")
            setItems(musicOptions) { _, which ->
                val selectedMusic = musicOptions[which]
                startActivity(Intent(this@TimerActivity, TimerActivity::class.java))
            }
            create()
            show()
        }
    }
}
