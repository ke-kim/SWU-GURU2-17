package com.example.swu_guru2_17

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView
import com.example.swu_guru2_17.R

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private val delayMillis: Long = 5000 // 5초

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.baseline_book_24) // 표시할 이미지 아이콘 설정

        Handler().postDelayed({
            val intent = Intent(this, TimerActivity::class.java)
            startActivity(intent)
            finish() // MainActivity 종료
        }, delayMillis)
    }
}
