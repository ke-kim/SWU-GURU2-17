package com.example.swu_guru2_17

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class CircleTimerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paintBackground = Paint().apply {
        color = ContextCompat.getColor(context, R.color.orange_5) // #FFC890
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val paintProgress = Paint().apply {
        color = ContextCompat.getColor(context, R.color.orange_1) // #FF8000
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var progress: Float = 0f // 진행률 (0 ~ 1)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width
        val height = height
        val radius = (minOf(width, height) / 2).toFloat()

        // 원 배경 그리기
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paintBackground)

        // 진행률 그리기
        val sweepAngle = 360 * progress
        canvas.drawArc(
            width / 2 - radius, height / 2 - radius,
            width / 2 + radius, height / 2 + radius,
            -90f, sweepAngle, true, paintProgress
        )
    }

    // 진행률 업데이트
    fun setProgress(progress: Float) {
        this.progress = progress
        invalidate()
    }

    // `RecordActivity.kt`에서도 사용할 수 있도록 TimePeriod 리스트를 받아서 진행률을 설정하는 메서드 추가
    fun setData(timePeriods: List<TimePeriod>) {
        val totalMinutesInDay = 24 * 60 // 하루는 1440분
        var activeMinutes = 0

        val format = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        for (period in timePeriods) {
            try {
                val startTime = format.parse(period.start)?.time ?: 0
                val endTime = format.parse(period.end)?.time ?: 0
                activeMinutes += ((endTime - startTime) / (1000 * 60)).toInt() // 밀리초 -> 분 변환
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 총 활성 시간(분)을 기준으로 진행률 설정
        val progressRatio = activeMinutes.toFloat() / totalMinutesInDay
        setProgress(progressRatio)
        Log.d("CIRCLE_VIEW", "Updated Circle Graph Progress: $progressRatio")
    }
}
