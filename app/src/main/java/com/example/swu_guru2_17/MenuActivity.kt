package com.example.swu_guru2_17

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.swu_guru2_17.R

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        Log.d("MenuActivity", "✅ MenuActivity 실행됨!")

        val stopwatchButton: Button = findViewById(R.id.stopwatchButton)
        val alertButton: Button = findViewById(R.id.alertButton)
        val goalButton: Button = findViewById(R.id.goalButton)
        val statisticsButton: Button = findViewById(R.id.statisticsButton)

        // 스톱워치 버튼 클릭 시 TimerActivity로 이동
        stopwatchButton.setOnClickListener {
            val intent = Intent(this, TimerActivity::class.java)
            startActivity(intent)
        }

        // 알람 버튼 클릭 시 AlarmActivity로 이동
        alertButton.setOnClickListener {
            Log.d("TEST", "Moving to AlarmActivity")
            val intent = Intent(this, AlarmActivity::class.java)
            startActivity(intent)
        }

        // 목표 버튼 클릭 시 GoalActivity로 이동
        goalButton.setOnClickListener {
            val intent = Intent(this, GoalActivity::class.java)
            startActivity(intent)
        }

        // 통계 버튼 클릭 시 RecordActivity로 이동
        statisticsButton.setOnClickListener {
            val intent = Intent(this, RecordActivity::class.java)
            startActivity(intent)
        }

        // 스티커판 버튼 클릭 시 이동
//        statisticsButton.setOnClickListener {
//            val intent = Intent(this, Activity::class.java)
//            startActivity(intent)
//        }

        // 닫기 버튼 클릭 이벤트 설정
        val closeButton: TextView = findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            finish() // 현재 Activity를 종료하고 이전 화면으로 돌아감
        }

    }
}