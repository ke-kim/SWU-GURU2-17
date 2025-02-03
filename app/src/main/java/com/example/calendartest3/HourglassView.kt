package com.example.swu_guru2_17

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class HourglassView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paintFilled = Paint().apply {
        color = ContextCompat.getColor(context, R.color.orange_5) // 하단 반원 색상을 orange_5로 변경
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val paintEmpty = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.white) // 빈 공간 색상
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val paintOutline = Paint().apply {
        color = ContextCompat.getColor(context, R.color.gray_3) // 외곽선 색상
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
    }

    private var progress: Float = 0.0f // 0 ~ 1 (모래 채움 퍼센트)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2
        val centerY = height / 2

        val rectTop = RectF(0f, 0f, width, centerY)
        val rectBottom = RectF(0f, centerY, width, height)

        // 1. 외곽선 그리기 (모래시계 형태)
        canvas.drawArc(rectTop, 0f, 180f, true, paintOutline)  // 상단 반원
        canvas.drawArc(rectBottom, 180f, 180f, true, paintOutline)  // 하단 반원

        // 2. 상단 빈 공간 (반 원)
        canvas.drawArc(rectTop, 0f, 180f, true, paintEmpty)

        // 3. 하단 모래 채워지는 부분 (progress 값에 따라 변동)
        val filledHeight = centerY * progress  // 모래 채워지는 높이 조정
        val filledRectBottom = RectF(
            0f, centerY + (centerY - filledHeight), // 상단이 맞물리도록 보정
            width, height
        )
        canvas.drawArc(filledRectBottom, 180f, 180f, true, paintFilled) // paintFilled` 색상 적용
    }

    fun setProgress(value: Float) {
        progress = value
        invalidate()
    }
}
