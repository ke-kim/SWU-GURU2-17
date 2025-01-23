package com.example.swu_guru2_17

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.swu_guru2_17.R

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val stopwatchButton: Button = findViewById(R.id.stopwatchButton)

        // 스톱워치 버튼 클릭 시 TimerActivity로 이동
        stopwatchButton.setOnClickListener {
            val intent = Intent(this, TimerActivity::class.java)
            startActivity(intent)
        }

        // 다른 버튼들도 같은 방식으로 구현 가능
    }
}