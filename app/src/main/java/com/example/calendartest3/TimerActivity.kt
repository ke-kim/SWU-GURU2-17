package com.example.swu_guru2_17

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.swu_guru2_17.databinding.ActivityTimerBinding

class TimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimerBinding
    private var isRunning = false
    private var secondsElapsed = 0
    private var progressElapsed = 0f
    private var animator: ValueAnimator? = null
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var timerRunnable: Runnable
    private var mediaPlayer: MediaPlayer? = null
    private var selectedMusic: String? = null

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
                R.id.nav_home -> startActivity(Intent(this, MainActivity::class.java))
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

        // 음악이 선택된 경우 재생
        selectedMusic?.let { playMusicFromRaw(it) }
    }

    private fun stopTimer() {
        isRunning = false
        binding.startStopButton.text = "Start"
        handler.removeCallbacks(timerRunnable)
        animator?.cancel()

        // 음악 정지
        mediaPlayer?.pause()
    }

    private fun resetTimer() {
        stopTimer()
        secondsElapsed = 0
        progressElapsed = 0f
        binding.timerTextView.text = "00:00"
        binding.circleTimerView.setProgress(0f)
    }

    private fun updateTimerTextView() {
        val minutes = secondsElapsed / 60
        val seconds = secondsElapsed % 60
        val formattedTime = String.format("%02d:%02d", minutes, seconds)
        binding.timerTextView.text = formattedTime
    }

    private fun showMusicDialog() {
        val musicOptions = arrayOf("NONE", "Library", "Night", "Rain", "Storm", "City", "Cooking")
        val musicFiles = mapOf(
            "Library" to R.raw.library,
            "Night" to R.raw.night,
            "Rain" to R.raw.rain,
            "Storm" to R.raw.storm,
            "City" to R.raw.city,
            "Cooking" to R.raw.cooking
        )

        AlertDialog.Builder(this).apply {
            setTitle("배경 음악 선택")
            setItems(musicOptions) { _, which ->
                val selected = musicOptions[which]
                if (selected == "NONE") {
                    stopMusic()
                    selectedMusic = null
                } else {
                    selectedMusic = selected
                    playMusicFromRaw(selected)
                }
            }
            create()
            show()
        }
    }

    private fun playMusicFromRaw(musicName: String) {
        val musicFiles = mapOf(
            "Library" to R.raw.library,
            "Night" to R.raw.night,
            "Rain" to R.raw.rain,
            "Storm" to R.raw.storm,
            "City" to R.raw.city,
            "Cooking" to R.raw.cooking
        )

        // 기존 음악 정지
        stopMusic()

        // 새 음악 재생
        val musicResId = musicFiles[musicName] ?: return
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        mediaPlayer = MediaPlayer.create(this, musicResId).apply {
            mediaPlayer?.setOnCompletionListener {
                mediaPlayer?.release()
                mediaPlayer = null
            }
            setOnCompletionListener { stopMusic() }
            start()
        }
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        super.onDestroy()
        mediaPlayer?.release()
    }
}
