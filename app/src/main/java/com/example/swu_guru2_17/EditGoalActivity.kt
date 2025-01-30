package com.example.swu_guru2_17

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class EditGoalActivity : AppCompatActivity() {

    private lateinit var closeButton: Button
    private lateinit var decrementWeeklyButton: Button
    private lateinit var incrementWeeklyButton: Button
    private lateinit var weeklyEditText: EditText
    private lateinit var decrementDailyButton: Button
    private lateinit var incrementDailyButton: Button
    private lateinit var dailyEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_goal)

        closeButton = findViewById(R.id.closeButton)
        decrementWeeklyButton = findViewById(R.id.decrementWeeklyButton)
        incrementWeeklyButton = findViewById(R.id.incrementWeeklyButton)
        weeklyEditText = findViewById(R.id.weeklyEditText)
        decrementDailyButton = findViewById(R.id.decrementDailyButton)
        incrementDailyButton = findViewById(R.id.incrementDailyButton)
        dailyEditText = findViewById(R.id.dailyEditText)

        closeButton.setOnClickListener {
            onBackPressed() // 이전 화면으로 넘어가기
        }

        decrementWeeklyButton.setOnClickListener {
            val current = weeklyEditText.text.toString().toIntOrNull() ?: 0
            if (current > 1) {
                weeklyEditText.setText((current - 1).toString())
            } else {
                Toast.makeText(this, "1 이하로 설정할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        incrementWeeklyButton.setOnClickListener {
            val current = weeklyEditText.text.toString().toIntOrNull() ?: 0
            if (current < 168) {
                weeklyEditText.setText((current + 1).toString())
            } else {
                Toast.makeText(this, "168 이상으로 설정할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        decrementDailyButton.setOnClickListener {
            val current = dailyEditText.text.toString().toIntOrNull() ?: 0
            if (current > 1) {
                dailyEditText.setText((current - 1).toString())
            } else {
                Toast.makeText(this, "1 이하로 설정할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        incrementDailyButton.setOnClickListener {
            val current = dailyEditText.text.toString().toIntOrNull() ?: 0
            if (current < 24) {
                dailyEditText.setText((current + 1).toString())
            } else {
                Toast.makeText(this, "24 이상으로 설정할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
