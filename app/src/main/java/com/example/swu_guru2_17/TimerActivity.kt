//package com.example.swu_guru2_17
//
//import android.animation.ValueAnimator
//import android.app.AlertDialog
//import android.content.Intent
//import android.os.Bundle
//import android.os.Handler
//import android.widget.Button
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import java.text.SimpleDateFormat
//import java.util.Locale
//
//class TimerActivity : AppCompatActivity() {
//
//    private lateinit var timerTextView: TextView
//    private lateinit var circleTimerView: CircleTimerView
//    private lateinit var startStopButton: Button
//    private lateinit var resetButton: Button
//    private lateinit var musicButton: Button
//
//    private var secondsElapsed = 0
//    private val handler = Handler()
//    private var isRunning = false
//    private var animator: ValueAnimator? = null
//
//    private val musicOptions = arrayOf("NONE", "Library", "Night", "Rain", "Storm", "City", "Cooking")
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_timer)
//
//        // 메뉴 버튼 클릭 이벤트
//        val menuButton: Button = findViewById(R.id.menuButton)
//        menuButton.setOnClickListener {
//            val intent = Intent(this, MenuActivity::class.java)
//            startActivity(intent)
//        }
//
//        timerTextView = findViewById(R.id.timerTextView)
//        circleTimerView = findViewById(R.id.circleTimerView)
//        startStopButton = findViewById(R.id.startStopButton)
//        resetButton = findViewById(R.id.resetButton)
//        musicButton = findViewById(R.id.musicButton)
//
//        startStopButton.setOnClickListener {
//            if (isRunning) {
//                stopTimer()
//            } else {
//                startTimer()
//            }
//        }
//
//        resetButton.setOnClickListener {
//            resetTimer()
//        }
//
//        musicButton.setOnClickListener {
//            showMusicDialog()
//        }
//    }
//
//    private fun startTimer() {
//        isRunning = true
//        startStopButton.text = "Stop"
//
//        animator = ValueAnimator.ofFloat(0f, 1f).apply {
//            duration = 60000L // 60초
//            repeatCount = ValueAnimator.INFINITE
//            repeatMode = ValueAnimator.RESTART
//            addUpdateListener { animation ->
//                val progress = animation.animatedValue as Float
//                circleTimerView.setProgress(progress)
//            }
//            start()
//        }
//
//        handler.postDelayed(object : Runnable {
//            override fun run() {
//                if (!isRunning) return
//
//                secondsElapsed++
//                val minutes = secondsElapsed / 60
//                val seconds = secondsElapsed % 60
//                timerTextView.text = String.format("%02d:%02d", minutes, seconds)
//
//                handler.postDelayed(this, 1000)
//            }
//        }, 1000)
//    }
//
//    private fun stopTimer() {
//        isRunning = false
//        startStopButton.text = "Start"
//        animator?.cancel()
//        handler.removeCallbacksAndMessages(null)
//        saveTimeData() // 타이머 종료 시 데이터 저장
//    }
//
//    private fun resetTimer() {
//        stopTimer()
//        secondsElapsed = 0
//        timerTextView.text = "00:00"
//        circleTimerView.setProgress(0f)
//    }
//
//    private fun saveStopwatchTime(startTime: String, endTime: String) {
//        val sharedPref = getSharedPreferences("StopwatchData", MODE_PRIVATE)
//        val editor = sharedPref.edit()
//        val gson = Gson()
//
//        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//
//        // 기존 데이터 불러오기
//        val dailyDataJson = sharedPref.getString("daily", "{}")
//        val dailyData: MutableMap<String, MutableList<TimePeriod>> =
//            gson.fromJson(dailyDataJson, object : TypeToken<MutableMap<String, MutableList<TimePeriod>>>() {}.type) ?: mutableMapOf()
//
//        // 새로운 시간 추가
//        val newEntry = TimePeriod(startTime, endTime)
//        dailyData[date] = (dailyData[date] ?: mutableListOf()).apply { add(newEntry) }
//
//        // 데이터 저장
//        editor.putString("daily", gson.toJson(dailyData))
//        editor.apply()
//    }
//
//    // 데이터 저장에 사용할 클래스
//    data class TimePeriod(val start: String, val end: String)
//
//    private fun showMusicDialog() {
//        AlertDialog.Builder(this).apply {
//            setTitle("Select Background Music")
//            setItems(musicOptions) { _, which ->
//                val selectedMusic = musicOptions[which]
//                Toast.makeText(this@TimerActivity, "Selected: $selectedMusic", Toast.LENGTH_SHORT).show()
//                // 여기에서 선택된 음악을 재생하는 로직을 구현할 수 있습니다.
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
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class TimerActivity : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var circleTimerView: CircleTimerView
    private lateinit var startStopButton: Button
    private lateinit var resetButton: Button
    private lateinit var musicButton: Button
    private lateinit var menuButton: Button

    private var secondsElapsed = 0
    private val handler = Handler()
    private var isRunning = false
    private var animator: ValueAnimator? = null
    private var startTime: String = ""

    private val musicOptions = arrayOf("NONE", "Library", "Night", "Rain", "Storm", "City", "Cooking")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        menuButton = findViewById(R.id.menuButton)
        timerTextView = findViewById(R.id.timerTextView)
        circleTimerView = findViewById(R.id.circleTimerView)
        startStopButton = findViewById(R.id.startStopButton)
        resetButton = findViewById(R.id.resetButton)
        musicButton = findViewById(R.id.musicButton)

        menuButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

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
        startTime = getCurrentTime()

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

        val endTime = getCurrentTime()
        saveStopwatchTime(startTime, endTime)
    }

    private fun resetTimer() {
        stopTimer()
        secondsElapsed = 0
        timerTextView.text = "00:00"
        circleTimerView.setProgress(0f)
    }

//    private fun saveStopwatchTime(startTime: String, endTime: String) {
//        val sharedPref = getSharedPreferences("StopwatchData", MODE_PRIVATE)
//        val editor = sharedPref.edit()
//        val gson = Gson()
//
//        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//
//        // 기존 데이터 불러오기
//        val dailyDataJson = sharedPref.getString("daily", "{}")
//        val dailyData: MutableMap<String, MutableList<TimePeriod>> =
//            gson.fromJson(dailyDataJson, object : TypeToken<MutableMap<String, MutableList<TimePeriod>>>() {}.type) ?: mutableMapOf()
//
//        // 새로운 시간 추가
//        val newEntry = TimePeriod(startTime, endTime)
//        dailyData[date] = (dailyData[date] ?: mutableListOf()).apply { add(newEntry) }
//
//        // 저장 로그 확인
//        Log.d("TIMER_DEBUG", "Saving time: $newEntry")
//
//        // 데이터 저장
//        editor.putString("daily", gson.toJson(dailyData))
//        editor.apply()
//    }

    private fun saveStopwatchTime(startTime: String, endTime: String) {
        val sharedPref = getSharedPreferences("StopwatchData", MODE_PRIVATE)
        val editor = sharedPref.edit()
        val gson = Gson()

        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        // 기존 데이터 불러오기
        val dailyDataJson = sharedPref.getString("daily", "{}")
        val dailyData: MutableMap<String, MutableList<TimePeriod>> =
            gson.fromJson(dailyDataJson, object : TypeToken<MutableMap<String, MutableList<TimePeriod>>>() {}.type) ?: mutableMapOf()

        // 새로운 시간 추가 (기존 데이터 유지)
        val newEntry = TimePeriod(startTime, endTime)
        val updatedList = (dailyData[date] ?: mutableListOf()).apply { add(newEntry) }
        dailyData[date] = updatedList

        // 저장 로그 확인
        Log.d("TIMER_DEBUG", "Saving updated data: ${gson.toJson(dailyData)}")

        // 업데이트된 데이터 저장
        editor.putString("daily", gson.toJson(dailyData))
        val success = editor.commit()  // ✅ commit()을 사용하여 즉시 저장

        if (success) {
            Log.d("TIMER_DEBUG", "Data successfully saved to SharedPreferences.")
        } else {
            Log.e("TIMER_DEBUG", "Failed to save data to SharedPreferences.")
        }
    }


    private fun getCurrentTime(): String {
        return SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
    }

    data class TimePeriod(val start: String, val end: String)

    private fun showMusicDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Select Background Music")
            setItems(musicOptions) { _, which ->
                val selectedMusic = musicOptions[which]
                Toast.makeText(this@TimerActivity, "Selected: $selectedMusic", Toast.LENGTH_SHORT).show()
            }
            create()
            show()
        }
    }
}
