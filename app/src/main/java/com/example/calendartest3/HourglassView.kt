//package com.example.swu_guru2_17
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Paint
//import android.util.AttributeSet
//import android.view.View
//import androidx.core.content.ContextCompat
//
//class HourglassView @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null
//) : View(context, attrs) {
//
//    private val paintFilled = Paint().apply {
//        color = ContextCompat.getColor(context, R.color.orange_1) // 목표 달성률 색상
//        style = Paint.Style.FILL
//        isAntiAlias = true
//    }
//
//    private val paintEmpty = Paint().apply {
//        color = ContextCompat.getColor(context, android.R.color.white)
//        style = Paint.Style.FILL
//        isAntiAlias = true
//    }
//
//    private var progress: Float = 0.0f // 0 ~ 1 (달성률)
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        val width = width.toFloat()
//        val height = height.toFloat()
//
//        // 상단 빈 부분 (달성되지 않은 영역)
//        canvas.drawArc(0f, 0f, width, height / 2, 0f, 180f, true, paintEmpty)
//
//        // 하단 채워진 부분 (달성된 영역)
//        val filledHeight = (height / 2) * progress
//        canvas.drawArc(0f, height / 2 - filledHeight, width, height, 0f, 180f, true, paintFilled)
//    }
//
//    fun setProgress(value: Float) {
//        progress = value
//        invalidate()
//    }
//}
package com.example.swu_guru2_17

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class HourglassView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paintFilled = Paint().apply {
        color = ContextCompat.getColor(context, R.color.orange_1) // 목표 달성률 색상
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val paintEmpty = Paint().apply {
        color = ContextCompat.getColor(context, android.R.color.white)
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private var progress: Float = 0.0f // 0 ~ 1 (달성률)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()
        val centerX = width / 2
        val centerY = height / 2
        val radius = width / 2

        val pathTop = Path()
        val pathBottom = Path()

        // 상단 빈 부분 (비어있는 부분)
        pathTop.moveTo(centerX, 0f)
        pathTop.lineTo(0f, centerY)
        pathTop.lineTo(width, centerY)
        pathTop.close()
        canvas.drawPath(pathTop, paintEmpty)

        // 하단 채워진 부분 (달성된 부분)
        val filledHeight = centerY * progress
        pathBottom.moveTo(centerX, height)
        pathBottom.lineTo(0f, centerY + filledHeight)
        pathBottom.lineTo(width, centerY + filledHeight)
        pathBottom.close()
        canvas.drawPath(pathBottom, paintFilled)
    }

    fun setProgress(value: Float) {
        progress = value
        invalidate()
    }
}