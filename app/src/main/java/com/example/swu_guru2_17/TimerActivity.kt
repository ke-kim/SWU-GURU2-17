package com.example.swu_guru2_17

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TimerActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var circleTimerView: CircleTimerView
    private lateinit var startStopButton: Button
    private lateinit var resetButton: Button
    private lateinit var musicButton: Button

    private var secondsElapsed = 0
    private val handler = Handler()
    private var isRunning = false
    private var animator: ValueAnimator? = null

    private val musicOptions = arrayOf("NONE", "Library", "Night", "Rain", "Storm", "City", "Cooking")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        timerTextView = findViewById(R.id.timerTextView)
        circleTimerView = findViewById(R.id.circleTimerView)
        startStopButton = findViewById(R.id.startStopButton)
        resetButton = findViewById(R.id.resetButton)
        musicButton = findViewById(R.id.musicButton)

        startStopButton.setOnClickListener {
            if (isRunning) {
                stopTimer()
            } else {
                startTimer()
            }
        }

        resetButton.setOnClickListener {
            resetTimer()
        }

        musicButton.setOnClickListener {
            showMusicDialog()
        }
    }

    private fun startTimer() {
        isRunning = true
        startStopButton.text = "Stop"

        animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 60000L // 60초
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener { animation ->
                val progress = animation.animatedValue as Float
                circleTimerView.setProgress(progress)
            }
            start()
        }

        handler.postDelayed(object : Runnable {
            override fun run() {
                if (!isRunning) return

                secondsElapsed++
                val minutes = secondsElapsed / 60
                val seconds = secondsElapsed % 60
                timerTextView.text = String.format("%02d:%02d", minutes, seconds)

                handler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private fun stopTimer() {
        isRunning = false
        startStopButton.text = "Start"
        animator?.cancel()
        handler.removeCallbacksAndMessages(null)
    }

    private fun resetTimer() {
        stopTimer()
        secondsElapsed = 0
        timerTextView.text = "00:00"
        circleTimerView.setProgress(0f)
    }

    private fun showMusicDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Select Background Music")
            setItems(musicOptions) { _, which ->
                val selectedMusic = musicOptions[which]
                Toast.makeText(this@TimerActivity, "Selected: $selectedMusic", Toast.LENGTH_SHORT).show()
                // 여기에서 선택된 음악을 재생하는 로직을 구현할 수 있습니다.
            }
            create()
            show()
        }
    }
}
