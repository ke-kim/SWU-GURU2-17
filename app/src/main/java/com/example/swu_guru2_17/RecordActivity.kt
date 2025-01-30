//package com.example.swu_guru2_17
//
//import android.annotation.SuppressLint
//import android.content.Intent
//import android.content.SharedPreferences
//import android.os.Bundle
//import android.widget.Button
//import android.widget.ImageButton
//import androidx.appcompat.app.AppCompatActivity
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//import java.text.SimpleDateFormat
//import java.util.*
//
//class RecordActivity : AppCompatActivity() {
//    private lateinit var sharedPref: SharedPreferences
//    private lateinit var gson: Gson
//    private lateinit var dailyButton: Button
//    private lateinit var weeklyButton: Button
//    private lateinit var menuButton: ImageButton
//
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_record)
//
//        sharedPref = getSharedPreferences("StopwatchData", MODE_PRIVATE)
//        gson = Gson()
//
//        dailyButton = findViewById(R.id.dailyButton)
//        weeklyButton = findViewById(R.id.weeklyButton)
//        menuButton = findViewById(R.id.menuButton)
//
//        dailyButton.setOnClickListener { loadDailyData() }
//        weeklyButton.setOnClickListener { loadWeeklyData() }
//        menuButton.setOnClickListener {
//            val intent = Intent(this, MenuActivity::class.java)
//            startActivity(intent)
//        }
//
//        // 초기 화면 설정
//        loadDailyData()
//    }
//
//    private fun loadDailyData() {
//        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
//        val dailyDataJson = sharedPref.getString("daily", "{}")
//        val dailyData: MutableMap<String, MutableList<TimePeriod>> =
//            gson.fromJson(dailyDataJson, object : TypeToken<MutableMap<String, MutableList<TimePeriod>>>() {}.type) ?: mutableMapOf()
//
//        val todayData = dailyData[date] ?: emptyList()
//
//        // 원형 그래프 UI 업데이트 (CircleTimerView 사용)
//        updateCircleGraph(todayData)
//    }
//
//    private fun loadWeeklyData() {
//        val calendar = Calendar.getInstance()
//        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
//        val startOfWeek = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
//        val weeklyKey = "week_$startOfWeek"
//
//        val weeklyDataJson = sharedPref.getString("weekly", "{}")
//        val weeklyData: MutableMap<String, MutableMap<String, Double>> =
//            gson.fromJson(weeklyDataJson, object : TypeToken<MutableMap<String, MutableMap<String, Double>>>() {}.type) ?: mutableMapOf()
//
//        val weeklyUsage = weeklyData[weeklyKey] ?: emptyMap()
//
//        // 막대 그래프 UI 업데이트
//        updateWeeklyGraph(weeklyUsage)
//    }
//
//    private fun updateCircleGraph(todayData: List<TimePeriod>) {
//        // 하루 동안 스톱워치를 켰던 구간을 원형 그래프에 반영하는 코드 작성
//    }
//
//    private fun updateWeeklyGraph(weeklyUsage: Map<String, Double>) {
//        // 일주일 동안 스톱워치를 사용한 시간을 막대 그래프로 반영하는 코드 작성
//    }
//}
//
//// 데이터 저장을 위한 클래스
//data class TimePeriod(val start: String, val end: String)

package com.example.swu_guru2_17

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class RecordActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private val gson by lazy { Gson() }
    private val dateFormatter by lazy { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    private lateinit var dailyButton: Button
    private lateinit var weeklyButton: Button
    private lateinit var menuButton: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record)

        sharedPref = getSharedPreferences("StopwatchData", MODE_PRIVATE)

        dailyButton = findViewById(R.id.dailyButton)
        weeklyButton = findViewById(R.id.weeklyButton)
        menuButton = findViewById(R.id.menuButton)

        dailyButton.setOnClickListener { loadDailyData() }
        weeklyButton.setOnClickListener { loadWeeklyData() }
        menuButton.setOnClickListener {
            startActivity(Intent(this, MenuActivity::class.java))
        }

        loadDailyData()
    }

    private fun loadDailyData() {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val dailyDataJson = sharedPref.getString("daily", "{}")
        val dailyData: MutableMap<String, MutableList<TimePeriod>> =
            gson.fromJson(dailyDataJson, object : TypeToken<MutableMap<String, MutableList<TimePeriod>>>() {}.type) ?: mutableMapOf()

        val todayData = dailyData[date] ?: emptyList()
        val totalTime = todayData.sumOf { it.getDurationInHours() }

        // 로그 확인
        Log.d("RECORD_DEBUG", "Loaded Data: $todayData")
        Log.d("RECORD_DEBUG", "Total Time: $totalTime hours")

        updateCircleGraph(todayData, totalTime)
    }


    private fun loadWeeklyData() {
        val startOfWeek = Calendar.getInstance().apply { set(Calendar.DAY_OF_WEEK, Calendar.MONDAY) }.time.let { dateFormatter.format(it) }
        val weeklyKey = "week_$startOfWeek"

        val weeklyDataJson = sharedPref.getString("weekly", "{}") ?: "{}"
        val weeklyData: MutableMap<String, MutableMap<String, Double>> =
            gson.fromJson(weeklyDataJson, object : TypeToken<MutableMap<String, MutableMap<String, Double>>>() {}.type) ?: mutableMapOf()

        val weeklyUsage = weeklyData[weeklyKey] ?: emptyMap()
        val totalWeeklyTime = weeklyUsage.values.sum()

        updateWeeklyGraph(weeklyUsage, totalWeeklyTime)
    }

    private fun updateCircleGraph(todayData: List<TimePeriod>, totalTime: Double) {
        // 하루 동안 스톱워치를 켰던 구간을 원형 그래프에 반영하는 코드 작성
    }

    private fun updateWeeklyGraph(weeklyUsage: Map<String, Double>, totalWeeklyTime: Double) {
        // 일주일 동안 스톱워치를 사용한 시간을 막대 그래프로 반영하는 코드 작성
    }
}

// 데이터 저장을 위한 클래스
data class TimePeriod(val start: String, val end: String) {
    fun getDurationInHours(): Double = runCatching {
        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val startTime = format.parse(start)?.time ?: 0
        val endTime = format.parse(end)?.time ?: 0
        (endTime - startTime) / 1000.0 / 3600.0 // 초 -> 시간 변환
    }.getOrElse {
        it.printStackTrace()
        0.0
    }
}
