package com.example.swu_guru2_17

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class EditWeeklyGoalActivity : AppCompatActivity() {
    private lateinit var sharedPref: SharedPreferences
    private val gson = Gson()

    private lateinit var goalEditText: EditText
    private lateinit var incrementButton: Button
    private lateinit var decrementButton: Button
    private lateinit var saveGoalButton: Button
    private lateinit var closeButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_weekly_goal)

        sharedPref = getSharedPreferences("GoalPrefs", MODE_PRIVATE)

        goalEditText = findViewById(R.id.goalEditText)
        incrementButton = findViewById(R.id.incrementButton)
        decrementButton = findViewById(R.id.decrementButton)
        saveGoalButton = findViewById(R.id.saveGoalButton)
        closeButton = findViewById(R.id.closeButton)

        // 목표 시간 숫자 입력 필터 (최대 168시간 제한)
        goalEditText.inputType = InputType.TYPE_CLASS_NUMBER
        goalEditText.filters = arrayOf(InputFilter.LengthFilter(3))

        // 저장된 목표 시간 불러오기
        loadSavedGoal()

        // 증가 버튼 클릭
        incrementButton.setOnClickListener {
            adjustGoalTime(1)
        }

        // 감소 버튼 클릭
        decrementButton.setOnClickListener {
            adjustGoalTime(-1)
        }

        // 목표 저장 버튼 클릭
        saveGoalButton.setOnClickListener {
            saveGoalTime()
        }

        // 닫기 버튼 클릭
        closeButton.setOnClickListener {
            finish()
        }
    }

    private fun loadSavedGoal() {
        val goalDataJson = sharedPref.getString("goalData", "{}")
        val goalData: MutableMap<String, Int> =
            gson.fromJson(goalDataJson, object : com.google.gson.reflect.TypeToken<MutableMap<String, Int>>() {}.type) ?: mutableMapOf()

        val savedHours = goalData["weekly"] ?: 10 // 기본값 10시간
        goalEditText.setText(savedHours.toString())
    }

    private fun adjustGoalTime(amount: Int) {
        val currentGoal = goalEditText.text.toString().toIntOrNull() ?: 0
        val newGoal = (currentGoal + amount).coerceIn(0, 168)
        goalEditText.setText(newGoal.toString())
    }

    private fun saveGoalTime() {
        val selectedHours = goalEditText.text.toString().toIntOrNull() ?: return

        val goalDataJson = sharedPref.getString("goalData", "{}")
        val goalData: MutableMap<String, Int> =
            gson.fromJson(goalDataJson, object : com.google.gson.reflect.TypeToken<MutableMap<String, Int>>() {}.type) ?: mutableMapOf()

        goalData["weekly"] = selectedHours

        val editor = sharedPref.edit()
        editor.putString("goalData", gson.toJson(goalData))
        editor.apply()

        // GoalActivity에서 목표 달성률 업데이트
        val intent = Intent(this, GoalActivity::class.java)
        startActivity(intent)
        finish()
    }
}
