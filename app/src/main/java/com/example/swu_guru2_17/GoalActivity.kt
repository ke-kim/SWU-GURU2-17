package com.example.swu_guru2_17

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GoalActivity : AppCompatActivity() {

    private lateinit var dailyButton: Button
    private lateinit var weeklyButton: Button
    private lateinit var completionTextView: TextView
    private lateinit var editGoalButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        // 메뉴 버튼 클릭 이벤트
        val menuButton: Button = findViewById(R.id.menuButton)
        menuButton.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        dailyButton = findViewById(R.id.dailyButton)
        weeklyButton = findViewById(R.id.weeklyButton)
        completionTextView = findViewById(R.id.completionTextView)
        editGoalButton = findViewById(R.id.editGoalButton)

        dailyButton.setOnClickListener {
            completionTextView.text = "56% 달성"
        }

        weeklyButton.setOnClickListener {
            completionTextView.text = "0% 달성"
        }

        editGoalButton.setOnClickListener {
            val intent = Intent(this, EditGoalActivity::class.java)
            startActivity(intent)
        }
    }
}
