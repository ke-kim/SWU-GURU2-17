//package com.example.swu_guru2_17
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
//import android.util.AttributeSet
//import android.view.View
//
//class BarChartView @JvmOverloads constructor(
//    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
//) : View(context, attrs, defStyleAttr) {
//
//    private val barPaint = Paint().apply {
//        color = Color.DKGRAY
//        style = Paint.Style.FILL
//    }
//
//    private val data = listOf(30, 50, 40, 60, 20, 10, 70) // 예제 데이터 (요일별 값)
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//
//        val barWidth = width / (data.size * 2).toFloat()
//        val maxHeight = height * 0.8f
//        val maxDataValue = data.maxOrNull() ?: 1
//
//        data.forEachIndexed { index, value ->
//            val left = index * 2 * barWidth + barWidth / 2
//            val top = height - (value / maxDataValue.toFloat()) * maxHeight
//            val right = left + barWidth
//            val bottom = height.toFloat()
//
//            canvas.drawRect(left, top, right, bottom, barPaint)
//        }
//    }
//}
package com.example.swu_guru2_17

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat

class BarChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val barPaint = Paint().apply {
        color = ContextCompat.getColor(context, R.color.orange_1) // 🔹 orange_1 색상 적용
        style = Paint.Style.FILL
    }

    private val data = listOf(30, 50, 40, 60, 20, 10, 70) // 예제 데이터 (요일별 값)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val barWidth = width / (data.size * 2).toFloat()
        val maxHeight = height * 0.8f
        val maxDataValue = data.maxOrNull() ?: 1

        data.forEachIndexed { index, value ->
            val left = index * 2 * barWidth + barWidth / 2
            val top = height - (value / maxDataValue.toFloat()) * maxHeight
            val right = left + barWidth
            val bottom = height.toFloat()

            canvas.drawRect(left, top, right, bottom, barPaint)
        }
    }
}
