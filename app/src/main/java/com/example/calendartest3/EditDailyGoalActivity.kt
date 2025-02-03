package com.example.swu_guru2_17

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class EditDailyGoalActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private val gson = Gson()

    private lateinit var hourPicker: NumberPicker
    private lateinit var minutePicker: NumberPicker
    private lateinit var saveGoalButton: Button
    private lateinit var backButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_daily_goal)

        sharedPref = getSharedPreferences("GoalPrefs", MODE_PRIVATE)

        hourPicker = findViewById(R.id.hourPicker)
        minutePicker = findViewById(R.id.minutePicker)
        saveGoalButton = findViewById(R.id.saveGoalButton)
        backButton = findViewById(R.id.backButton)

        // NumberPicker 설정
        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        minutePicker.minValue = 0
        minutePicker.maxValue = 59

        // 저장된 목표 시간 불러오기
        loadSavedGoal()

        // 목표 저장 버튼 클릭
        saveGoalButton.setOnClickListener {
            saveGoalTime()
        }

        // 닫기 버튼 클릭
        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadSavedGoal() {
        val goalDataJson = sharedPref.getString("goalData", "{}")
        val goalData: MutableMap<String, Int> =
            gson.fromJson(goalDataJson, object : com.google.gson.reflect.TypeToken<MutableMap<String, Int>>() {}.type) ?: mutableMapOf()

        val savedMinutes = goalData["daily"] ?: 60 // 기본값 60분
        hourPicker.value = savedMinutes / 60
        minutePicker.value = savedMinutes % 60
    }

    private fun saveGoalTime() {
        val selectedMinutes = (hourPicker.value * 60) + minutePicker.value

        val goalDataJson = sharedPref.getString("goalData", "{}")
        val goalData: MutableMap<String, Int> =
            gson.fromJson(goalDataJson, object : com.google.gson.reflect.TypeToken<MutableMap<String, Int>>() {}.type) ?: mutableMapOf()

        goalData["daily"] = selectedMinutes

        val editor = sharedPref.edit()
        editor.putString("goalData", gson.toJson(goalData))
        editor.apply()

        // GoalActivity에서 목표 달성률 업데이트
        val intent = Intent(this, GoalActivity::class.java)
        startActivity(intent)
        finish()
    }
}
