package com.example.swu_guru2_17

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CircleTimerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paintBackground = Paint().apply {
        color = 0xFFE0E0E0.toInt() // 연한 회색
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val paintProgress = Paint().apply {
        color = 0xFF76C7C0.toInt() // 청록색
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
}
